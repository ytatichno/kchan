package com.maxdev.kchan.repo;

import com.maxdev.kchan.models.Usercard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Created by ytati
 * on 05.03.2024.
 */
public interface UsercardsRepository extends JpaRepository<Usercard, Integer> {
    Optional<Usercard> findUsercardByNick(String nick);

    /**
     * it takes list of all topics of this section,
     * then takes all messages with topic(id) in the list,
     * then group it by author and aggregates sum,
     * then sorts it descending by aggregated sum,
     * then takes authors' usercards from this new list
     * TODO super test coverage
     *
     * @return [[..Usercard, activity]]
     */
    @Query(value = "SELECT * " +
            "FROM usercards u " +
            "JOIN ( " +
            "       SELECT m.author, COUNT(m.author) as activity " +
            "       FROM messages m " +
            "       JOIN topics t ON m.topic = t.id" +
            "           WHERE t.section = :section" +
            "       GROUP BY m.author" +
            "       ORDER BY activity desc" +
            "       LIMIT :limit OFFSET :offset" +
            ") as stats(author, activity) ON u.id = stats.author",
            nativeQuery = true
    )
    ArrayList<Pair<Usercard, Integer>> findAllActiveUsersNative(Integer section, Integer limit, Integer offset);
}


