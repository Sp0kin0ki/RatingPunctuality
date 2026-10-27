#!/usr/bin/env bash
set -euo pipefail

BASE="http://localhost:8085/realms/course"
API="http://localhost:8083"
BOOK_ID="22222222-2222-4222-8222-222222222222"

need_cmd() {
  command -v "$1" >/dev/null 2>&1 || {
    echo "Ошибка: требуется команда '$1'." >&2
    exit 1
  }
}

need_cmd curl
need_cmd jq

get_token() {
  local client_id="$1"
  local client_secret="$2"

  curl -fsS -X POST "$BASE/protocol/openid-connect/token" \
    -H 'Content-Type: application/x-www-form-urlencoded' \
    --data-urlencode grant_type=client_credentials \
    --data-urlencode "client_id=$client_id" \
    --data-urlencode "client_secret=$client_secret" \
    | jq -r '.access_token'
}

status_with_bearer() {
  local token="$1"
  local url="$2"

  curl -sS -o /dev/null -w '%{http_code}' \
    -H "Authorization: Bearer $token" \
    "$url"
}

echo "1) Checking OpenID Discovery..."
discovery="$(curl -fsS "$BASE/.well-known/openid-configuration")"

issuer="$(jq -r '.issuer' <<<"$discovery")"

if [[ "$issuer" != "$BASE" ]]; then
  echo "Unexpected issuer: $issuer" >&2
  exit 1
fi

echo "2) Getting service token with Client Credentials..."

service_token="$(get_token \
  course-service-client \
  course-service-secret)"

if [[ -z "$service_token" || "$service_token" == "null" ]]; then
  echo "Empty service access token" >&2
  exit 1
fi

echo "3) Calling recommendations as service..."

service_response="$(
  curl -fsS \
    -H "Authorization: Bearer $service_token" \
    "$API/internal/recommendations/$BOOK_ID"
)"

jq -e \
  --arg id "$BOOK_ID" \
  '.bookId == $id
   and (.titles | index("Clean Code")) != null
   and (.titles | index("Refactoring")) != null' \
  <<<"$service_response" >/dev/null

echo "4) Checking negative cases..."

anonymous_status="$(
  curl -sS \
    -o /dev/null \
    -w '%{http_code}' \
    "$API/internal/recommendations/$BOOK_ID"
)"

if [[ "$anonymous_status" != "401" ]]; then
  echo "Anonymous request: expected 401, got $anonymous_status" >&2
  exit 1
fi

ops_token="$(get_token \
  course-ops-client \
  course-ops-secret)"

ops_status="$(
  status_with_bearer \
    "$ops_token" \
    "$API/internal/recommendations/$BOOK_ID"
)"

if [[ "$ops_status" != "403" ]]; then
  echo "Operator recommendations: expected 403, got $ops_status" >&2
  exit 1
fi

ops_info="$(
  curl -fsS \
    -H "Authorization: Bearer $ops_token" \
    "$API/internal/admin/info"
)"

if [[ "$(jq -r '.status' <<<"$ops_info")" != "ok" ]]; then
  echo "Operator diagnostics did not return status=ok" >&2
  exit 1
fi

service_admin_status="$(
  status_with_bearer \
    "$service_token" \
    "$API/internal/admin/info"
)"

if [[ "$service_admin_status" != "403" ]]; then
  echo "Service admin: expected 403, got $service_admin_status" >&2
  exit 1
fi

other_token="$(get_token \
  course-other-client \
  course-other-secret)"

wrong_audience="$(
  status_with_bearer \
    "$other_token" \
    "$API/internal/recommendations/$BOOK_ID"
)"

if [[ "$wrong_audience" != "401" ]]; then
  echo "Wrong audience: expected 401, got $wrong_audience" >&2
  exit 1
fi

echo "OAuth/JWT verification completed successfully."