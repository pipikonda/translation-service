package com.pipikonda.translationbot.repository;

import com.pipikonda.translationbot.domain.TimePeriod;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TimePeriodRepository extends CrudRepository<TimePeriod, Long> {

    List<TimePeriod> findByUserId(Long userId);

}
