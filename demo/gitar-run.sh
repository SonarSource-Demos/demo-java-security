#!/usr/bin/env bash
#
# Spin up a fresh Gitar demo PR branch.
#
# Topology: the whole demo runs inside the fork -- base AND head. We have no
# write access upstream, and the cross-fork PR we tried first failed twice
# over: Gitar has no installation on the head repo so the PR never appeared in
# its UI, and GitHub held the workflow behind a maintainer's "Approve and run
# workflows" click so CI never produced the failure to analyse. A PR whose base
# and head are both in the fork has neither problem.
#
#   upstream (origin) : SonarSource-Demos/demo-java-security  -- read-only ancestor
#   fork              : $FORK_OWNER/$FORK_REPO                -- base and head
#
# Creates a disposable branch off the fork's main, cherry-picks the permanent
# payload commit onto it, pushes, and opens the PR (or prints the URL to open
# it). Every run produces a brand-new branch, so the PR always starts with zero
# prior bot comments.
#
# One-time setup on the fork, or CI never runs and Gitar has nothing to read:
#   - Actions tab -> enable workflows (forks ship with them disabled)
#   - install the Gitar app on the fork
#
# Usage:
#   demo/gitar-run.sh            # create + push a new run branch
#   demo/gitar-run.sh --dry-run  # do everything locally, skip the push
#
set -euo pipefail

UPSTREAM_REMOTE="${UPSTREAM_REMOTE:-origin}"
FORK_REMOTE="${FORK_REMOTE:-fork}"
FORK_OWNER="${FORK_OWNER:-seanthompson-psse}"
# The fork was created under a different repo name than upstream, so it is
# tracked separately rather than derived from the upstream slug.
FORK_REPO="${FORK_REPO:-demo-java-security-sean}"
BASE_BRANCH="${BASE_BRANCH:-main}"
PAYLOAD_BRANCH="${PAYLOAD_BRANCH:-demo/gitar-payload}"

FORK_SLUG="${FORK_OWNER}/${FORK_REPO}"

DRY_RUN=0
[[ "${1:-}" == "--dry-run" ]] && DRY_RUN=1

cd "$(git rev-parse --show-toplevel)"

# Run branches are disposable and get deleted at teardown. Always return to the
# branch we started on so the next run still has this script in the worktree.
ORIGINAL_BRANCH="$(git rev-parse --abbrev-ref HEAD)"
restore_branch() {
  local current
  current="$(git rev-parse --abbrev-ref HEAD)"
  if [[ "$current" != "$ORIGINAL_BRANCH" ]]; then
    git checkout --quiet "$ORIGINAL_BRANCH" 2>/dev/null || true
  fi
}
trap restore_branch EXIT

# The fork remote is created on first run so a fresh clone works out of the box.
if ! git remote get-url "$FORK_REMOTE" >/dev/null 2>&1; then
  echo "==> Adding '${FORK_REMOTE}' remote -> ${FORK_SLUG}"
  git remote add "$FORK_REMOTE" "https://github.com/${FORK_SLUG}"
fi

RUN_BRANCH="demo/gitar-run-$(date +%m%d-%H%M)"

echo "==> Fetching ${FORK_REMOTE}/${BASE_BRANCH}, ${FORK_REMOTE}/${PAYLOAD_BRANCH}, ${UPSTREAM_REMOTE}/${BASE_BRANCH}"
git fetch --quiet "$FORK_REMOTE" "$BASE_BRANCH" "$PAYLOAD_BRANCH"
git fetch --quiet "$UPSTREAM_REMOTE" "$BASE_BRANCH"

