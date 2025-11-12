# Development Environment Setup Guide

This guide helps developers set up the e-commerce application with baseline data for development.

## Quick Start

### One-Command Setup ✨
```powershell
docker-compose up -d --build
```

That's it! Everything will be ready in ~30 seconds.

### What Happens Automatically:
1. ✅ PostgreSQL starts and runs `init.sql` (baseline data)
2. ✅ Flyway recognizes existing migrations (no checksum errors)
3. ✅ Backend validates schema and starts
4. ✅ Frontend, Elasticsearch, Kibana, PgAdmin all start
5. ✅ Product images are mounted from local directory

---

## Alternative: Use Init Script
```powershell
.\scripts\init_dev_environment.ps1
```

This script will:
1. Stop and remove all existing containers and volumes
2. Build and start fresh containers
3. Wait for backend to be ready
4. Display environment status

### Option 2: Manual Setup
```powershell
# Clean up existing environment
docker-compose down -v

# Build and start containers
docker-compose up -d --build

# Check status
docker-compose ps
```

## Baseline Database Data

The development environment includes the following baseline data:

### Categories (2)
- **Nike** (id: 1)
- **Puma** (id: 2)

### Users (2)
- **Customer**: customer@customer.com / customer (role: CUSTOMER)
- **Admin**: admin@admin.com / admin (role: ADMIN)

### Products (4)
1. Nike Air Force1 - 3500 THB
2. Nike Shoe 1998 Limited Edition - 2000 THB
3. Puma Shoe Alpha - 1800 THB
4. Puma X Travis - 1800 THB

### Coupons (7)
| Code | Type | Value | Expiration | Active | Special Conditions |
|------|------|-------|------------|--------|-------------------|
| FIXED10 | FIXED | 10 | 2025-11-30 | ✓ | - |
| PER20 | PERCENTAGE | 20% | 2025-11-30 | ✓ | Used 2 times |
| TESTACT | FIXED | 60 | 2025-11-30 | ✗ | Inactive |
| TESTMIN | FIXED | 2500 | 2025-11-30 | ✓ | Min purchase 3000 |
| CLOSE7 | FIXED | 80 | 2025-11-16 | ✓ | Expires soon |
| TESTLIM | FIXED | 80 | 2025-11-30 | ✓ | Max uses 1 (used 1) |
| TESTDATE | FIXED | 1000 | 2025-11-06 | ✓ | Expired |

### Orders (3)
- 3 sample orders with 4 order items total
- Mix of DELIVERED, PENDING, and CANCELED statuses
- Some orders use coupons

## Services Access

| Service | URL | Credentials |
|---------|-----|-------------|
| Frontend | http://localhost:5173 | - |
| Backend API | http://localhost:8080 | - |
| PgAdmin | http://localhost:8081 | admin@admin.com / admin |
| Elasticsearch | http://localhost:9200 | - |
| Kibana | http://localhost:5601 | - |

## Database Connection

```
Host: localhost
Port: 5432
Database: ecommerce
Username: myuser
Password: mypass
```

## Product Images

Product images are stored in `docker/uploads/images/` and mounted to the backend container.

The following images are included:
- nike-air-force1.png
- nike-shoe-1998-limited.jpg
- puma-shoe-alpha.png
- puma-x-travis.jpg
- default-product.svg

**Important**: Images are preserved using bind mount. They will NOT be deleted when running `docker-compose down -v`.

## Database Migrations

The project uses Flyway for database migrations. Migrations are located in:
```
backend/src/main/resources/db/migration/
  ├── V1__init_schema.sql    (Create tables)
  ├── V2__seed_data.sql      (Initial data)
  └── V3__add_coupons.sql    (Coupons table and data)
```

## Resetting to Baseline

To reset the environment to baseline state:
```powershell
.\scripts\init_dev_environment.ps1
```

This will recreate all containers and restore the database to the baseline state.

## Troubleshooting

### Backend not starting
Check logs:
```powershell
docker logs backend_app
```

### Database connection issues
Check database logs:
```powershell
docker logs mydb
```

### Frontend cannot connect to backend
Ensure backend is running:
```powershell
docker ps
```

### Images not displaying
Images are stored in `docker/uploads/images/`. Verify files exist:
```powershell
docker exec backend_app ls -la /app/uploads/images/
```

## Development Workflow

1. Start environment: `.\scripts\init_dev_environment.ps1`
2. Make code changes
3. Test changes
4. Restart specific service if needed:
   ```powershell
   docker-compose restart app      # Restart backend
   docker-compose restart frontend # Restart frontend
   ```
5. Reset to baseline when needed: `.\scripts\init_dev_environment.ps1`

## Notes

- All SEQUENCE values are automatically synchronized with the baseline data
- The init.sql file in `docker/postgres/initdb/init.sql` contains the complete baseline dump
- Flyway migrations are the source of truth for schema and initial data
- Product images persist across container rebuilds due to bind mount configuration
