# ============================================
# Development Environment Initialization Script
# ============================================
# This script sets up a fresh development environment with baseline data
# 
# Usage: .\scripts\init_dev_environment.ps1
# ============================================

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Development Environment Setup" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Step 1: Stop and remove all containers and volumes
Write-Host "[1/4] Stopping and removing existing containers and volumes..." -ForegroundColor Yellow
docker-compose down -v
if ($LASTEXITCODE -ne 0) {
    Write-Host "Error stopping containers" -ForegroundColor Red
    exit 1
}
Write-Host "✓ Cleanup completed" -ForegroundColor Green
Write-Host ""

# Step 2: Build and start containers
Write-Host "[2/4] Building and starting containers..." -ForegroundColor Yellow
docker-compose up -d --build
if ($LASTEXITCODE -ne 0) {
    Write-Host "Error building containers" -ForegroundColor Red
    exit 1
}
Write-Host "✓ Containers started" -ForegroundColor Green
Write-Host ""

# Step 3: Wait for backend to be ready
Write-Host "[3/4] Waiting for backend to be ready..." -ForegroundColor Yellow
$maxAttempts = 30
$attempt = 0
$ready = $false

while ($attempt -lt $maxAttempts -and -not $ready) {
    $attempt++
    Start-Sleep -Seconds 2
    
    try {
        $logs = docker logs backend_app 2>&1 | Select-String "Started BackendApplication"
        if ($logs) {
            $ready = $true
        } else {
            Write-Host "  Attempt $attempt/$maxAttempts - Backend not ready yet..." -ForegroundColor Gray
        }
    } catch {
        Write-Host "  Attempt $attempt/$maxAttempts - Checking..." -ForegroundColor Gray
    }
}

if ($ready) {
    Write-Host "✓ Backend is ready!" -ForegroundColor Green
} else {
    Write-Host "⚠ Backend might still be starting up. Check logs with: docker logs backend_app" -ForegroundColor Yellow
}
Write-Host ""

# Step 4: Display summary
Write-Host "[4/4] Environment Status" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan
docker-compose ps
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "✓ Development environment is ready!" -ForegroundColor Green
Write-Host ""
Write-Host "Services available at:" -ForegroundColor Cyan
Write-Host "  • Frontend:       http://localhost:5173" -ForegroundColor White
Write-Host "  • Backend API:    http://localhost:8080" -ForegroundColor White
Write-Host "  • PgAdmin:        http://localhost:8081" -ForegroundColor White
Write-Host "  • Elasticsearch:  http://localhost:9200" -ForegroundColor White
Write-Host "  • Kibana:         http://localhost:5601" -ForegroundColor White
Write-Host ""
Write-Host "Database baseline data:" -ForegroundColor Cyan
Write-Host "  • 2 Categories (Nike, Puma)" -ForegroundColor White
Write-Host "  • 2 Users (customer, admin)" -ForegroundColor White
Write-Host "  • 4 Products" -ForegroundColor White
Write-Host "  • 7 Coupons" -ForegroundColor White
Write-Host "  • 3 Orders with 4 Order Items" -ForegroundColor White
Write-Host ""
Write-Host "Default credentials:" -ForegroundColor Cyan
Write-Host "  Customer: customer@customer.com / customer" -ForegroundColor White
Write-Host "  Admin:    admin@admin.com / admin" -ForegroundColor White
Write-Host "========================================" -ForegroundColor Cyan
