package com.maxdev.kchan.repo;

import com.maxdev.kchan.models.Usercard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by ytati
 * on 05.03.2024.
 */
public interface UsercardsRepository extends JpaRepository<Usercard, Integer> {
    Optional<Usercard> findUsercardByNick(String nick);

    boolean existsByNick(String nick);

    /**
     * it takes list of all topics of this section,
     * then takes all messages with topic(id) in the list,
     * then group it by author and aggregates sum,
     * then sorts it descending by aggregated sum,
     * then takes authors' usercards from this new list
     *
     * @return [[..Usercard, activity]]
     */
    @Query(value = "SELECT * " +
            "FROM usercards u " +
            "JOIN ( " +
            "       SELECT m.author, COUNT(m.author) as activity " +
            "       FROM messages m " +
            "       JOIN topics t ON m.topic = t.id" +
            "       WHERE t.section = :section" +
            "       GROUP BY m.author" +
//            "       LIMIT :limit + " +
            ") as stats(author, activity) ON u.id = stats.author " +
            "LEFT JOIN ( " +  // and exclude those already moders
            "            SELECT moder_id, section_id" +
            "            FROM sections_moders" +
            "            WHERE section_id = :section" +
            ") as moders(author, section) ON u.id = moders.author " +
            "WHERE moders.section is NULL AND u.is_admin is NOT TRUE " +
            "ORDER BY activity DESC " +
            "LIMIT :limit OFFSET :offset",
            nativeQuery = true
    )
    List<Map<String, Object>> findAllActiveUsersNative(Integer section, Integer limit, Integer offset);

    @Query(value = "SELECT COUNT(*) " +
            "FROM usercards u " +
            "JOIN ( " +
//            "       SELECT m.author, COUNT(m.author) as activity " +
            "       SELECT m.author" +
            "       FROM messages m " +
            "       JOIN topics t ON m.topic = t.id" +
            "       WHERE t.section = :section" +
            "       GROUP BY m.author" +
//            "       LIMIT :limit OFFSET :offset" +
            ") as stats(author) ON u.id = stats.author " +
            "LEFT JOIN ( " +  // and exclude those already moders
            "            SELECT moder_id" +  // todo add
            "            FROM sections_moders" +
            "            WHERE section_id = :section" +
            ") as moders(author) ON u.id = moders.author " +
            "WHERE moders.author is NULL AND u.is_admin is NOT TRUE ",  // todo fix
            nativeQuery = true
    )
    Long countAllActiveUsersNative(Integer section);

    @Query(value = """
            SELECT * 
            FROM usercards u  
            JOIN (  
                   SELECT sm.moder_id  
                   FROM sections_moders sm  
                   WHERE sm.section_id = :section 
                   LIMIT :limit OFFSET :offset  
            ) as moders(moder_id) ON u.id = moders.moder_id  
            """,
            nativeQuery = true
    )
    List<Usercard> findModersNative(Integer section, Integer limit, Integer offset);

    @Query(value = """
            DELETE
            FROM sections_moders sm
            WHERE sm.section_id = :moderable AND sm.moder_id = :toDisrank
            RETURNING *
        """,
            nativeQuery = true
    )
    Object disrankModer(Integer moderable, Integer toDisrank);
}


