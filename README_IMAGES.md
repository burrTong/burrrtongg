# Product Images - Fully Automated Setup ✨# How to Handle Product ImagesHow to populate uploads volume with repository images



## TL;DR - Quick Start



```powershell## Quick Start1) Place any image files referenced by seed SQL into `docker/uploads/images/` (keep filenames identical to what's stored in `image_url` columns).

# Just run this - everything is automatic!

docker-compose down -v

docker-compose up -d --build

```When you reset and rebuild from scratch:2) Start docker-compose as usual (this creates the named volume `uploads_data`):



**Wait ~30-60 seconds**, then everything should work:

- ✅ Default images auto-copied to volume

- ✅ Database initialized with products```powershell```powershell

- ✅ Backend serves images automatically

# 1. Stop and remove everythingcd <repo-root>

**Note**: Backend may need restart if Elasticsearch isn't ready:

```powershelldocker-compose down -vdocker-compose up -d --build

docker-compose restart app

``````



## How It Works# 2. Start fresh



The system now automatically copies default product images when you start containers!docker-compose up -d --build3) Run the populate script to copy the files into the volume:



### New `init-uploads` Service



Added to `docker-compose.yml`:# 3. Copy default images to volume (run this AFTER containers are up)```powershell

```yaml

init-uploads:$containerName = "tmp_copy_to_volume_" + ([System.Guid]::NewGuid().ToString().Substring(0,8))# from repository root (PowerShell)

  image: alpine:3.18

  volumes:docker container create --name $containerName -v "burrrtongg_uploads_data:/uploads" alpine:3.18 sh -c "sleep 3600"./scripts/populate_uploads_volume.ps1

    - uploads_data:/uploads

    - ./docker/uploads/images:/source:rodocker cp ./docker/uploads/images/. "${containerName}:/uploads"```

  command: sh -c "cp -r /source/. /uploads/ && echo 'Default images copied'"

  restart: "no"docker container rm -f $containerName

```

```The script creates a temporary container that mounts the `uploads_data` volume and copies files into `/uploads` inside the volume. After it finishes, your backend container (which mounts the same volume to `/app/uploads/images`) will serve the files.

This service:

1. Runs once when you `docker-compose up`

2. Copies files from `docker/uploads/images/` → volume

3. Exits automatically## How It WorksNotes:

4. Backend depends on it, so images are ready before backend starts

- Adjust the script parameter `-volumeName` if your Compose project uses a different volume name (the script defaults to `demo-repository_uploads_data`).

## What Changed

- **Seed database** (`init.sql`) has products pointing to `/uploads/images/default-product.svg`- The `docker/uploads/images` folder is .gitignored for actual image binaries by default; add images intentionally as required.

**Before** (manual):

```powershell- **Default placeholder** exists in `docker/uploads/images/default-product.svg`

docker-compose up -d- **Copy script** transfers files from repo into Docker volume

# Then manually run populate script- **Backend serves** images from `/app/uploads/images` → accessible via `http://localhost:8080/uploads/images/*`

.\scripts\populate_uploads_volume.ps1

```## Using Real Images



**Now** (automatic):1. Put image files in `docker/uploads/images/`

```powershell2. Update `init.sql` to reference them:

docker-compose up -d   ```sql

# Done! Images are copied automatically   107	My Product	/uploads/images/my-product.jpg	Product Name	1000	42	100	1	34

```   ```

3. Run copy script again

## Default Images

## Alternative: Use PowerShell Script

- **default-product.svg** - Placeholder for seed products

- Stored in `docker/uploads/images/````powershell

- Referenced in `init.sql`:.\scripts\populate_uploads_volume.ps1

  ```sql```

  107  ...  /uploads/images/default-product.svg  Shoe Model 1  ...

  108  ...  /uploads/images/default-product.svg  Shoe Model 2  ...If script execution is blocked:

  115  ...  /uploads/images/default-product.svg  Nike Shoe 1998 Limited Edition  ...```powershell

  ```Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass

.\scripts\populate_uploads_volume.ps1

## Using Custom Images```



Want real product photos instead of placeholders?## Verify Images Are Loaded



1. **Add your images** to `docker/uploads/images/`:```powershell

   ```# Check files in volume

   docker/uploads/images/docker exec backend_app ls -la /app/uploads/images/

   ├── nike-shoe.jpg

   ├── adidas-sneaker.png# Test access

   └── default-product.svg# Open: http://localhost:8080/uploads/images/default-product.svg

   ``````



2. **Update `init.sql`** to reference them:## Important Notes

   ```sql

   115  Description  /uploads/images/nike-shoe.jpg  Nike Shoe 1998  4500  45  20  1  34✅ **Admin uploads work automatically** - Images uploaded via admin panel are saved directly to the volume  

   ```✅ **Volume persists** - Images survive container restarts (unless you do `docker-compose down -v`)  

⚠️ **After DB reset** - Re-run the copy script to restore default images  

3. **Restart**:📁 **Folder**: `docker/uploads/images/` contains placeholder SVG (not .gitignored)  

   ```powershell🔧 **Volume name**: `burrrtongg_uploads_data`

   docker-compose down -v
   docker-compose up -d
   ```

## Verification Commands

```powershell
# Check init-uploads ran successfully
docker logs init_uploads
# Should show: "Default images copied to uploads volume"

# List files in volume
docker exec backend_app ls -la /app/uploads/images/

# Check database
docker exec mydb psql -U myuser -d ecommerce -c "SELECT id, name, image_url FROM products LIMIT 3;"

# Test image access in browser
# http://localhost:8080/uploads/images/default-product.svg
```

## Admin Panel Uploads

✅ **Still works automatically!**  
- Admin uploads are saved directly to `uploads_data` volume  
- No manual intervention needed  
- Images persist across container restarts

## Troubleshooting

### Backend won't start (Elasticsearch connection error)

**Symptom**: Backend exits with "Connection refused" to Elasticsearch

**Solution**: Wait ~20 seconds for Elasticsearch to be ready, then:
```powershell
docker-compose restart app
```

### Images don't appear

1. **Check init-uploads completed**:
   ```powershell
   docker logs init_uploads
   ```
   Should show success message.

2. **Check files in volume**:
   ```powershell
   docker exec backend_app ls /app/uploads/images/
   ```
   Should list `default-product.svg`

3. **Check database paths**:
   ```powershell
   docker exec mydb psql -U myuser -d ecommerce -c "SELECT image_url FROM products;"
   ```
   Should all point to `/uploads/images/default-product.svg`

### Need to re-copy images manually

```powershell
docker run --rm `
  -v burrrtongg_uploads_data:/uploads `
  -v ${PWD}/docker/uploads/images:/source:ro `
  alpine:3.18 sh -c "cp -r /source/. /uploads/ && echo Done"
```

## Legacy Manual Script

The old script still exists but is no longer needed:
```powershell
.\scripts\populate_uploads_volume.ps1
```

Use it only if you need to manually update images without restarting.

## Summary of Changes

| Before | After |
|--------|-------|
| Manual populate script required | ✅ Fully automatic |
| Extra step after `docker-compose up` | ✅ One command: `docker-compose up -d` |
| Easy to forget | ✅ Can't forget - it's built-in |
| Complex for new developers | ✅ Simple - just works |

🎉 **No more manual steps for image setup!**
