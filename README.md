# SIDIS-3DB-G02

The main goal of this project is to turn the monolithic HAP project in a distributed system.

## Deployment Instructions
To deploy the HAP project, the user most have the following software and configurations in their system.

### nginx
The system most have nginx installed and nginx.config file most have the ports of the services of the HAP, for that, there is a file in the root of the repository folder with that configuration.

### PostgreSQL
PostgreSQl most be installed in the system and it needs to have a user named "posgres" with the password being "UoU4CrIoGNgOtK31" and the following databases created: "sidis_appointments_db"; "sidis_patients_db"; "sidis_physicians_db".

## Architectural Decisions

### Domain-driven segregation



### Load Balancing

We decided to use nginx for the load balancing of the instances of a respective service, one of the reasons for that necessity is the fact that all the instances of the same service share the repository, so we want to make sure that none of the instances got overloaded. For more info [see doc](./docs/2/API_GATEWAY_DOCUMENTATION.md)

### Databases Engine

PostgreSQL was our choice for the database, it's open source and for our project it fit perfectly to have a shared databases between services.

## CQRS Pattern

We used the CQRS pattern only for the Physician Service. We decided that it was the best fit because the difference between read and write uses was significant, the number of times that it's need to consult a physician is greater than the times that a physician is added or dismissed.
For the appointment and patient services, we decided that using CQRS was unnecessary because the difference between write and read wasnt that great, and it would add more complexity to the system.

## Saga Pattern

### OBSERVABILITY

We use grafana labs solution for observability(Alloy, Loki and Tempo) and Prometheus. Access http://localhost:3030/ to see the dashboard, for more info see [Doc](./docs/Observability/OBSERVABILITY_EXPLANATION.md).

# Autoavaliação
### 1221412 - Vítor Barbosa

| Nome               | Número      | Nota |
|--------------------|-------------|------|
| Vítor Barbosa      | 1221412     | 15   |
| Carlos Oliveira    | 1220806     | 15   |
| Henrique Gonçalves | 1200968     | 15   | 

### 1220806 - Carlos Oliveira

| Nome               | Número      | Nota |
|--------------------|-------------|------|
| Vítor Barbosa      | 1221412     |      |
| Carlos Oliveira    | 1220806     |      |
| Henrique Gonçalves | 1200968     |      | 

### 1200968 - Henrique Gonçalves

| Nome               | Número      | Nota |
|--------------------|-------------|------|
| Vítor Barbosa      | 1221412     |      |
| Carlos Oliveira    | 1220806     |      |
| Henrique Gonçalves | 1200968     |      | 
