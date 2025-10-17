How to populate uploads volume with repository images

1) Place any image files referenced by seed SQL into `docker/uploads/images/` (keep filenames identical to what's stored in `image_url` columns).

2) Start docker-compose as usual (this creates the named volume `uploads_data`):

```powershell
cd <repo-root>
docker-compose up -d --build
```

3) Run the populate script to copy the files into the volume:

```powershell
# from repository root (PowerShell)
./scripts/populate_uploads_volume.ps1
```

The script creates a temporary container that mounts the `uploads_data` volume and copies files into `/uploads` inside the volume. After it finishes, your backend container (which mounts the same volume to `/app/uploads/images`) will serve the files.

Notes:
- Adjust the script parameter `-volumeName` if your Compose project uses a different volume name (the script defaults to `demo-repository_uploads_data`).
- The `docker/uploads/images` folder is .gitignored for actual image binaries by default; add images intentionally as required.
