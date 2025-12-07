# API Gateway Documentation - SIDIS Healthcare System

## Overview

The SIDIS Healthcare System uses **Nginx** as an API Gateway to provide a single entry point for all client requests. The gateway handles routing, load balancing, SSL termination, and request distribution across multiple service instances.

## Architecture

```
                    ┌─────────────────┐
                    │   API Gateway   │
                    │  (Nginx:8080)   │
                    └────────┬────────┘
                             │
                ┌────────────┼────────────┐
                │            │            │
        ┌───────▼──────┐ ┌───▼──────┐ ┌──▼──────────┐
        │ Appointments │ │Physicians│ │   Patients  │
        │  Service     │ │ Service  │ │  Service   │
        │  (4000/4001) │ │(5000/5001)│ │ (3000/3001)│
        └──────────────┘ └───────────┘ └────────────┘
```

## Gateway Features

### 1. **Single Entry Point**
- All external requests enter through port **8080** (HTTP)
- Clients don't need to know individual service ports or locations
- Simplified client configuration and deployment

### 2. **Load Balancing**
- **Algorithm**: Least Connections (`least_conn`)
- Distributes requests to the instance with the fewest active connections
- Better load distribution than round-robin for varying request durations

### 3. **High Availability**
- Each service has **2 instances** running simultaneously
- Automatic failover on service instance failure
- Health checks remove unhealthy instances from the pool

### 4. **Request Routing**
- Path-based routing to different services
- Transparent proxy with proper header forwarding
- SSL passthrough for Patient Service (HTTPS backend with load balancing)

## Service Routing

### Appointments Service
- **Route**: `/api/appointments/*`
- **Backend**: `appointment-service-1:4000`, `appointment-service-2:4001`
- **Protocol**: HTTP
- **Load Balancing**: Least connections algorithm
- **Example**: 
  ```
  GET http://localhost:8080/api/appointments/patient/123/history
  → http://appointment-service-1:4000/api/appointments/patient/123/history
  (or appointment-service-2:4001, depending on load)
  ```

### Physicians Service
- **Route**: `/api/physicians/*`
- **Backend**: `physician-service-1:5000`, `physician-service-2:5001`
- **Protocol**: HTTP
- **Load Balancing**: Least connections algorithm
- **Example**:
  ```
  GET http://localhost:8080/api/physicians/search?name=Dr.Smith
  → http://physician-service-1:5000/api/physicians/search?name=Dr.Smith
  (or physician-service-2:5001, depending on load)
  ```

### Patients Service
- **Route**: `/api/patients/*`
- **Backend**: `patient-service-1:3000`, `patient-service-2:3001`
- **Protocol**: HTTPS (with SSL verification disabled for self-signed certs)
- **Load Balancing**: Round-robin based on request ID (with proper SNI handling)
- **Example**:
  ```
  GET http://localhost:8080/api/patients/id/1/profile
  → https://patient-service-1:3000/api/patients/id/1/profile
  (or patient-service-2:3001, depending on request ID)
  ```



## Load Balancing Configuration

### Upstream Definitions

```nginx
# Appointments Service
upstream appointments_service {
    least_conn;
    server appointment-service-1:4000 max_fails=3 fail_timeout=30s;
    server appointment-service-2:4001 max_fails=3 fail_timeout=30s;
    keepalive 32;
}

# Physicians Service
upstream physicians_service {
    least_conn;
    server physician-service-1:5000 max_fails=3 fail_timeout=30s;
    server physician-service-2:5001 max_fails=3 fail_timeout=30s;
    keepalive 32;
}

# Patients Service (HTTPS backend)
upstream patients_service {
    least_conn;
    server patient-service-1:3000 max_fails=3 fail_timeout=30s;
    server patient-service-2:3001 max_fails=3 fail_timeout=30s;
    keepalive 32;
}

# Patient Service backend selection (for HTTPS with proper SNI)
map $request_id $patient_backend {
    default patient-service-1:3000;
    ~*[02468]$ patient-service-1:3000;
    ~*[13579]$ patient-service-2:3001;
}
```

### Load Balancing Parameters

- **`least_conn`**: Routes to instance with fewest active connections
- **`max_fails=3`**: Removes instance after 3 consecutive failures
- **`fail_timeout=30s`**: Re-attempts failed instance after 30 seconds
- **`keepalive 32`**: Maintains up to 32 keepalive connections per upstream

## Request Retry Logic

Each route includes automatic retry on failure:

```nginx
proxy_next_upstream error timeout invalid_header http_500 http_502 http_503 http_504;
proxy_next_upstream_tries 2;
proxy_next_upstream_timeout 10s;
```

- **Retries on**: Network errors, timeouts, HTTP 5xx errors
- **Max retries**: 2 attempts
- **Timeout**: 10 seconds per retry

## Proxy Headers

The gateway forwards important headers to backend services:

```nginx
proxy_set_header Host $host;
proxy_set_header X-Real-IP $remote_addr;
proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
proxy_set_header X-Forwarded-Proto $scheme;
proxy_set_header X-Forwarded-Host $host;
proxy_set_header X-Forwarded-Port $server_port;
```

