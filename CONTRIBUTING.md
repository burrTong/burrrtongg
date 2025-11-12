# Contributing Guide

## Getting Started

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd burrrtongg
   ```

2. **Start development environment**
   ```powershell
   docker-compose up -d --build
   ```

3. **Verify everything is running**
   - Frontend: http://localhost:5173
   - Backend API: http://localhost:8080
   - PgAdmin: http://localhost:8081

## Development Workflow

### Making Database Changes

**Option 1: Using Flyway (Recommended for Production)**
1. Create a new migration file: `backend/src/main/resources/db/migration/V4__your_description.sql`
2. Write your SQL changes
3. Restart backend: `docker-compose restart app`
4. Flyway will automatically run the new migration
5. Export new baseline: `docker exec mydb pg_dump -U myuser -d ecommerce --clean --if-exists --inserts --column-inserts > docker/postgres/initdb/init.sql`
6. Commit both files

**Option 2: Update init.sql Directly (Quick Changes)**
1. Edit `docker/postgres/initdb/init.sql`
2. Rebuild: `docker-compose down -v && docker-compose up -d --build`
3. Verify changes
4. Commit

### Testing Your Changes

```powershell
# Run all tests
docker-compose exec app ./gradlew test

# Run specific test
docker-compose exec app ./gradlew test --tests "YourTestClass"
```

### Resetting to Baseline

```powershell
# Clean slate
docker-compose down -v
docker-compose up -d --build
```

## Code Style

- **Java**: Follow Spring Boot conventions
- **JavaScript/React**: ESLint configuration in `frontend/burrtong/`
- **SQL**: Use lowercase for keywords, uppercase for table/column names in migrations

## Database Credentials

```
Database: ecommerce
User: myuser
Password: mypass
Host: localhost
Port: 5432
```

**Test Accounts:**
- Customer: `customer@customer.com` / `customer`
- Admin: `admin@admin.com` / `admin`

## Common Issues

### Database won't start
```powershell
# Check logs
docker logs mydb

# Common fix: remove volumes
docker-compose down -v
docker-compose up -d
```

### Backend keeps restarting
```powershell
# Check logs
docker logs backend_app

# Usually schema validation issue - check entity classes match database
```

### Images not showing
```powershell
# Verify images exist
ls docker/uploads/images/

# Images are in bind mount, should persist across restarts
```

## Pull Request Guidelines

1. Create a feature branch: `git checkout -b feature/your-feature`
2. Make your changes
3. Test thoroughly
4. Update documentation if needed
5. Commit with clear message
6. Push and create PR

## Commit Message Format

```
type(scope): brief description

- Detailed change 1
- Detailed change 2

[Optional] Related issue: #123
```

**Types:** feat, fix, docs, style, refactor, test, chore

**Examples:**
- `feat(coupon): add coupon expiration validation`
- `fix(database): resolve sequence synchronization issue`
- `docs(readme): update setup instructions`

## Need Help?

- Check `DEV_SETUP.md` for detailed setup guide
- Check `BASELINE_DATA.md` for database structure
- Check `CHANGELOG.md` for recent changes
