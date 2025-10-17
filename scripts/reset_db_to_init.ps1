<#
Reset DB to run init.sql on a clean data dir.
- Loads .env (if present) to get POSTGRES_* values
- Backs up DB to backup_ecommerce.sql if the DB container is running
- Stops docker-compose and removes Postgres volume
- Brings docker-compose up --build -d
- Waits for Postgres to be ready
- Optionally populates uploads volume using scripts/populate_uploads_volume.ps1

Usage:
    # Dry run (just show what would happen):
    .\scripts\reset_db_to_init.ps1 -WhatIf

    # Actual run (may require elevated privileges):
    .\scripts\reset_db_to_init.ps1

Parameters:
-VolumeName: the Docker volume name for Postgres data (default: demo-repository_postgres_data)
-PopulateUploads: switch to call the uploads populate script after bringing services up
-UploadsVolumeName: name of uploads volume (default: demo-repository_uploads_data)
-ComposeFile: path to docker-compose.yml (default: ./docker-compose.yml)
#>
param(
    [string]$VolumeName = "demo-repository_postgres_data",
    [switch]$PopulateUploads,
    [string]$UploadsVolumeName = "demo-repository_uploads_data",
    [string]$ComposeFile = "./docker-compose.yml"
)

function Load-DotEnv([string]$path) {
    if (Test-Path $path) {
        Get-Content $path | ForEach-Object {
            if ($_ -match '^\s*#') { return }
            if ($_ -match '^(\w+)=(.*)$') {
                $k = $matches[1]
                $v = $matches[2]
                # remove surrounding quotes
                if ($v -match '^(?:\"|\')(.+)(?:\"|\')$') { $v = $matches[1] }
                $env:$k = $v
            }
        }
        Write-Host "Loaded .env"
    }
}

Push-Location (Resolve-Path .)

# Load .env if exists
Load-DotEnv "./.env"

# Defaults if .env not provided
if (-not $env:POSTGRES_DB) { $env:POSTGRES_DB = 'ecommerce' }
if (-not $env:POSTGRES_USER) { $env:POSTGRES_USER = 'myuser' }
if (-not $env:POSTGRES_PASSWORD) { $env:POSTGRES_PASSWORD = 'mypass' }
if (-not $env:POSTGRES_PORT) { $env:POSTGRES_PORT = '5432' }

Write-Host "Using POSTGRES_DB=$($env:POSTGRES_DB), POSTGRES_USER=$($env:POSTGRES_USER), volume=$VolumeName"

# Helper to run a command and echo
function Run-Command([string]$cmd) {
    Write-Host "> $cmd"
    & powershell -NoProfile -Command $cmd
}

# If db container exists and running, back it up
$dbContainer = (docker ps --filter "name=mydb" --format "{{.ID}}")
if (-not $dbContainer) {
    Write-Host "DB container 'mydb' is not running (or not present). Skipping pg_dump backup."
} else {
    Write-Host "Backing up DB from container 'mydb' to backup_ecommerce.sql..."
    try {
        # Run pg_dump inside container and capture output to a file on host
        docker exec -i mydb pg_dump -U $env:POSTGRES_USER -d $env:POSTGRES_DB > backup_ecommerce.sql
        Write-Host "Backup written to backup_ecommerce.sql"
    } catch {
        Write-Warning "Failed to run pg_dump inside container 'mydb': $_"
    }
}

# Stop compose and remove postgres volume
Write-Host "Stopping docker-compose and removing Postgres volume: $VolumeName"
if (Test-Path $ComposeFile) {
    docker-compose -f $ComposeFile down
} else {
    docker-compose down
}

# Remove volume if exists
$volumeExists = (docker volume ls --format '{{.Name}}' | Select-String -Pattern "^$VolumeName$")
if ($volumeExists) {
    Write-Host "Removing volume $VolumeName"
    docker volume rm $VolumeName
} else {
    Write-Host "Volume $VolumeName not found (nothing to remove)"
}

# Start compose
Write-Host "Starting docker-compose up --build -d"
if (Test-Path $ComposeFile) {
    docker-compose -f $ComposeFile up --build -d
} else {
    docker-compose up --build -d
}

# Wait for Postgres to be ready (use pg_isready inside container 'mydb')
Write-Host "Waiting for Postgres (container 'mydb') to be ready..."
$maxRetries = 60
$retry = 0
while ($retry -lt $maxRetries) {
    Start-Sleep -Seconds 2
    $isReady = $false
    try {
        $status = docker exec mydb pg_isready -U $env:POSTGRES_USER -d $env:POSTGRES_DB 2>&1 | Out-String
        if ($status -match "accepting connections") { $isReady = $true }
    } catch {
        # ignore
    }
    if ($isReady) { break }
    $retry++
    Write-Host "Waiting... ($retry/$maxRetries)"
}

if ($retry -ge $maxRetries) {
    Write-Warning "Postgres did not become ready within the expected time. Check 'docker logs mydb'."
    Pop-Location
    exit 1
}

Write-Host "Postgres is ready. If ./docker/postgres/initdb/init.sql exists and the Postgres data dir was empty, init.sql will have been executed."

if ($PopulateUploads) {
    Write-Host "Populating uploads volume $UploadsVolumeName..."
    & powershell -NoProfile -Command "Start-Process powershell -ArgumentList '-NoProfile','-ExecutionPolicy','Bypass','-File','./scripts/populate_uploads_volume.ps1','-volumeName','$UploadsVolumeName'" -Wait
}

Write-Host "Reset complete."
Pop-Location
