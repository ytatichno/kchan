package com.maxdev.kchan.repo;

import com.maxdev.kchan.models.Section;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by ytati
 * on 09.03.2024.
 */
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class SectionsRepositoryTest {
    @Autowired
    SectionsRepository sectionsRepository;

    @Test
    public void SectionsRepository_Save_ReturnSavedSection() {
        Section section = new Section();
        section.setName("section1");
        section.setDescription("descr");

        sectionsRepository.save(section);

        Optional<Section> savedSection = sectionsRepository.findById(section.getId());
        assertTrue(savedSection.isPresent());
        assertEquals(savedSection.get().getName(), section.getName());
        assertEquals(savedSection.get().getDescription(), section.getDescription());
    }

    @Test
    public void SectionsRepository_FindAll_ReturnAllSections() {
        Section section1 = new Section();
        section1.setName("section1");
        sectionsRepository.save(section1);
        Section section2 = new Section();
        section2.setName("section2");
        sectionsRepository.save(section2);

        List<Section> sectionList = sectionsRepository.findAll();

        assertEquals(sectionList.size(), 2);
        assertAll("find same sections",
                () -> assertEquals(sectionList.get(0).getName(), "section1"),
                () -> assertEquals(sectionList.get(1).getName(), "section2")
        );
    }

    @Test
    public void SectionsRepository_FindAllByPageable_ReturnAllSectionsPaged() {
        Section section1 = new Section();
        section1.setName("section1");
        sectionsRepository.save(section1);
        Section section2 = new Section();
        section2.setName("section2");
        sectionsRepository.save(section2);
        Section section3 = new Section();
        section3.setName("section3");
        sectionsRepository.save(section3);
        Section section4 = new Section();
        section4.setName("section4");
        sectionsRepository.save(section4);

        List<Section> sectionList = sectionsRepository.findAll(PageRequest.of(1, 2)).toList();

        assertEquals(sectionList.size(), 2);
        assertAll("find same sections",
                () -> assertEquals(sectionList.get(0).getName(), "section3"),
                () -> assertEquals(sectionList.get(1).getName(), "section4")
        );
    }

    @Test
    public void SectionsRepository_ExistsById_ReturnTrueForSavedSection() {
        Section section1 = new Section();
        section1.setName("section1");
        sectionsRepository.save(section1);
        Section section2 = new Section();
        section2.setName("section2");
        sectionsRepository.save(section2);

        Section section3 = new Section();
        section3.setName("section3");

        Section section4 = new Section();
        section4.setName("section4");
        sectionsRepository.save(section4);

        boolean exists1 = section1.getId() != null && sectionsRepository.existsById(section1.getId());
        boolean exists2 = section2.getId() != null && sectionsRepository.existsById(section2.getId());
        // this must have null id, therefore false
        boolean exists3 = section3.getId() != null && sectionsRepository.existsById(section3.getId());
        boolean exists4 = section4.getId() != null && sectionsRepository.existsById(section4.getId());

        assertAll("saved sections really exists",
                () -> {
                    assertTrue(exists1);

                    Optional<Section> savedSection = sectionsRepository.findById(section1.getId());
                    assertTrue(savedSection.isPresent());
                    assertEquals(section1, savedSection.get());
                },
                () -> {
                    assertTrue(exists2);

                    Optional<Section> savedSection = sectionsRepository.findById(section2.getId());
                    assertTrue(savedSection.isPresent());
                    assertEquals(section2, savedSection.get());
                },
                () -> assertFalse(exists3),
                () -> {
                    assertTrue(exists4);

                    Optional<Section> savedSection = sectionsRepository.findById(section4.getId());
                    assertTrue(savedSection.isPresent());
                    assertEquals(section4, savedSection.get());
                }
        );

    }

    @Test
    public void SectionsRepository_FindById_ReturnSectionWithSameId() {
        Section section1 = new Section();
        section1.setName("section1");
        sectionsRepository.save(section1);

        Optional<Section> savedSection = sectionsRepository.findById(section1.getId());

        assertTrue(savedSection.isPresent());
        assertEquals(section1, savedSection.get());
    }

    @Test
    public void SectionsRepository_DeleteById_ReturnVoid() {
        Section section1 = new Section();
        section1.setName("section1");
        sectionsRepository.save(section1);

        sectionsRepository.deleteById(section1.getId());

        Optional<Section> deletedSection = sectionsRepository.findById(section1.getId());
        assertFalse(deletedSection.isPresent());
    }
}
