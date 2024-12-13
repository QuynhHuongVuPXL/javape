package be.pxl.activity.repository;

import be.pxl.activity.domain.Sleep;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SleepRepository extends JpaRepository<Sleep, Long> {
    List<Sleep> findByUserEmail(String email);
}
