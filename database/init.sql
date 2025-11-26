SELECT 'CREATE DATABASE sidis_patients_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'sidis_patients_db')\gexec

SELECT 'CREATE DATABASE sidis_physicians_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'sidis_physicians_db')\gexec

SELECT 'CREATE DATABASE sidis_appointments_db'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'sidis_appointments_db')\gexec

