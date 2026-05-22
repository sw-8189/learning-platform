package com.nchu.learningplatform.service.impl;

import com.nchu.learningplatform.entity.StudyCheckIn;
import com.nchu.learningplatform.mapper.StudyCheckInMapper;
import com.nchu.learningplatform.service.StudyCheckInService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class StudyCheckInServiceImpl implements StudyCheckInService {

    @Resource
    private StudyCheckInMapper studyCheckInMapper;

    @Override
    public Map<String, Object> getSummary(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusWeeks(13).with(DayOfWeek.MONDAY);
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = weekStart.plusDays(6);

        List<StudyCheckIn> checkIns = studyCheckInMapper.findByUserIdBetweenDates(userId, startDate, today);
        Set<LocalDate> checkedDates = new HashSet<>();
        Map<LocalDate, StudyCheckIn> checkInByDate = new HashMap<>();
        for (StudyCheckIn checkIn : checkIns) {
            checkedDates.add(checkIn.getCheckInDate());
            checkInByDate.put(checkIn.getCheckInDate(), checkIn);
        }

        boolean checkedToday = checkedDates.contains(today);
        boolean checkedYesterday = checkedDates.contains(today.minusDays(1));
        int streakDays = calculateStreakDays(userId, today, checkedToday);
        int thisWeekDays = studyCheckInMapper.countByUserIdBetweenDates(userId, weekStart, weekEnd);

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("checkedToday", checkedToday);
        summary.put("streakDays", streakDays);
        summary.put("thisWeekDays", thisWeekDays);
        summary.put("weekGoal", 5);
        summary.put("reminder", buildReminder(checkedToday, checkedYesterday, streakDays));
        summary.put("heatmap", buildHeatmap(startDate, today, checkInByDate));
        summary.put("weekDays", List.of("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"));
        return summary;
    }

    @Override
    public Map<String, Object> checkIn(Long userId, Integer studyMinutes, String note) {
        LocalDate today = LocalDate.now();
        StudyCheckIn existing = studyCheckInMapper.findByUserIdAndDate(userId, today);
        if (existing == null) {
            StudyCheckIn checkIn = new StudyCheckIn();
            checkIn.setUserId(userId);
            checkIn.setCheckInDate(today);
            checkIn.setStudyMinutes(normalizeStudyMinutes(studyMinutes));
            checkIn.setNote(normalizeNote(note));
            studyCheckInMapper.insert(checkIn);
        }
        return getSummary(userId);
    }

    private int calculateStreakDays(Long userId, LocalDate today, boolean checkedToday) {
        List<StudyCheckIn> allCheckIns = studyCheckInMapper.findByUserIdOrderByDateDesc(userId);
        Set<LocalDate> checkedDates = new HashSet<>();
        for (StudyCheckIn checkIn : allCheckIns) {
            checkedDates.add(checkIn.getCheckInDate());
        }

        LocalDate cursor = checkedToday ? today : today.minusDays(1);
        int streak = 0;
        while (checkedDates.contains(cursor)) {
            streak++;
            cursor = cursor.minusDays(1);
        }
        return streak;
    }

    private List<Map<String, Object>> buildHeatmap(
            LocalDate startDate,
            LocalDate today,
            Map<LocalDate, StudyCheckIn> checkInByDate
    ) {
        List<Map<String, Object>> heatmap = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(today); date = date.plusDays(1)) {
            StudyCheckIn checkIn = checkInByDate.get(date);
            Map<String, Object> day = new LinkedHashMap<>();
            day.put("date", date.toString());
            day.put("checked", checkIn != null);
            day.put("studyMinutes", checkIn == null ? 0 : checkIn.getStudyMinutes());
            day.put("weekday", date.getDayOfWeek().getValue());
            heatmap.add(day);
        }
        return heatmap;
    }

    private String buildReminder(boolean checkedToday, boolean checkedYesterday, int streakDays) {
        if (checkedToday) {
            return "Checked in today. Keep the momentum going tomorrow.";
        }
        if (streakDays > 0 || checkedYesterday) {
            return "You have not checked in today. Check in after learning to protect your streak.";
        }
        return "Your streak is currently broken. Start a new run with today's study session.";
    }

    private int normalizeStudyMinutes(Integer studyMinutes) {
        if (studyMinutes == null || studyMinutes < 0) {
            return 0;
        }
        return Math.min(studyMinutes, 1440);
    }

    private String normalizeNote(String note) {
        if (note == null) {
            return "";
        }
        String trimmed = note.trim();
        return trimmed.length() > 255 ? trimmed.substring(0, 255) : trimmed;
    }
}
