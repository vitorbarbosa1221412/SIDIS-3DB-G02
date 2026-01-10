# Kubernetes Deployment Guide

This directory contains all Kubernetes manifests for deploying the SIDIS application stack.

## Prerequisites

1. **Kubernetes Cluster**: Docker Desktop Kubernetes, Minikube, or Kind
2. **kubectl**: Kubernetes command-line tool
3. **Docker Images**: All service images must be built locally (see below)

## Directory Structure

```
k8s/
├── namespace.yaml                    # Namespace definition
├── configmap.yaml                    # Non-sensitive configuration
├── secret.yaml                       # Sensitive credentials
├── infrastructure/                   # Infrastructure components
│   ├── postgres-deployment.yaml     # PostgreSQL database
│   ├── rabbitmq-deployment.yaml     # RabbitMQ message broker
│   └── mongodb-deployment.yaml      # MongoDB database
├── services/                         # Application services
│   ├── patient-service-1.yaml       # Patient Service Instance 1
│   ├── patient-service-2.yaml       # Patient Service Instance 2
│   ├── appointment-service-1.yaml   # Appointment Service Instance 1
│   ├── appointment-service-2.yaml   # Appointment Service Instance 2
│   ├── physician-command-service-1.yaml  # Physician Command Service Instance 1 (CQRS Write Side)
│   ├── physician-command-service-2.yaml  # Physician Command Service Instance 2 (CQRS Write Side)
│   ├── physician-query-service-1.yaml    # Physician Query Service Instance 1 (CQRS Read Side)
│   └── physician-query-service-2.yaml    # Physician Query Service Instance 2 (CQRS Read Side)
├── observability/                    # Observability stack
│   ├── loki-deployment.yaml         # Log aggregation
│   ├── tempo-deployment.yaml        # Distributed tracing
│   ├── prometheus-deployment.yaml   # Metrics collection
│   ├── alloy-daemonset.yaml         # Log collector
│   └── grafana-deployment.yaml      # Visualization dashboard
└── gateway/                          # API Gateway
    └── ingress.yaml                  # Ingress resource (requires Ingress Controller)
```

## Building Docker Images

Before deploying, build all service images locally:

```bash
# From the repository root directory
docker build -t patient-service:latest ./PatientService
docker build -t appointment-service:latest ./AppointmentService
docker build -t physician-command-service:latest ./PhysicianCommandService
docker build -t physician-query-service:latest ./PhysicianQueryService
```

**Note**: For Docker Desktop Kubernetes, images are automatically available to the cluster. For other Kubernetes setups, you may need to load images into the cluster or use a container registry.

## Deployment Steps

### 1. Create Namespace

```bash
kubectl apply -f k8s/namespace.yaml
```

### 2. Create ConfigMap and Secrets

```bash
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.yaml
```

### 3. Deploy Infrastructure

```bash
kubectl apply -f k8s/infrastructure/
```

Wait for infrastructure to be ready:

```bash
kubectl wait --for=condition=ready pod -l app=postgres -n sidis --timeout=120s
kubectl wait --for=condition=ready pod -l app=rabbitmq -n sidis --timeout=120s
kubectl wait --for=condition=ready pod -l app=mongodb -n sidis --timeout=120s
```

### 4. Deploy Application Services

```bash
kubectl apply -f k8s/services/
```

### 5. Deploy Observability Stack

```bash
kubectl apply -f k8s/observability/
```

### 6. Deploy Ingress (Optional)

**Note**: This requires an Ingress Controller (e.g., NGINX Ingress Controller).

For Docker Desktop, install NGINX Ingress Controller:

```bash
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.8.1/deploy/static/provider/cloud/deploy.yaml
```

Then deploy the Ingress:

```bash
kubectl apply -f k8s/gateway/ingress.yaml
```

## Quick Deploy All

To deploy everything at once:

```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/secret.yaml
kubectl apply -f k8s/infrastructure/
kubectl apply -f k8s/services/
kubectl apply -f k8s/observability/
```

## Accessing Services

