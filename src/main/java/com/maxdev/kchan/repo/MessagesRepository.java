package com.maxdev.kchan.repo;

import com.maxdev.kchan.models.Message;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by ytati
 * on 07.03.2024.
 */
public interface MessagesRepository extends JpaRepository<Message, Long> {
    Page<Message> findAllByTopicId(@NotNull Integer topic, Pageable pageable);
    Long countAllByTopicId(@NotNull Integer topic);
}
