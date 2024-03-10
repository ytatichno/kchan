package com.maxdev.kchan.repo;

import com.maxdev.kchan.models.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by ytati
 * on 07.03.2024.
 */
public interface TopicsRepository extends JpaRepository<Topic, Integer> {
    Page<Topic> findAllBySectionId(Integer section, Pageable pageable);
}
