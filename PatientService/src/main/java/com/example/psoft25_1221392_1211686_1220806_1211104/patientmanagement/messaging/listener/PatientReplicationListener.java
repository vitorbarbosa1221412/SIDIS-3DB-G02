package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.messaging.listener;

import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.messaging.config.RabbitMQConfig;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.messaging.dto.PatientCreatedEvent;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.messaging.dto.PatientDeletedEvent;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.messaging.dto.PatientUpdatedEvent;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.services.PatientPeerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Listener for Patient events from other instances (cross-instance replication)
 * Implements event-driven replication for database-per-instance pattern
 * 
 * When a patient is created/updated/deleted in one instance, this listener
 * replicates the change to the local database for eventual consistency
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PatientReplicationListener {

    private final PatientPeerService patientPeerService;
    
    @Value("${server.instance.id:patient-service-1}")
    private String instanceId;

    /**
     * Listen to PatientCreatedEvent from other instances
     * Replicates patient data to local database
     */
    @RabbitListener(queues = RabbitMQConfig.PATIENT_CREATED_QUEUE)
    public void handlePatientCreated(PatientCreatedEvent event) {
        try {
            // Check if event is from another instance (to avoid infinite loops)
            String eventInstanceId = event.getInstanceId();
            if (eventInstanceId != null && eventInstanceId.equals(instanceId)) {
                log.debug("Ignoring PatientCreatedEvent from same instance: {}", instanceId);
                return;
            }

            log.info("Received PatientCreatedEvent for patient {} from instance {}, replicating to local DB", 
                    event.getPatientNumber(), eventInstanceId);

            // Fetch full patient data from peer instance and replicate locally
            patientPeerService.replicatePatientFromPeer(event.getPatientId(), event.getPatientNumber());
            
        } catch (Exception e) {
            log.error("Error replicating PatientCreatedEvent: {}", e.getMessage(), e);
            // In production, consider dead-letter queue for retry
        }
    }

    /**
     * Listen to PatientUpdatedEvent from other instances
     * Replicates updated patient data to local database
     */
    @RabbitListener(queues = RabbitMQConfig.PATIENT_UPDATED_QUEUE)
    public void handlePatientUpdated(PatientUpdatedEvent event) {
        try {
            String eventInstanceId = event.getInstanceId();
            if (eventInstanceId != null && eventInstanceId.equals(instanceId)) {
                log.debug("Ignoring PatientUpdatedEvent from same instance: {}", instanceId);
                return;
            }

            log.info("Received PatientUpdatedEvent for patient {} from instance {}, replicating to local DB", 
                    event.getPatientNumber(), eventInstanceId);

            // Fetch updated patient data from peer instance and replicate locally
            patientPeerService.replicatePatientFromPeer(event.getPatientId(), event.getPatientNumber());
            
        } catch (Exception e) {
            log.error("Error replicating PatientUpdatedEvent: {}", e.getMessage(), e);
        }
    }

    /**
     * Listen to PatientDeletedEvent from other instances
     * Removes patient from local database
     */
    @RabbitListener(queues = RabbitMQConfig.PATIENT_DELETED_QUEUE)
    public void handlePatientDeleted(PatientDeletedEvent event) {
        try {
            String eventInstanceId = event.getInstanceId();
            if (eventInstanceId != null && eventInstanceId.equals(instanceId)) {
                log.debug("Ignoring PatientDeletedEvent from same instance: {}", instanceId);
                return;
            }

            log.info("Received PatientDeletedEvent for patient {} from instance {}, removing from local DB", 
                    event.getPatientNumber(), eventInstanceId);

            // Delete patient from local database
            patientPeerService.deletePatientLocally(event.getPatientId(), event.getPatientNumber());
            
        } catch (Exception e) {
            log.error("Error replicating PatientDeletedEvent: {}", e.getMessage(), e);
        }
    }
}


