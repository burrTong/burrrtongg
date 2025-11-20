# Quick Start Commands for Observability

## 🚀 Start All Services
```bash
docker-compose up -d
```

## 🔍 Check Service Status
```bash
docker-compose ps
```

## 📊 Access UIs

### Grafana (Main Dashboard)
```
URL: http://localhost:3000
Username: admin
Password: admin
```

### Prometheus (Metrics)
```
URL: http://localhost:9090
```

### Backend Metrics Endpoint
```
URL: http://localhost:8080/actuator/prometheus
```

## 🧪 Test Queries

### Prometheus (http://localhost:9090)

1. **Total Requests:**
   ```promql
   http_server_requests_seconds_count
   ```

2. **Average Response Time:**
   ```promql
   rate(http_server_requests_seconds_sum[5m]) / rate(http_server_requests_seconds_count[5m])
   ```

3. **CPU Usage:**
   ```promql
   system_cpu_usage
   ```

### Loki (Grafana > Explore > Loki)

1. **All Backend Logs:**
   ```logql
   {container_name="backend_app"}
   ```

2. **Error Logs Only:**
   ```logql
   {container_name="backend_app"} |= "ERROR"
   ```

3. **Specific Service Logs:**
   ```logql
   {container_name="backend_app"} |= "ProductService"
   ```

## 🎯 Generate Test Data

### Load Testing Script (PowerShell)
```powershell
# Generate traffic to backend
for ($i = 1; $i -le 100; $i++) {
    Invoke-RestMethod -Uri "http://localhost:8080/api/products" -Method Get
    Start-Sleep -Milliseconds 100
}
```

### Bash (Git Bash / WSL)
```bash
# Generate 100 requests
for i in {1..100}; do
  curl -s http://localhost:8080/api/products > /dev/null
  echo "Request $i completed"
  sleep 0.1
done
```

## 📈 Import Pre-built Dashboards

1. Go to Grafana: http://localhost:3000
2. Click **+** > **Import Dashboard**
3. Enter Dashboard ID:
   - **JVM Dashboard:** `4701`
   - **Spring Boot:** `12900`
   - **Loki Logs:** `13639`
4. Select **Prometheus** as data source
5. Click **Import**

## 🔧 Troubleshooting

### Check Logs
```bash
# Backend logs
docker logs backend_app

# Prometheus logs
docker logs prometheus

# Loki logs
docker logs loki

# Promtail logs
docker logs promtail
```

### Verify Prometheus Targets
```
http://localhost:9090/targets
```
- `app-service` should be **UP**

### Test Loki Connection
```bash
# Check Loki is receiving logs
curl http://localhost:3100/ready
```

### Restart Specific Service
```bash
docker-compose restart backend_app
docker-compose restart prometheus
docker-compose restart loki
```

## 🎨 Common Grafana Queries

### Requests per Endpoint
```promql
sum by (uri) (rate(http_server_requests_seconds_count[5m]))
```

### Error Rate by Status Code
```promql
sum by (status) (rate(http_server_requests_seconds_count{status=~"[45].."}[5m]))
```

### Memory Usage Trend
```promql
jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"} * 100
```

### Top 10 Longest Requests
```promql
topk(10, http_server_requests_seconds_max)
```

## 📝 Useful Log Patterns

### Filter by Log Level
```logql
{container_name="backend_app"} |= "ERROR" or "WARN"
```

### Parse JSON Logs (if enabled)
```logql
{container_name="backend_app"} | json | level="ERROR"
```

### Count Errors per Minute
```logql
sum(rate({container_name="backend_app"} |= "ERROR" [1m]))
```

### Extract Values from Logs
```logql
{container_name="backend_app"} 
  |= "Order created" 
  | regexp "Order created: (?P<orderId>\\d+)"
```

## 🎓 Learning Resources

- **Prometheus Tutorial:** https://prometheus.io/docs/prometheus/latest/getting_started/
- **LogQL Basics:** https://grafana.com/docs/loki/latest/query/
- **Grafana Tutorials:** https://grafana.com/tutorials/
- **Pre-built Dashboards:** https://grafana.com/grafana/dashboards/
