-- Patient Service Databases (one per instance)
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'sidis_patients_db_1') THEN
        CREATE DATABASE sidis_patients_db_1;
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'sidis_patients_db_2') THEN
        CREATE DATABASE sidis_patients_db_2;
    END IF;
END
$$;

-- Physician Service Database (shared for now)
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'sidis_physicians_db') THEN
        CREATE DATABASE sidis_physicians_db;
    END IF;
END
$$;

-- Appointment Service Databases (one per instance)
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'sidis_appointments_db_1') THEN
        CREATE DATABASE sidis_appointments_db_1;
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'sidis_appointments_db_2') THEN
        CREATE DATABASE sidis_appointments_db_2;
    END IF;
END
$$;

