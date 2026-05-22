package com.nchu.learningplatform.service;

import java.util.Map;

public interface StudyCheckInService {

    Map<String, Object> getSummary(Long userId);

    Map<String, Object> checkIn(Long userId, Integer studyMinutes, String note);
}
