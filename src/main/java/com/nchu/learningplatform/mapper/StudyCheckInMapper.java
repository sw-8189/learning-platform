package com.nchu.learningplatform.mapper;

import com.nchu.learningplatform.entity.StudyCheckIn;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface StudyCheckInMapper {

    int insert(StudyCheckIn checkIn);

    StudyCheckIn findByUserIdAndDate(@Param("userId") Long userId, @Param("checkInDate") LocalDate checkInDate);

    List<StudyCheckIn> findByUserIdBetweenDates(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    List<StudyCheckIn> findByUserIdOrderByDateDesc(@Param("userId") Long userId);

    int countByUserIdBetweenDates(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
