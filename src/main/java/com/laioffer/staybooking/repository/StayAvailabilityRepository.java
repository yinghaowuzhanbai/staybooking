package com.laioffer.staybooking.repository;

import com.laioffer.staybooking.model.StayAvailability;
import com.laioffer.staybooking.model.StayAvailabilityKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface StayAvailabilityRepository extends JpaRepository<StayAvailability, StayAvailabilityKey> {
//   HAVING COUNT(sa.id.date) = ?4 重复出现的次数，看是不是“2- 4号每天都可以用“  "?2" 和“？3”指的是参数start date 和end date
//    sa.id.stay 和 sa.state 为什么有一个点和两个点的区别，因为hibernate
    @Query(value = "SELECT sa.id.stay_id FROM StayAvailability sa WHERE sa.id.stay_id IN ?1 AND sa.state = 0 AND sa.id.date BETWEEN ?2 AND ?3 GROUP BY sa.id.stay_id HAVING COUNT(sa.id.date) = ?4")
    List<Long> findByDateBetweenAndStateIsAvailable(List<Long> stayIds, LocalDate startDate, LocalDate endDate, long duration);
}