### Port Forwarding (Without Ingress)

```bash
# Patient Service Instance 1
kubectl port-forward -n sidis service/patient-service-1 3000:3000

# Appointment Service Instance 1
kubectl port-forward -n sidis service/appointment-service-1 4000:4000

# Physician Command Service Instance 1 (Write Side)
kubectl port-forward -n sidis service/physician-command-service-1 5000:5000

# Physician Query Service Instance 1 (Read Side)
kubectl port-forward -n sidis service/physician-query-service-1 5001:5001

# Grafana
kubectl port-forward -n sidis service/grafana 3030:3000

# Prometheus
kubectl port-forward -n sidis service/prometheus 9090:9090
```

### Using Ingress (With Ingress Controller)

Add to `/etc/hosts` (Linux/Mac) or `C:\Windows\System32\drivers\etc\hosts` (Windows):

```
127.0.0.1 sidis.local
```

Then access:
- Patient Service: `http://sidis.local/api/patients`
- Appointment Service: `http://sidis.local/api/appointments`
- Physician Command Service: `http://sidis.local/api/physicians` (write operations)
- Physician Query Service: `http://sidis.local/api/physicians` (read operations)
- Grafana: `http://sidis.local/grafana`
- Prometheus: `http://sidis.local/prometheus`

## Monitoring and Debugging

### Check Pod Status

```bash
kubectl get pods -n sidis
```

### View Pod Logs

```bash
# Patient Service
kubectl logs -n sidis -l app=patient-service-1 --tail=100

# Appointment Service
kubectl logs -n sidis -l app=appointment-service-1 --tail=100

# Physician Command Service (Write Side)
kubectl logs -n sidis -l app=physician-command-service-1 --tail=100

# Physician Query Service (Read Side)
kubectl logs -n sidis -l app=physician-query-service-1 --tail=100
```

### Describe Pod (for troubleshooting)

```bash
kubectl describe pod <pod-name> -n sidis
```

### Check Services

```bash
kubectl get services -n sidis
```

### Check ConfigMaps and Secrets

```bash
kubectl get configmap -n sidis
kubectl get secret -n sidis
```

## Database Initialization

The PostgreSQL deployment includes an init script that creates all required databases:
- `sidis_patients_db_1` (Patient Service Instance 1)
- `sidis_patients_db_2` (Patient Service Instance 2)
- `sidis_appointments_db_1` (Appointment Service Instance 1)
- `sidis_appointments_db_2` (Appointment Service Instance 2)
- `sidis_physicians_db` (Physician Command Service - CQRS Write Side)

The Physician Query Service uses MongoDB:
- Database: `hap_physicians_read` (MongoDB - CQRS Read Side)

The init script runs automatically on first startup. If databases already exist, they won't be recreated.

## Troubleshooting

### Pods Not Starting

1. Check pod status: `kubectl get pods -n sidis`
2. Check pod logs: `kubectl logs <pod-name> -n sidis`
3. Describe pod: `kubectl describe pod <pod-name> -n sidis`

### Database Connection Issues

1. Verify PostgreSQL is running: `kubectl get pods -l app=postgres -n sidis`
2. Check PostgreSQL logs: `kubectl logs -l app=postgres -n sidis`
3. Verify databases exist: Connect to PostgreSQL and list databases

### Image Pull Errors

For Docker Desktop Kubernetes:
- Ensure images are built locally: `docker images | grep -E "patient-service|appointment-service|physician-command-service|physician-query-service"`
- Verify `imagePullPolicy: IfNotPresent` is set in deployments

For other Kubernetes setups:
- Load images into cluster or push to a container registry
- Update image references in deployment manifests

### Service Discovery Issues

- Services use Kubernetes DNS: `<service-name>.<namespace>.svc.cluster.local`
- Within the same namespace, use just `<service-name>`
- Verify services exist: `kubectl get svc -n sidis`

## Cleanup

To remove all resources:

```bash
kubectl delete namespace sidis
```

This will delete all resources in the namespace, including PVCs (unless they have a `Retain` reclaim policy).

