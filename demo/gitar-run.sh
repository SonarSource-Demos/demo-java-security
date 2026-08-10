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

cat <<EOF

==> Done. Open the cross-fork PR here (gh CLI is not installed, so this is manual):

    https://github.com/${UPSTREAM_SLUG}/compare/${BASE_BRANCH}...${FORK_OWNER}:${FORK_REPO}:${RUN_BRANCH}?expand=1

    Suggested title: Harden RSA encryption padding and add order reporting lookup

==> Teardown when the demo is over:

    git checkout ${BASE_BRANCH} && git branch -D ${RUN_BRANCH} && git push ${FORK_REMOTE} --delete ${RUN_BRANCH}

EOF
