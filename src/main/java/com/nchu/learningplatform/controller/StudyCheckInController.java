package com.nchu.learningplatform.controller;

import com.nchu.learningplatform.service.StudyCheckInService;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/check-ins")
@CrossOrigin
public class StudyCheckInController {

    @Resource
    private StudyCheckInService studyCheckInService;

    @Resource
    private AuthController authController;

    @GetMapping("/summary")
    public ResponseEntity<?> getSummary(@RequestHeader("Authorization") String token) {
        Long userId = authController.getUserIdByToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Login has expired. Please sign in again."));
        }
        return ResponseEntity.ok(studyCheckInService.getSummary(userId));
    }

    @PostMapping("/today")
    public ResponseEntity<?> checkIn(
            @RequestHeader("Authorization") String token,
            @RequestBody(required = false) Map<String, Object> payload
    ) {
        Long userId = authController.getUserIdByToken(token);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Login has expired. Please sign in again."));
        }

        Integer studyMinutes = parseStudyMinutes(payload);
        Object noteValue = payload == null ? null : payload.get("note");
        String note = noteValue == null ? "" : String.valueOf(noteValue);
        return ResponseEntity.ok(studyCheckInService.checkIn(userId, studyMinutes, note));
    }

    private Integer parseStudyMinutes(Map<String, Object> payload) {
        if (payload == null || payload.get("studyMinutes") == null) {
            return null;
        }
        Object value = payload.get("studyMinutes");
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
