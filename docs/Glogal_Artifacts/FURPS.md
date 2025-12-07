# Supplementary Specification (FURPS+)

## Functionality

### Patient and Physician Registration:

The software should allow for the registration of patients, physicians, the schedule of appointments and the creation of the respective record. The system should always certificate that there are no duplicated data (Ex: To Clients with the same ID).

### User Login

The software needs to let patients, physicians and admins login to the system in order to verify if they have the permissions to do certain tasks.

### Update Data

The software should permit the change of patients information, physicians information, reschedule and cancellation of appointments and edit the appointment records.

### Search data

The software must allow the search all the data in the database, and search by specific characteristics like: 

- Patients by ID and Name; 
- Physicians by ID, Name and Speciality;
- Appointments by Number, patient history and upcoming.

## Usability

### Error prevention:

The software should incorporate a system to prevent the input of duplicated data, such as the registration of two patients with the same ID. It should include robust data validation checks to verify if the entered data aligns with the designated data types, such as integers, strings, dates, and other specific formats

### Patient Registration and Accessibility:

The software should facilitate a patient registration process for new patients and easy accessibility to their appointments history.

## Reliability

The software should maintain accuracy so that every patient, physician, appointment and appointment record is well linked to ensure that no data is lost.
In case the system fails another instance should be available and there should be no data loss.

**There should be no SPOFs (Single Point of Failure).**

## Performance

The software should be able to add and store more and more information, about the labs, the clients, etc.
It also should start up in less than 10 seconds. At certain times of the day, it is expected that the system will be overloaded. To avoid
potential problems, the system must be prepared so that the response time is at maximum of 5
seconds regardless of the existing load.
Overall system availability must be higher than 99% per year.

## Supportability

### Maintenance and Upgrades:

The software should allow for easy maintenance  without disrupting the daily operations of the laboratories.

### Testability

The software should have a testing component to ensure that it runs the way it was designed for.
For that, it should be used the Google Testing Framework.

## +

### Design Constraints

The software needs to be coded using C++.
All project artifacts (including code) must be developed in English.

### Implementation Constraints

No implementation constraints stated.

### Interface Constraints

It is also important that the system is
prepared to easily support data persistence on multiple target systems as, for instance, relational
databases, NoSQL databases or in memory databases.

### Physical Constraints

No physical constraint stated.