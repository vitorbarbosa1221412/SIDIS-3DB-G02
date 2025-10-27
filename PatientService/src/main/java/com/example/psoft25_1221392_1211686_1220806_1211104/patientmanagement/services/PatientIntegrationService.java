package com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.services;

import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.client.AppointmentClient;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.client.PhysicianClient;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.client.dto.AppointmentDTO;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.client.dto.PhysicianDTO;
import com.example.psoft25_1221392_1211686_1220806_1211104.patientmanagement.model.Patient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class PatientIntegrationService {

	private final PhysicianClient physicianClient;
	private final AppointmentClient appointmentClient;
	private final RestTemplate restTemplate;

	@Value("${patient.peers:}")
	private String peersCsv;

	private List<String> getPeerBaseUrls() {
		if (peersCsv == null || peersCsv.isBlank()) {
			return Collections.emptyList();
		}
		return Arrays.stream(peersCsv.split(","))
				.map(String::trim)
				.filter(s -> !s.isEmpty())
				.toList();
	}

	// Simple helper to GET from first responsive peer
	public <T> Optional<T> getFromPeers(String relativePath, Class<T> responseType) {
		for (String base : getPeerBaseUrls()) {
			String url = base + relativePath;
			try {
				T body = restTemplate.getForObject(url, responseType);
				if (body != null) return Optional.of(body);
			} catch (RestClientException ignored) {
				// try next peer
			}
		}
		return Optional.empty();
	}


	public Optional<PhysicianDTO> getPhysicianById(Long physicianId) {
		return physicianClient.getPhysicianById(physicianId);
	}


	public boolean isPhysicianAvailable(String physicianNumber, String dateTime) {
		return physicianClient.isPhysicianAvailable(physicianNumber, dateTime);
	}


	public List<AppointmentDTO> getAppointmentHistory(Patient patient) {
		return appointmentClient.getAppointmentHistory(patient.getPatientNumber());
	}


	public List<AppointmentDTO> getUpcomingAppointments(Patient patient) {
		return appointmentClient.getUpcomingAppointments(patient.getPatientNumber());
	}


	public List<AppointmentDTO> getAppointmentHistory(String patientNumber) {
		return appointmentClient.getAppointmentHistory(patientNumber);
	}


	public List<AppointmentDTO> getUpcomingAppointments(String patientNumber) {
		return appointmentClient.getUpcomingAppointments(patientNumber);
	}


	public Optional<AppointmentDTO> getAppointmentByNumber(String appointmentNumber) {
		return appointmentClient.getAppointmentByNumber(appointmentNumber);
	}


	public Optional<AppointmentDTO> createAppointment(Object appointmentRequest) {
		return appointmentClient.createAppointment(appointmentRequest);
	}


	public boolean cancelAppointment(String appointmentNumber) {
		return appointmentClient.cancelAppointment(appointmentNumber);
	}

	public List<PhysicianDTO> searchPhysiciansByName(String name) {
		return physicianClient.searchPhysiciansByName(name)
				.map(arr -> Arrays.asList(arr))
				.orElseGet(Collections::emptyList);
	}

	public List<PhysicianDTO> searchPhysiciansBySpecialty(String specialty) {
		return physicianClient.searchPhysiciansBySpecialty(specialty)
				.map(arr -> Arrays.asList(arr))
				.orElseGet(Collections::emptyList);
	}

	public List<LocalTime> getAvailableSlots(String physicianNumber, LocalDate date) {
		return appointmentClient.getAvailableSlots(physicianNumber, date);
	}

	public Optional<AppointmentDTO> scheduleAppointment(Object scheduleRequest) {
		return appointmentClient.scheduleAppointment(scheduleRequest);
	}

	public List<AppointmentDTO> searchAppointmentsByPhysician(String physicianName) {
		return appointmentClient.searchAppointmentsByPhysician(physicianName);
	}
}
