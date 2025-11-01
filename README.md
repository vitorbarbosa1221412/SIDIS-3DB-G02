# SIDIS-3DB-G02

## Deployment Instructions
To deploy the HAP project, the user most have the following software and configurations in their system.

### nginx
The system most have nginx installed and nginx.config file most have the ports of the services of the HAP, for that, there is a file in the root of the repository folder with that configuration.

### PostgreSQL
PostgreSQl most be installed in the system and it needs to have a user named "posgres" with the password being "UoU4CrIoGNgOtK31" and the following databases created: "sidis_appointments_db"; "sidis_patients_db"; "sidis_physicians_db".

## Architectural Decisions
We decided to use nginx for the load balancing of the instances of a respective service, one of the reasons for that necessity is the fact that all the instances of the same service share the repository, so we want to make sure that none of the instances got overloaded.

PostgreSQL was our choice for the database, it's open source and for our project it fit perfectly to have a shared databases between services.