# 📊 Observability Stack - Quick Reference

## 🎯 What You Have

```
┌─────────────┐
│   Grafana   │ ← Visualization (All-in-one UI)
│   :3000     │
└──────┬──────┘
       │
   ┌───┴───┬────────┬─────────┐
   ▼       ▼        ▼         ▼
┌────────┐ ┌──────┐ ┌──────┐ ┌─────────┐
│Prometh-│ │ Loki │ │Tempo │ │Promtail │
│ eus    │ │      │ │      │ │(scraper)│
│:9090   │ │:3100 │ │:3200 │ └─────────┘
└────────┘ └──────┘ └──────┘
    ▲         ▲        ▲
    │         │        │
    └─────────┴────────┴──────┐
                               │
                        ┌──────┴────────┐
                        │ Backend :8080 │
                        │ /actuator     │
                        └───────────────┘
```

## 🚀 เริ่มต้นใช้งาน

### 1. Start Services
```bash
docker-compose up -d
```

### 2. เข้าใช้งาน Grafana
```
URL: http://localhost:3000
User: admin
Pass: admin
```

### 3. ดู Metrics (Prometheus)
- เปิด **Explore** > เลือก **Prometheus**
- Query: `http_server_requests_seconds_count`

### 4. ดู Logs (Loki)
- เปิด **Explore** > เลือก **Loki**
- Query: `{container_name="backend_app"}`

### 5. ดู Dashboard
- ไปที่ **Dashboards** > **Backend Application Monitoring**

---

## 📝 ตัวอย่าง Queries ที่ใช้บ่อย

### Prometheus (Metrics)

```promql
# 1. Request rate per second
rate(http_server_requests_seconds_count[1m])

# 2. Average response time
rate(http_server_requests_seconds_sum[5m]) / 
rate(http_server_requests_seconds_count[5m])

# 3. Error rate (%)
sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m])) /
sum(rate(http_server_requests_seconds_count[5m])) * 100

# 4. CPU usage
system_cpu_usage * 100

# 5. Memory used
jvm_memory_used_bytes{area="heap"}

# 6. Top 5 slowest endpoints
topk(5, avg by (uri) (
  rate(http_server_requests_seconds_sum[5m]) / 
  rate(http_server_requests_seconds_count[5m])
))
```

### Loki (Logs)

```logql
# 1. All backend logs
{container_name="backend_app"}

# 2. Error logs only
{container_name="backend_app"} |= "ERROR"

# 3. Specific service logs
{container_name="backend_app"} |= "OrderService"

# 4. Filter multiple keywords
{container_name="backend_app"} |= "ERROR" |= "database"

# 5. Count errors per minute
sum(rate({container_name="backend_app"} |= "ERROR" [1m]))

# 6. Logs from specific endpoint
{container_name="backend_app"} |= "/api/products"
```

---

## 🧪 Generate Test Data

### PowerShell
```powershell
# Send 100 requests
for ($i = 1; $i -le 100; $i++) {
    Invoke-RestMethod "http://localhost:8080/api/products"
    Start-Sleep -Milliseconds 100
}
```

### Bash
```bash
for i in {1..100}; do
  curl http://localhost:8080/api/products
  sleep 0.1
done
```

---

## 📊 Dashboard Panels

Dashboard อัตโนมัติมี panels:

1. **Request Rate** - requests per second
2. **Response Time** - average response time
3. **Error Rate** - percentage of 5xx errors
4. **CPU Usage** - system CPU usage
5. **Memory Usage** - JVM heap memory
6. **Slowest Endpoints** - top 5 slow endpoints
7. **Live Logs** - streaming logs

---

## 🔍 Common Use Cases

### Case 1: API ช้า
1. ดู **Response Time** panel
2. หา endpoint ที่ช้าใน **Slowest Endpoints**
3. ดู logs: `{container_name="backend_app"} |= "uri_นั้น"`

### Case 2: มี Errors เยอะ
1. ดู **Error Rate** panel
2. Query: `{container_name="backend_app"} |= "ERROR"`
3. ดูว่า error มาจาก service ไหน

### Case 3: Memory Leak
1. ดู **Memory Usage** graph
2. ถ้าเห็น memory เพิ่มขึ้นเรื่อยๆ
3. Query: `jvm_memory_used_bytes{area="heap"}`

### Case 4: High Load
1. ดู **Request Rate** และ **CPU Usage**
2. Check ว่า endpoint ไหนถูกเรียกบ่อย
3. Optimize endpoint นั้น

---

## 🎨 Import Pre-built Dashboards

Grafana มี dashboards สำเร็จรูป:

1. ไปที่ **+ > Import Dashboard**
2. ใส่ Dashboard ID:
   - **JVM (Micrometer):** `4701`
   - **Spring Boot 2.1:** `12900`
   - **Loki Dashboard:** `13639`

3. เลือก **Prometheus** เป็น datasource
4. Click **Import**

---

## ⚡ Quick Tips

### Prometheus
- `rate()` = ความเร็วของ counter
- `histogram_quantile()` = percentiles (p50, p95, p99)
- `topk(n, query)` = top N values

### Loki
- `|=` = contains text
- `!=` = not contains
- `|~ "regex"` = match regex
- Use `|= "ERROR"` แทน `| level="ERROR"` (เร็วกว่า)

### Grafana
- **Ctrl+K** = Quick search
- **Shift+Click** time = Zoom in
- **Double Click** = Reset zoom
- สร้าง **Variables** ให้ dashboard มี dropdown filter

---

## 🆘 Troubleshooting

### ดู Service Logs
```bash
docker logs backend_app
docker logs prometheus
docker logs loki
docker logs promtail
```

### Check Prometheus Targets
```
http://localhost:9090/targets
```
ต้องเห็น **app-service** เป็น **UP**

### Test Loki
```bash
curl http://localhost:3100/ready
# Should return: ready
```

### Restart Services
```bash
docker-compose restart backend_app
docker-compose restart grafana
```

---

## 📚 Learn More

- **Full Guide:** `OBSERVABILITY_SETUP.md`
- **Quick Commands:** `OBSERVABILITY_QUICKSTART.md`
- **Prometheus Docs:** https://prometheus.io/docs/
- **Loki Docs:** https://grafana.com/docs/loki/
- **Grafana Tutorials:** https://grafana.com/tutorials/

---

## ✅ Next Steps

### 1. Add Distributed Tracing (Tempo)
- เพิ่ม tracing dependencies
- Config `management.otlp.tracing.endpoint`
- See full guide in `OBSERVABILITY_SETUP.md`

### 2. Create Custom Dashboards
- สร้าง dashboard สำหรับแต่ละ service
- Add business metrics (orders, sales, etc.)

### 3. Set Up Alerts
- Config alert rules ใน Prometheus
- Connect Alertmanager
- Notify via Slack/Email

### 4. Log Structured JSON
- Change logback pattern to JSON
- Parse logs better in Loki
- Add correlation IDs

---

**🎉 Happy Monitoring!**
