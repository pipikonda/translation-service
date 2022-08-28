package com.pipikonda.translationbot.repository;

import com.pipikonda.translationbot.domain.Repeat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepeatRepository extends JpaRepository<Repeat, Long> {
}
