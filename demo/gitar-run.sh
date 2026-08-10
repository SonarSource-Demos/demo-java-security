#!/usr/bin/env bash
#
# Spin up a fresh Gitar demo PR branch.
#
# Topology: we don't have write access to the upstream repo, so run branches
# live on a fork and the PR is opened cross-fork into upstream main.
#
#   upstream (origin) : SonarSource-Demos/demo-java-security  -- base, read-only
#   fork              : $FORK_OWNER/demo-java-security        -- we push here
#
# Creates a disposable branch off upstream main, cherry-picks the permanent
# payload commit onto it, pushes it to the fork, and prints the URL to open
# the PR. Every run produces a brand-new branch, so the PR always starts with
# zero prior bot comments.
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

DRY_RUN=0
[[ "${1:-}" == "--dry-run" ]] && DRY_RUN=1

cd "$(git rev-parse --show-toplevel)"

# Run branches are cut from upstream main, which does NOT contain this script.
# Always return to the branch we started on, or the next run can't find us.
ORIGINAL_BRANCH="$(git rev-parse --abbrev-ref HEAD)"
restore_branch() {
  local current
  current="$(git rev-parse --abbrev-ref HEAD)"
  if [[ "$current" != "$ORIGINAL_BRANCH" ]]; then
    git checkout --quiet "$ORIGINAL_BRANCH" 2>/dev/null || true
  fi
}
trap restore_branch EXIT

# Upstream slug, e.g. SonarSource-Demos/demo-java-security
UPSTREAM_URL="$(git remote get-url "$UPSTREAM_REMOTE")"
UPSTREAM_SLUG="$(printf '%s' "$UPSTREAM_URL" | sed -E 's#^git@[^:]+:##; s#^https?://[^/]+/##; s#\.git$##')"
REPO_NAME="${UPSTREAM_SLUG##*/}"

# The fork remote is created on first run so a fresh clone works out of the box.
if ! git remote get-url "$FORK_REMOTE" >/dev/null 2>&1; then
  echo "==> Adding '${FORK_REMOTE}' remote -> ${FORK_OWNER}/${FORK_REPO}"
  git remote add "$FORK_REMOTE" "https://github.com/${FORK_OWNER}/${FORK_REPO}"
fi

RUN_BRANCH="demo/gitar-run-$(date +%m%d-%H%M)"

echo "==> Fetching ${UPSTREAM_REMOTE}/${BASE_BRANCH} and ${FORK_REMOTE}/${PAYLOAD_BRANCH}"
git fetch --quiet "$UPSTREAM_REMOTE" "$BASE_BRANCH"
git fetch --quiet "$FORK_REMOTE" "$PAYLOAD_BRANCH"

# The payload branch is exactly one commit on top of upstream main.
PAYLOAD_COMMIT="$(git rev-parse "${FORK_REMOTE}/${PAYLOAD_BRANCH}")"
PAYLOAD_COUNT="$(git rev-list --count "${UPSTREAM_REMOTE}/${BASE_BRANCH}..${FORK_REMOTE}/${PAYLOAD_BRANCH}")"
if [[ "$PAYLOAD_COUNT" != "1" ]]; then
  echo "!! Expected exactly 1 commit on ${PAYLOAD_BRANCH} above ${BASE_BRANCH}, found ${PAYLOAD_COUNT}." >&2
  echo "!! Upstream main has probably moved. Rebuild the payload branch:" >&2
  echo "!!   git checkout -B ${PAYLOAD_BRANCH} ${UPSTREAM_REMOTE}/${BASE_BRANCH}" >&2
  echo "!!   git cherry-pick <payload-sha> && git push --force ${FORK_REMOTE} ${PAYLOAD_BRANCH}" >&2
  exit 1
fi

echo "==> Creating ${RUN_BRANCH} from ${UPSTREAM_REMOTE}/${BASE_BRANCH}"
git checkout --quiet -B "$RUN_BRANCH" "${UPSTREAM_REMOTE}/${BASE_BRANCH}"

echo "==> Cherry-picking payload ${PAYLOAD_COMMIT:0:9}"
if ! git cherry-pick "$PAYLOAD_COMMIT"; then
  echo "!! Cherry-pick failed -- upstream main has drifted under the payload." >&2
  echo "!! Resolve, then: git cherry-pick --continue   (or --abort to bail out)" >&2
  exit 1
fi

if [[ "$DRY_RUN" == "1" ]]; then
  echo
  echo "==> --dry-run: skipping push. Local branch ${RUN_BRANCH} is ready."
  exit 0
fi

echo "==> Pushing ${RUN_BRANCH} to ${FORK_REMOTE} (${FORK_OWNER}/${FORK_REPO})"
git push --quiet --set-upstream "$FORK_REMOTE" "$RUN_BRANCH"

PR_TITLE="Harden RSA encryption padding and add order reporting lookup"
COMPARE_URL="https://github.com/${UPSTREAM_SLUG}/compare/${BASE_BRANCH}...${FORK_OWNER}:${FORK_REPO}:${RUN_BRANCH}?expand=1"

# gh CLI is not installed. Export GH_TOKEN to have the PR opened over the REST
# API; otherwise the compare URL is printed and you click "Create pull request".
if [[ -n "${GH_TOKEN:-}" ]]; then
  echo "==> Opening PR via REST API"
  PR_BODY='Moves RSA encryption onto OAEP with SHA-256 and centralises the transformation, adds findOrdersByStatus() for the reporting dashboard, and extracts the shared result-set collection loop.'
  RESPONSE="$(curl -sS -X POST \
    -H "Authorization: Bearer ${GH_TOKEN}" \
    -H "Accept: application/vnd.github+json" \
    "https://api.github.com/repos/${UPSTREAM_SLUG}/pulls" \
    -d "{\"title\":\"${PR_TITLE}\",\"head\":\"${FORK_OWNER}:${RUN_BRANCH}\",\"base\":\"${BASE_BRANCH}\",\"body\":\"${PR_BODY}\"}")"
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

==> Done. Open the cross-fork PR here (export GH_TOKEN to have this script do it):

    ${COMPARE_URL}

    Suggested title: ${PR_TITLE}
EOF
fi

cat <<EOF

==> Teardown when the demo is over:

    git checkout ${BASE_BRANCH} && git branch -D ${RUN_BRANCH} && git push ${FORK_REMOTE} --delete ${RUN_BRANCH}

EOF
