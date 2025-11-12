# Baseline Database Summary

This file documents the baseline data that is initialized in the development database.

**Last Updated:** November 13, 2025  
**Database Dump:** `docker/postgres/initdb/init.sql`

## Database Statistics

| Table | Records | Max ID | Sequence |
|-------|---------|--------|----------|
| categories | 2 | 2 | 2 |
| users | 2 | 2 | 2 |
| products | 4 | 4 | 4 |
| coupons | 7 | 7 | 7 |
| orders | 3 | 3 | 3 |
| order_items | 4 | 4 | 4 |
| payments | 0 | - | - |

## Detailed Data

### Categories
```sql
id | name
---+------
1  | Nike
2  | Puma
```

### Users
```sql
user_id | username               | role      | password (plain)
--------|------------------------|-----------|------------------
1       | customer@customer.com  | CUSTOMER  | customer
2       | admin@admin.com        | ADMIN     | admin
```

### Products
```sql
id | name                           | price | size | stock | category_id | seller_id | image_url
---|--------------------------------|-------|------|-------|-------------|-----------|---------------------------
1  | Nike Air Force1                | 3500  | 42   | 5     | 1 (Nike)    | 2 (admin) | /uploads/images/nike-air-force1.png
2  | Nike Shoe 1998 Limited Edition | 2000  | 42   | 10    | 1 (Nike)    | 2 (admin) | /uploads/images/nike-shoe-1998-limited.jpg
3  | Puma Shoe Alpha                | 1800  | 39   | 0     | 2 (Puma)    | 2 (admin) | /uploads/images/puma-shoe-alpha.png
4  | Puma X Travis                  | 1800  | 39   | 15    | 2 (Puma)    | 2 (admin) | /uploads/images/puma-x-travis.jpg
```

### Coupons
```sql
id | code     | type       | value | expiration | max_uses | times_used | min_purchase | active
---|----------|------------|-------|------------|----------|------------|--------------|-------
1  | FIXED10  | FIXED      | 10    | 2025-11-30 | NULL     | 0          | NULL         | true
2  | PER20    | PERCENTAGE | 20    | 2025-11-30 | NULL     | 2          | NULL         | true
3  | TESTACT  | FIXED      | 60    | 2025-11-30 | NULL     | 0          | NULL         | false
4  | TESTMIN  | FIXED      | 2500  | 2025-11-30 | NULL     | 0          | 3000         | true
5  | CLOSE7   | FIXED      | 80    | 2025-11-16 | NULL     | 0          | NULL         | true
6  | TESTLIM  | FIXED      | 80    | 2025-11-30 | 1        | 1          | NULL         | true
7  | TESTDATE | FIXED      | 1000  | 2025-11-06 | NULL     | 0          | NULL         | true
```

**Coupon Test Scenarios:**
- `FIXED10`: Basic fixed discount
- `PER20`: Percentage discount (has been used 2 times)
- `TESTACT`: Inactive coupon (should not be applicable)
- `TESTMIN`: Requires minimum purchase of 3000 THB
- `CLOSE7`: Expires on 2025-11-16 (soon)
- `TESTLIM`: Limited to 1 use (already used, should be expired)
- `TESTDATE`: Expired on 2025-11-06 (should not be applicable)

### Orders
```sql
id | order_date           | status    | total_price | customer_id | coupon_id
---|----------------------|-----------|-------------|-------------|----------
1  | 2025-11-12 18:58:37  | CANCELED  | 3600        | 1           | NULL
2  | 2025-11-12 18:59:15  | DELIVERED | 1440        | 1           | 2 (PER20)
3  | 2025-11-12 19:00:45  | PENDING   | 5400        | 1           | NULL
```

### Order Items
```sql
id | order_id | product_id | quantity | price
---|----------|------------|----------|------
1  | 1        | 3          | 1        | 1800
2  | 1        | 4          | 1        | 1800
3  | 2        | 3          | 1        | 1800
4  | 3        | 4          | 3        | 1800
```

## Image Files

Location: `docker/uploads/images/`

```
nike-air-force1.png           1.2 MB
nike-shoe-1998-limited.jpg    5 KB
puma-shoe-alpha.png           19 KB
puma-x-travis.jpg             76 KB
default-product.svg           ~1 KB
```

## How to Update Baseline

When you need to update the baseline data:

1. **Make changes in the running database** (via application, pgAdmin, or SQL)

2. **Export the current database:**
   ```powershell
   docker exec mydb pg_dump -U myuser -d ecommerce --clean --if-exists --inserts --column-inserts > docker/postgres/initdb/init.sql
   ```

3. **Verify the export:**
   ```powershell
   Get-Content docker/postgres/initdb/init.sql | Select-String -Pattern "INSERT INTO"
   ```

4. **Update this documentation** with the new data summary

5. **Test the baseline:**
   ```powershell
   .\scripts\init_dev_environment.ps1
   ```

6. **Commit changes:**
   ```bash
   git add docker/postgres/initdb/init.sql BASELINE_DATA.md
   git commit -m "Update baseline database with [description]"
   ```

## Notes

- All passwords are hashed using BCrypt
- SEQUENCE values are automatically set to match the MAX ID of each table
- The `flyway_schema_history` table is also included in the dump
- Product images must exist in `docker/uploads/images/` directory
- The baseline includes realistic test data for various scenarios (expired coupons, used coupons, different order statuses, etc.)
