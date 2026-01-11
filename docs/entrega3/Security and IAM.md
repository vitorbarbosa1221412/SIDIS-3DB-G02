# Security & Identity Access Management (IAM) Stack

To ensure secure communication between microservices and centralized user management, an IAM strategy was implemented. Several tools were considered, and a comparison can be seen in the Table below.


| Tool          | Open Source | Strengths                               | Limitations                         |
|---------------|-------------|-----------------------------------------|-------------------------------------|
| Keycloak    | Yes         | Full IAM (AuthN/AuthZ), Docker-ready, Self-hosted            | High resource consumption (JVM)    |
| Auth0       | No          | Managed infrastructure, easy setup             | Data privacy (SaaS), cost at scale      |
| Spring Security        | Yes         | Native Java integration           | No centralized user management UI    |
| Okta   | No          | Enterprise-grade                    | Vendor lock-in, expensive           |
| Gluu          | Yes         | High performance               | High deployment complexity                          |




Addressing the needs for a self-hosted, secure, and centralized user management system within a Docker environment, Keycloak was selected as the Identity Provider (IdP), integrated with Spring Security 6 (OAuth2 Resource Server) on the microservices side.

This architecture delegates authentication to Keycloak (via OpenID Connect) while retaining authorization logic (RBAC) within the microservices.






### 1. Authorization Flow
The authentication process follows the standard OpenID Connect flow with a specific adaptation for the local Docker environment:


- Authentication: The User (e.g., Doctor) sends credentials to Keycloak (via Host localhost:8180)
- Token Issuance: Keycloak validates credentials against its database and returns a JWT Access Token. Crucially, this token is signed by Keycloak's private key.
- Resource Request: The Client sends a request to the Microservice API (e.g., GET /api/physicians/test-patient/1) including the JWT in the Authorization: Bearer header.

- Token Validation (Service Side):
- `The Microservice intercepts the request.`
- `It fetches the Public Keys (JWK Set) from Keycloak using the internal Docker address (http://sidis-keycloak:8080).`
- `It verifies the JWT signature using these keys.`
- `It verifies the Issuer claim (iss) against the allowed list`
- Role Enforcement: Spring Security extracts the roles from the JWT (realm_access.roles) and maps them to internal authorities (e.g., ROLE_PHYSICIAN). If the user has the required role, access is granted (200 OK); otherwise, it is denied (403 Forbidden).

### 2. Implementation Configuration
**File:** `SecurityConfig.java`

- securityFilterChain: Configures the application as an OAuth2 Resource Server and establishes stateless session management (REST standard). It defines granular access control rules, such as allowing public access to /api/public/** while restricting /api/physicians/** to users with ADMIN or PHYSICIAN roles.
- jwtDecoder: The custom bean responsible for solving the network issue. It forces the application to fetch keys internally while validating external tokens.
- jwtAuthenticationConverter: A utility bean that translates Keycloak's specific JSON structure (where roles are nested under realm_access) into the flat structure (GrantedAuthority) required by Spring Security.

**File:** `pom.xml`

- spring-boot-starter-security: Core security framework.
- spring-boot-starter-oauth2-resource-server: Enables the application to validate JWTs.
- spring-security-oauth2-jose: Provides the libraries for Javascript Object Signing and Encryption (JOSE) required to process JWT signatures.```



### 3. Deployment Architecture

Infrastructure Update: To support this security layer, the docker-compose.yml was updated to include:

    -Keycloak Container: Running on port 8180 (mapped to host) and 8080 (internal), backed by its own Postgres database (sidis_keycloak_db).
    -Network: All services, including Keycloak and the Microservices, share the common sidis-network to allow direct internal communication for key fetching.


## Accessing Keycloak:

Once everything is running:
- **URL:** http://localhost:8180
- **Username:** admin
- **Password:** admin

This setup ensures a robust security posture where authentication is centralized, credentials are never stored in the microservices, and access is strictly controlled via tokens and roles.
