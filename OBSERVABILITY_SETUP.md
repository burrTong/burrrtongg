# 🚀 Setup Distributed Tracing with Tempo

## ขั้นตอนการเพิ่ม Tracing

### 1. เพิ่ม Dependencies ใน `build.gradle`

```gradle
dependencies {
    // Existing dependencies...
    
    // Add tracing support
    implementation 'io.micrometer:micrometer-tracing-bridge-otel'
    implementation 'io.opentelemetry:opentelemetry-exporter-otlp'
}
```

### 2. เพิ่ม Configuration ใน `application.properties`

```properties
# Tracing configuration
management.tracing.sampling.probability=1.0
management.otlp.tracing.endpoint=http://tempo:4318/v1/traces
```

### 3. Restart Backend

```bash
docker-compose down
docker-compose up --build -d
```

### 4. ทดสอบ Tracing

1. ทำ requests ไปที่ backend: `http://localhost:8080/api/products`
2. เปิด Grafana: `http://localhost:3000`
3. ไปที่ **Explore** > เลือก **Tempo**
4. Query: `{service.name="backend"}`
5. คุณจะเห็น trace timeline ของแต่ละ request!

---

## 📊 Grafana Dashboard Setup

### สร้าง Dashboard สำหรับ Backend

#### 1. เปิด Grafana
```
http://localhost:3000
Username: admin
Password: admin
```

#### 2. สร้าง Dashboard ใหม่
- คลิก **+** > **Create Dashboard**
- Add panels ตามด้านล่าง

---

### 📈 Panel 1: Request Rate

**Type:** Time Series Graph
**Data Source:** Prometheus
**Query:**
```promql
rate(http_server_requests_seconds_count{job="app-service"}[1m])
```
**Title:** "Requests per Second"
**Legend:** `{{uri}} - {{method}}`

---

### 📈 Panel 2: Response Time (p95, p99)

**Type:** Time Series Graph
**Data Source:** Prometheus
**Query A (p95):**
```promql
histogram_quantile(0.95, rate(http_server_requests_seconds_bucket[5m]))
```
**Query B (p99):**
```promql
histogram_quantile(0.99, rate(http_server_requests_seconds_bucket[5m]))
```
**Title:** "Response Time Percentiles"

---

### 📈 Panel 3: Error Rate

**Type:** Stat / Gauge
**Data Source:** Prometheus
**Query:**
```promql
sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m])) / sum(rate(http_server_requests_seconds_count[5m])) * 100
```
**Title:** "Error Rate (%)"
**Thresholds:** 
- Green: < 1%
- Yellow: 1-5%
- Red: > 5%

---

### 📈 Panel 4: Top 5 Slowest Endpoints

**Type:** Table
**Data Source:** Prometheus
**Query:**
```promql
topk(5, avg by (uri) (rate(http_server_requests_seconds_sum[5m]) / rate(http_server_requests_seconds_count[5m])))
```
**Title:** "Slowest Endpoints (Avg Response Time)"

---

### 📈 Panel 5: JVM Memory Usage

**Type:** Time Series Graph
**Data Source:** Prometheus
**Query:**
```promql
jvm_memory_used_bytes{area="heap"}
```
**Title:** "JVM Heap Memory Usage"
**Unit:** bytes

---

### 📈 Panel 6: CPU Usage

**Type:** Gauge
**Data Source:** Prometheus
**Query:**
```promql
system_cpu_usage * 100
```
**Title:** "CPU Usage (%)"
**Unit:** percent (0-100)

---

### 📈 Panel 7: Live Logs

**Type:** Logs
**Data Source:** Loki
**Query:**
```logql
{container_name="backend_app"}
```
**Title:** "Backend Logs (Live)"

---

## 🎯 Use Cases

### Use Case 1: หา Slow Requests
```promql
# Requests ที่ช้ากว่า 1 วินาที
http_server_requests_seconds_sum / http_server_requests_seconds_count > 1
```

### Use Case 2: Monitor Order Service
```logql
# Logs ของ OrderService
{container_name="backend_app"} |= "OrderService"
```

### Use Case 3: Track Errors
```logql
# Error logs พร้อม context
{container_name="backend_app"} |= "ERROR" | json
```

### Use Case 4: Correlate Metrics + Logs + Traces
1. เห็น spike ใน Response Time (Prometheus)
2. ดู logs ช่วงเวลานั้น (Loki)
3. เปิด trace เพื่อดู bottleneck (Tempo)

---

## 🔍 Alerting (Optional)

### สร้าง Alert Rules ใน Prometheus

**File:** `docker/prometheus/prometheus.yml`

```yaml
rule_files:
  - "alert_rules.yml"

alerting:
  alertmanagers:
    - static_configs:
        - targets: []
```

**File:** `docker/prometheus/alert_rules.yml`

```yaml
groups:
  - name: backend_alerts
    interval: 30s
    rules:
      - alert: HighErrorRate
        expr: |
          sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m])) 
          / sum(rate(http_server_requests_seconds_count[5m])) > 0.05
        for: 5m
        labels:
          severity: critical
        annotations:
          summary: "High error rate detected"
          description: "Error rate is {{ $value | humanizePercentage }}"

      - alert: HighResponseTime
        expr: |
          histogram_quantile(0.95, 
            rate(http_server_requests_seconds_bucket[5m])
          ) > 2
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High response time"
          description: "P95 response time is {{ $value }}s"
```

---

## 📚 Resources

- [Prometheus Query Language](https://prometheus.io/docs/prometheus/latest/querying/basics/)
- [LogQL (Loki)](https://grafana.com/docs/loki/latest/query/)
- [Tempo Tracing](https://grafana.com/docs/tempo/latest/)
- [Micrometer Documentation](https://micrometer.io/docs)
- [Grafana Dashboards](https://grafana.com/grafana/dashboards/)

---

## 🎓 Quick Tips

### Prometheus
- ใช้ `rate()` สำหรับ counters
- ใช้ `histogram_quantile()` สำหรับ percentiles
- ใช้ `topk()` หา top N values

### Loki
- `|=` - contains
- `!=` - not contains
- `|~ "regex"` - regex match
- `| json` - parse JSON logs

### Grafana
- กด **Ctrl+K** เพื่อค้นหาอะไรก็ได้
- ใช้ **Variables** ($variable) เพื่อทำ dynamic dashboards
- **Save** dashboard บ่อยๆ!

---

## ✅ Checklist

- [ ] Prometheus scraping metrics (http://localhost:9090/targets)
- [ ] Loki receiving logs (Query ใน Grafana Explore)
- [ ] Tempo ready for traces (Add tracing dependencies)
- [ ] Grafana datasources configured
- [ ] Create custom dashboard
- [ ] Set up alerts (Optional)