# The payload branch is exactly one commit on top of upstream main. It is
# measured against upstream rather than the fork because the fork's main also
# carries this script, which must stay out of the PR diff.
PAYLOAD_COMMIT="$(git rev-parse "${FORK_REMOTE}/${PAYLOAD_BRANCH}")"
PAYLOAD_COUNT="$(git rev-list --count "${UPSTREAM_REMOTE}/${BASE_BRANCH}..${FORK_REMOTE}/${PAYLOAD_BRANCH}")"
if [[ "$PAYLOAD_COUNT" != "1" ]]; then
  echo "!! Expected exactly 1 commit on ${PAYLOAD_BRANCH} above ${BASE_BRANCH}, found ${PAYLOAD_COUNT}." >&2
  echo "!! Upstream main has probably moved. Rebuild the payload branch:" >&2
  echo "!!   git checkout -B ${PAYLOAD_BRANCH} ${UPSTREAM_REMOTE}/${BASE_BRANCH}" >&2
  echo "!!   git cherry-pick <payload-sha> && git push --force ${FORK_REMOTE} ${PAYLOAD_BRANCH}" >&2
  exit 1
fi

echo "==> Creating ${RUN_BRANCH} from ${FORK_REMOTE}/${BASE_BRANCH}"
git checkout --quiet -B "$RUN_BRANCH" "${FORK_REMOTE}/${BASE_BRANCH}"

echo "==> Cherry-picking payload ${PAYLOAD_COMMIT:0:9}"
if ! git cherry-pick "$PAYLOAD_COMMIT"; then
  echo "!! Cherry-pick failed -- the fork's main has drifted under the payload." >&2
  echo "!! Resolve, then: git cherry-pick --continue   (or --abort to bail out)" >&2
  exit 1
fi

if [[ "$DRY_RUN" == "1" ]]; then
  echo
  echo "==> --dry-run: skipping push. Local branch ${RUN_BRANCH} is ready."
  exit 0
fi

echo "==> Pushing ${RUN_BRANCH} to ${FORK_REMOTE} (${FORK_SLUG})"
git push --quiet --set-upstream "$FORK_REMOTE" "$RUN_BRANCH"

PR_TITLE="Harden RSA encryption padding and add order reporting lookup"
COMPARE_URL="https://github.com/${FORK_SLUG}/compare/${BASE_BRANCH}...${RUN_BRANCH}?expand=1"

# gh CLI is not installed. Export GH_TOKEN to have the PR opened over the REST
# API; otherwise the compare URL is printed and you click "Create pull request".
if [[ -n "${GH_TOKEN:-}" ]]; then
  echo "==> Opening PR via REST API"
  PR_BODY='Moves RSA encryption onto OAEP with SHA-256 and centralises the transformation, adds findOrdersByStatus() for the reporting dashboard, and extracts the shared result-set collection loop.'
  RESPONSE="$(curl -sS -X POST \
    -H "Authorization: Bearer ${GH_TOKEN}" \
    -H "Accept: application/vnd.github+json" \
    "https://api.github.com/repos/${FORK_SLUG}/pulls" \
    -d "{\"title\":\"${PR_TITLE}\",\"head\":\"${RUN_BRANCH}\",\"base\":\"${BASE_BRANCH}\",\"body\":\"${PR_BODY}\"}")"
  PR_URL="$(printf '%s' "$RESPONSE" | grep -o '"html_url": *"[^"]*/pull/[0-9]*"' | head -1 | sed 's/.*"\(https[^"]*\)"/\1/')"
  if [[ -n "$PR_URL" ]]; then
    echo "==> PR opened: ${PR_URL}"
  else
    echo "!! PR creation failed. API said:" >&2
    printf '%s\n' "$RESPONSE" | grep -o '"message": *"[^"]*"' | head -3 >&2
    echo "!! Fall back to: ${COMPARE_URL}" >&2
  fi
else
  cat <<EOF

==> Done. Open the PR here (export GH_TOKEN to have this script do it):

    ${COMPARE_URL}

    Suggested title: ${PR_TITLE}
EOF
fi

cat <<EOF

==> Teardown when the demo is over:

    git push ${FORK_REMOTE} --delete ${RUN_BRANCH}

EOF
