package be.pxl.activity.repository;

import be.pxl.activity.domain.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    @Query("SELECT a FROM Activity a WHERE a.user.email = :email")
    List<Activity> findByUserEmail(String email);

    @Query("SELECT a FROM Activity a WHERE a.user.email = :email AND a.activity = :sport")
    List<Activity> findByUserEmailAndSport(String email, String sport);

}

