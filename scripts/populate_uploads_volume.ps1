<#
Script: populate_uploads_volume.ps1
Purpose: copy files from repo's docker/uploads/images into the Docker named volume `demo-repository_uploads_data`
Notes:
- On Windows, Docker named volumes are stored in a VM, so the script uses a temporary container to copy files into the volume.
- Update `$volumeName` if your Compose project creates a different volume name.
#>

param(
    [string]$volumeName = "demo-repository_uploads_data",
    [string]$sourceDir = "./docker/uploads/images"
)

# Validate source
if (-not (Test-Path $sourceDir)) {
    Write-Error "Source directory '$sourceDir' not found."
    exit 1
}

# Ensure files exist
$files = Get-ChildItem -Path $sourceDir -File -Recurse
if ($files.Count -eq 0) {
    Write-Warning "No files found in $sourceDir. Nothing to copy."
    exit 0
}

# Create a temporary container with the volume mounted, then copy files into the volume
$containerName = "_tmp_copy_to_volume_" + ([System.Guid]::NewGuid().ToString().Substring(0,8))

# Create a minimal container that mounts the target volume at /uploads
Write-Host "Creating temporary container '$containerName' and mounting volume '$volumeName'..."
docker container create --name $containerName -v "${volumeName}:/uploads" alpine:3.18 sh -c "sleep 3600" | Out-Null

try {
    Write-Host "Copying files into container..."
    # Use docker cp to copy from host into container's /uploads
    docker cp $sourceDir/. $containerName:/uploads

    Write-Host "Files copied to volume successfully. Removing temporary container..."
} finally {
    docker container rm -f $containerName | Out-Null
}

Write-Host "Done. The files from '$sourceDir' are now in the Docker volume '$volumeName' (path /uploads)."