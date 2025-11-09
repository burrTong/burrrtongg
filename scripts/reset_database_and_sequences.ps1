# Reset Database and Sequences Script
# This script clears all data and resets sequences to start from 1

Write-Host "Clearing database data..." -ForegroundColor Yellow

# Disable foreign key constraints temporarily
docker exec mydb psql -U myuser -d ecommerce -c "SET session_replication_role = 'replica';"

# Delete all data (keep structure)
docker exec mydb psql -U myuser -d ecommerce -c "TRUNCATE TABLE order_items, orders, payments, products, categories, coupons RESTART IDENTITY CASCADE;"

# Re-enable foreign key constraints
docker exec mydb psql -U myuser -d ecommerce -c "SET session_replication_role = 'origin';"

Write-Host "Data cleared!" -ForegroundColor Green

# Reset all sequences to 1
Write-Host "Resetting sequences..." -ForegroundColor Yellow

docker exec mydb psql -U myuser -d ecommerce -c "ALTER SEQUENCE categories_id_seq RESTART WITH 1; ALTER SEQUENCE coupons_id_seq RESTART WITH 1; ALTER SEQUENCE order_items_id_seq RESTART WITH 1; ALTER SEQUENCE orders_id_seq RESTART WITH 1; ALTER SEQUENCE payments_id_seq RESTART WITH 1; ALTER SEQUENCE products_id_seq RESTART WITH 1;"

Write-Host "Sequences reset!" -ForegroundColor Green

# Dump clean database
Write-Host "Dumping clean database..." -ForegroundColor Yellow
docker exec mydb pg_dump -U myuser -d ecommerce --clean --if-exists > ./docker/postgres/initdb/init_clean.sql

Write-Host "Done! Clean init.sql created at docker/postgres/initdb/init_clean.sql" -ForegroundColor Green
Write-Host ""
Write-Host "Note: Users table was NOT cleared (admin and customer accounts still exist)" -ForegroundColor Cyan
