# Observability Stack

Several tools were considered for the implementation, and a comparison can be seen in Table(adapted from H. Ahmed and H. J. Syed, “Observability in microservices: An in-depth explo ration of frameworks, challenges, and deployment paradigms,” IEEE Access, 2 2025.)



| Tool          | Open Source | Strengths                               | Limitations                          |
|---------------|------|-----------------------------------------|--------------------------------------|
| Prometheus    | Yes  | Reliability and scalability             | Does not support tracing or logs     |
| Grafana       | Yes  | Sophisticated visualization             | Does not collect data natively       |
| Zipkin        | Yes  | Latency diagnosis and tracing           | Does not include metrics or logs     |
| Weave Scope   | Yes  | Container monitoring                    | Does not support tracing             |
| Loki          | Yes  | Economical log management               | Logs only                            |
| OpenTelemetry | Yes  | Comprehensive framework                 | Requires external storage            |
| BPFTrace      | Yes  | Dynamic tracing                         | Low-level observability              |
| Istio         | Yes  | Traffic management                      | High complexity                      |
| Jaeger        | Yes  | Service analysis                        | Tracing only                         |




Addressing the needs, the solutions that stood out were the ELK Stack (consisting of Elasticsearch, Logstash, and Kibana), developed by Elastic, and Alloy, Loki, and Grafana Dashboard, developed by Grafana Labs.

The Grafana Labs solution stands out due to its simplicity, efficiency, and the integration capabilities between its components (Loki for logs, Prometheus for metrics, and Tempo for traces).

Although the ELK Stack is a more established product, with a broader user community and superior search capabilities that allow it to handle complex queries and deliver results quickly, its complexity and higher resource consumption led to the conclusion that it would not be the most suitable option.






### 1. Alloy Configuration
**File:** `alloy-config.alloy`

**What it does:**
- Reads log files from all 3 services from `/var/log/sidis/` directory
- Adds `service` labels to distinguish logs (patient-service, appointment-service, physician-service)
- Sends all logs to Loki

Instead of reading from one file, it now reads from:
- `/var/log/sidis/patient-service/app.log`
- `/var/log/sidis/appointment-service/app.log`
- `/var/log/sidis/physician-service/app.log`

### 2. Prometheus Configuration
**File:** `prometheus-config.yml`

**What it does:**
- Prometheus **scrapes** (pulls) metrics from all services
- Each service exposes a `/internal/prometheus` endpoint
- Prometheus periodically requests metrics from these endpoints

**How Prometheus Scraping Works:**

```
┌─────────────────┐
│  Prometheus     │
│  (Every 15s)    │
└────────┬────────┘
         │
         │ HTTP GET requests
         │
    ┌────┴────┬──────────────┬──────────────┐
    │         │              │              │
    ▼         ▼              ▼              ▼
Patient   Patient    Appointment  Appointment  Physician  Physician
Svc-1     Svc-2      Svc-1        Svc-2        Svc-1      Svc-2
:3000     :3001      :4000        :4001        :5000       :5001
```

**Process:**
1. Prometheus reads `prometheus-config.yml`
2. Every 15 seconds, it makes HTTP GET requests to:
   - `http://patient-service-1:3000/internal/prometheus`
   - `http://patient-service-2:3001/internal/prometheus`
   - `http://appointment_service1:4000/internal/prometheus`
   - `http://appointment_service2:4001/internal/prometheus`
   - `http://physician_service1:5000/internal/prometheus`
   - `http://physician_service2:5001/internal/prometheus`
3. Services respond with metrics in Prometheus format
4. Prometheus stores the metrics
5. Grafana queries Prometheus to display metrics

**Important:** For this to work, services must be:
- On the same Docker network as Prometheus, OR
- Accessible via `host.docker.internal` if running on host machine

### 3. Grafana Datasource Provisioning
**File:** `grafana/provisioning/datasources/datasources.yml`

**What "Grafana Datasource Configuration" Means:**

Normally, when you start Grafana, you have to manually:
1. Go to Configuration → Data Sources
2. Click "Add data source"
3. Select "Loki" → Enter URL → Save
4. Repeat for Prometheus
5. Repeat for Tempo

**With provisioning files:**
- Grafana automatically reads `datasources.yml` on startup
- All three datasources (Loki, Prometheus, Tempo) are automatically configured
- You can immediately start creating dashboards!

**What the file does:**
- Configures Loki at `http://loki:3100` (for logs)
- Configures Prometheus at `http://prometheus:9090` (for metrics)
- Configures Tempo at `http://tempo:3200` (for traces)
- Sets up cross-linking (e.g., click trace ID in logs to see trace in Tempo)

### 4. Docker Compose Updates
**File:** `docker-compose.yml`

**Added services:**
- **Loki**: Log aggregation database
- **Tempo**: Distributed tracing backend
- **Prometheus**: Metrics database
- **Alloy**: Log collector (reads files, sends to Loki)
- **Grafana**: Observability dashboard UI

**Key configuration:**
- Each service container bind-mounts the host folder `/SIDIS-25-26/logs/<service>` into `/var/log/sidis/<service>` inside the container, so whatever Spring writes to `/SIDIS-25-26/...` is visible to Alloy.
- Alloy mounts the whole `/SIDIS-25-26/logs` tree read-only at `/var/log/sidis` and tails the individual `app.log` files from there.
- All services are on `patient-network` for communication.

## How Logs Come Together

### Architecture Flow:

```
┌──────────────────┐
│ PatientService    │ ──writes──> /SIDIS-25-26/logs/patient-service/app.log
└──────────────────┘

┌──────────────────┐
│AppointmentService│ ──writes──> /SIDIS-25-26/logs/appointment-service/app.log
└──────────────────┘

┌──────────────────┐
│PhysicianService  │ ──writes──> /SIDIS-25-26/logs/physician-service/app.log
└──────────────────┘
                    │
                    │ (Alloy reads all files)
                    ▼
            ┌───────────────┐
            │     Alloy     │ (Separate service)
            │  (Container)  │
            └───────────────┘
                    │
                    │ (Adds service labels)
                    │
                    ▼
            ┌───────────────┐
            │     Loki      │ (All logs stored here with labels)
            └───────────────┘
                    │
                    │ (Grafana queries)
                    ▼
            ┌───────────────┐
            │    Grafana    │ (View all logs together, filter by service)
            └───────────────┘
```

### Important Points:

1. **Services' logs**
   - Each service writes to its own log file
   - Alloy reads all files independently
   - Alloy is a separate observability service

2. **Service Labels**
   - Alloy adds `service=patient-service`, `service=appointment-service`, `service=physician-service` labels
   - In Grafana, you can:
     - View all logs: `{service=~".+"}`
     - Filter by service: `{service="patient-service"}`
     - Compare services side-by-side

3. **Log File Locations**
   - All services now write directly to the host paths `/SIDIS-25-26/logs/<service>/app.log`.
   - Docker bind-mounts those directories into each container at `/var/log/sidis/<service>`, so Alloy can tail `/var/log/sidis/...` regardless of which service produced the file.
   - Make sure the `/SIDIS-25-26/logs/...` folders exist on the host before starting the stack; Docker does not create nested directories automatically.


    

## Accessing Grafana

Once everything is running:
- **URL:** http://localhost:3030
- **Username:** admin
- **Password:** admin

You'll see all three datasources (Loki, Prometheus, Tempo) already configured.

