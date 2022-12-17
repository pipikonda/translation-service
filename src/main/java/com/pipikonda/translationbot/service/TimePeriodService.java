package com.pipikonda.translationbot.service;

import com.pipikonda.translationbot.domain.TimePeriod;
import com.pipikonda.translationbot.error.BasicLogicException;
import com.pipikonda.translationbot.error.ErrorCode;
import com.pipikonda.translationbot.repository.TimePeriodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimePeriodService {

    private final TimePeriodRepository timePeriodRepository;

    public TimePeriod add(Long userId, LocalTime startTime, LocalTime endTime) {
        if (startTime.isAfter(endTime)) {
            throw new BasicLogicException(ErrorCode.VALIDATION_ERROR, "startTime is before than endTime");
        }

        return timePeriodRepository.save(TimePeriod.builder()
                .userId(userId)
                .startTime(startTime)
                .endTime(endTime)
                .build());
    }

    public List<TimePeriod> findByUserId(Long userId) {
        return timePeriodRepository.findByUserId(userId);
    }

    @Transactional
    public void delete(Long id) {
        TimePeriod timePeriod = timePeriodRepository.findById(id)
                .orElseThrow(() -> new BasicLogicException(ErrorCode.NOT_FOUND, "Not found time period by id " + id));
        timePeriodRepository.delete(timePeriod);
    }

    public boolean checkTimeBetween(List<TimePeriod> periods, LocalTime time) {
        return periods.stream()
                .filter(e -> time.isAfter(e.getStartTime()) || time == e.getStartTime())
                .anyMatch(e -> time.isBefore(e.getEndTime()) || time == e.getEndTime());
    }

}
