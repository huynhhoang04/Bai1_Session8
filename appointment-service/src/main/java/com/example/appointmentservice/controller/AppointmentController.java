package com.example.appointmentservice.controller;

import com.example.appointmentservice.dto.ApiResponseError;
import com.example.appointmentservice.dto.AppointmentRequest;
import com.example.appointmentservice.model.Appointment;
import com.example.appointmentservice.service.AppointmentService;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/v1/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<?> createAppointment(@RequestBody AppointmentRequest request) {
        try {
            Appointment saved = appointmentService.createAppointment(request);
            return ResponseEntity.ok(saved);
        } catch (CallNotPermittedException ex) {
            ApiResponseError error = new ApiResponseError(
                    "CircuitBreakerOpen",
                    "Doctor-service hiện không khả dụng, vui lòng thử lại sau",
                    DateTimeFormatter.ISO_INSTANT.format(Instant.now())
            );
            return new ResponseEntity<>(error, HttpStatus.SERVICE_UNAVAILABLE);
        } catch (Exception ex) {
            ApiResponseError error = new ApiResponseError(
                    "ExternalServiceError",
                    "Failed to verify patient or doctor: " + ex.getMessage(),
                    DateTimeFormatter.ISO_INSTANT.format(Instant.now())
            );
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
