package com.example.appointmentservice.service;

import com.example.appointmentservice.dto.ApiResponseError;
import com.example.appointmentservice.dto.AppointmentRequest;
import com.example.appointmentservice.model.Appointment;
import com.example.appointmentservice.repository.AppointmentRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private RestTemplate restTemplate;

    @CircuitBreaker(name = "doctorServiceCB")
    public Appointment createAppointment(AppointmentRequest request) {
        restTemplate.getForObject("http://patient-service/api/v1/patients/" + request.getPatientId(), Object.class);
        restTemplate.getForObject("http://doctor-service/api/v1/doctors/" + request.getDoctorId(), Object.class);

        Appointment appointment = new Appointment();
        appointment.setPatientId(request.getPatientId());
        appointment.setDoctorId(request.getDoctorId());
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setReason(request.getReason());
        appointment.setStatus("PENDING");
        return appointmentRepository.save(appointment);
    }
}
