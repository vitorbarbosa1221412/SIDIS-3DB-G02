# Bounded Contexts

The primary application of DDD was the division of the monolith into three Bounded Contexts, which became the microservices, each managing its own domain and data.

| DDD Concept                  | Microservices    | Domain Responsibility                                                                                                                                                                                             |
| :--------------------------- | :--------------- | :---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Bounded Context 1: Patient** | **Patient**      | Manage patient data (Patient), authenticate patient users.                                                                                                                                                        |
| **Bounded Context 2: Physician** | **Physician**    | Manage physician data (Physician), authenticate physician users, and manage work schedules.                                                                                                                       |
| **Bounded Context 3: Scheduling** | **Appointment**  | Manage appointments (Appointment) and consultation records (AppointmentRecord), coordinating with the other two services.                                                                                         |

This segregation ensures that each business concept has clear and unique meaning and business rules within the boundaries of its own service.