These headers allow backend services to:
- Identify the original client IP
- Determine if request was HTTPS
- Maintain proper host information

## Timeout Configuration

```nginx
proxy_connect_timeout 60s;  # Time to establish connection
proxy_send_timeout 60s;      # Time to send request
proxy_read_timeout 60s;      # Time to read response
```

All timeouts set to 60 seconds to accommodate long-running operations.

## SSL/HTTPS Configuration

### Patient Service (HTTPS Backend)

The Patient Service uses HTTPS internally. The gateway:
- Connects to backend using HTTPS with load balancing
- Uses variable-based proxy_pass for proper SNI (Server Name Indication) handling
- Disables SSL verification (for self-signed certificates in development)
- Uses Docker's internal DNS resolver for dynamic hostname resolution

**Configuration**:
```nginx
# DNS resolver for variable-based proxy_pass
resolver 127.0.0.11 valid=30s;

# Backend selection using map
map $request_id $patient_backend {
    default patient-service-1:3000;
    ~*[02468]$ patient-service-1:3000;
    ~*[13579]$ patient-service-2:3001;
}

# Location block
location ^~ /api/patients {
    set $backend https://$patient_backend;
    proxy_pass $backend$request_uri;
    
    proxy_ssl_verify off;  # Development only
    proxy_ssl_server_name on;
    proxy_ssl_name $patient_backend;
}
```

**Why variable-based approach?**
- Nginx upstream blocks don't work reliably with `https://` prefix for SNI
- Variable-based approach allows proper SNI handling for each backend server
- Ensures SSL handshake succeeds with correct server name indication

**Security Note**: `proxy_ssl_verify off` should be enabled in production with proper certificates.

## Health Checks

### Gateway Health Endpoint

```bash
GET http://localhost:8080/nginx-health
```

Returns: `200 OK` with body `healthy\n`

### Service Health Checks

Each service instance has health checks configured in Docker Compose:
- **Interval**: 30 seconds
- **Timeout**: 10 seconds
- **Retries**: 3 attempts
- **Endpoint**: `/actuator/health`

## WebSocket Support

The gateway is configured for WebSocket connections:

```nginx
proxy_http_version 1.1;
proxy_set_header Upgrade $http_upgrade;
proxy_set_header Connection "upgrade";
```

## Logging

### Access Logs
- **Location**: `/var/log/nginx/access.log`
- **Format**: Standard Nginx access log format
- **Contains**: Request method, URI, response code, response time

### Error Logs
- **Location**: `/var/log/nginx/error.log`
- **Contains**: Connection errors, upstream failures, configuration issues

## Network Configuration

### Docker Network

All services run on the `sidis-network` bridge network:

```yaml
networks:
  sidis-network:
    driver: bridge
```

### Container Names

The gateway expects these exact container names:
- `appointment-service-1`, `appointment-service-2`
- `physician-service-1`, `physician-service-2`
- `patient-service-1`, `patient-service-2`

## Deployment

### Docker Compose

The gateway is deployed as part of the main `compose.yaml`:

```yaml
nginx:
  image: nginx:alpine
  container_name: sidis-nginx
  ports:
    - "8080:80"   # Port 8080 on host to avoid conflicts (e.g., with WSL)
    - "8443:443"
  volumes:
    - ./nginx/nginx.conf:/etc/nginx/conf.d/default.conf:ro
  depends_on:
    - appointment-service-1
    - appointment-service-2
    - physician-service-1
    - physician-service-2
    - patient-service-1
    - patient-service-2
  networks:
    - sidis-network
```

**Note**: Port 8080 is used instead of 80 to avoid conflicts with other services (e.g., WSL on Windows).

### Configuration File

- **Location**: `nginx/nginx.conf`
- **Mounted as**: `/etc/nginx/conf.d/default.conf:ro` (read-only)

## Testing the Gateway

### Test Load Balancing

```bash
# Test appointments service load balancing
for i in {1..10}; do 
  curl http://localhost:8080/api/appointments/health
  echo ""
done

# Test patients service load balancing (HTTPS backend)
for i in {1..10}; do 
  curl http://localhost:8080/api/patients/id/1/profile
  echo ""
done
```

### Test Routing

```bash
# Test appointments route
curl http://localhost:8080/api/appointments/patient/PAT-001/history

# Test physicians route
curl http://localhost:8080/api/physicians/search?name=Dr.Smith

# Test patients route
curl http://localhost:8080/api/patients/number/PAT-001
curl http://localhost:8080/api/patients/id/1/profile
```

### Test Health Endpoint

```bash
curl http://localhost:8080/nginx-health
```


## Configuration Reference

Full configuration file: `nginx/nginx.conf`

Key sections:
- Lines 5-27: Upstream definitions
- Lines 29-35: Patient Service backend selection map
- Lines 37-143: Server block with routing rules
- Lines 93-95: Appointments routing
- Lines 100-102: Physicians routing
- Lines 107-120: Patients routing (HTTPS with load balancing)
- Lines 125-131: Actuator routing

