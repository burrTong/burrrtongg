# CI / E2E guide

This repository includes a GitHub Actions workflow at `.github/workflows/ci-e2e.yml` that builds the backend and frontend using `docker compose`, starts the services, waits for readiness and runs Cypress E2E tests.

What the workflow does
- Builds `backend` and `frontend/burrtong` images via `docker compose build`
- Starts `db` (Postgres), waits for it to be ready, then starts `app` (backend) and `frontend` containers
- Waits for backend health endpoint
- Runs Cypress (`npx cypress run`) from the runner in `frontend/burrtong`
- Uploads Cypress artifacts (screenshots/videos) as workflow artifacts

Required repository Secrets (recommended)
- `POSTGRES_DB` (default: `demo_db`)
- `POSTGRES_USER` (default: `demo_user`)
- `POSTGRES_PASSWORD` (default: `demo_pass`) — set this in Secrets for real CI
- `POSTGRES_PORT` (default: `5432`)
- `BACKEND_APP_PORT` (default: `8080`)
- `FRONTEND_APP_PORT` (default: `3000`)
- `PGADMIN_EXTERNAL_PORT` (default: `5050`)
- `PGADMIN_DEFAULT_PASSWORD` (default: `pgadmin`)

Local reproduction (macOS / zsh)

1) Create `.env` in repository root (workflow does this on CI automatically):

```bash
cat > .env <<EOF
POSTGRES_DB=demo_db
POSTGRES_USER=demo_user
POSTGRES_PASSWORD=demo_pass
POSTGRES_PORT=5432
BACKEND_APP_PORT=8080
FRONTEND_APP_PORT=3000
PGADMIN_EXTERNAL_PORT=5050
PGADMIN_INTERNAL_PORT=80
PGADMIN_DEFAULT_PASSWORD=pgadmin
EOF
```

2) Build and start services:

```bash
docker compose build --parallel
docker compose up -d db
# wait until Postgres is ready (or use a small retry loop)
docker compose up -d app frontend
```

3) Run Cypress from your machine (host -> frontend exposed on `FRONTEND_APP_PORT`):

```bash
cd frontend/burrtong
npm ci
npx cypress open   # for interactive debugging
# or
npx cypress run --config baseUrl=http://localhost:3000
```

Troubleshooting
- If backend never becomes healthy, check logs:

```bash
docker compose logs -f app
```
- If Postgres can't be reached, confirm `.env` values match docker-compose and retry:

```bash
docker compose exec db pg_isready -U $POSTGRES_USER
```
- On GitHub Actions, if `docker compose exec -T db pg_isready` fails because of missing tools, consider adding a small waiting script or rely on backend retry logic.

Next steps / improvements you may want
- Run Cypress inside the `frontend` container instead of the runner (requires container to have Node/Cypress installed or building a test image).
- Cache docker layers and Node dependencies to speed CI.
- Upload Cypress test reports (JUnit/HTML) and fail the job when e2e fails (currently artifacts are uploaded and job will fail if waiting fails or backend not ready).

If you want, I can:
- convert the workflow to run Cypress inside the `frontend` container
- wire real secrets into the workflow and show example `Settings -> Secrets` entries
- add caching or parallelization for faster runs
