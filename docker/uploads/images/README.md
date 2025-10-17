Put any image files referenced by seed data here (e.g. files whose image_url values are like `/uploads/images/…`).

When you want to populate the running Docker volume `uploads_data` with these files, run the provided PowerShell script `scripts/populate_uploads_volume.ps1` from the repository root.

Keep filenames unchanged so existing seed SQL that references `/uploads/images/<name>` will match the files placed into the uploads volume.
