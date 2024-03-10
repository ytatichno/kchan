package com.maxdev.kchan.repo;

import com.maxdev.kchan.models.Section;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by ytati
 * on 05.03.2024.
 */
public interface SectionsRepository extends JpaRepository<Section, Integer> {
}
