package com.sellspark.SellsHRMS.repository;


import com.sellspark.SellsHRMS.entity.TicketActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TicketActivityRepository extends JpaRepository<TicketActivity, Long> {

    /**
     * Get full chronological activity log for a ticket.
     */
    List<TicketActivity> findByTicketIdOrderByCreatedAtAsc(Long ticketId);


    List<TicketActivity> findByTicket_Id(Long id);

    /**
     * Optional: get activity for a ticket by employee.
     */
    List<TicketActivity> findByTicketIdAndEmployeeIdOrderByCreatedAtAsc(Long ticketId, Long employeeId);

    /**
     * Optional: recent activities for dashboard widgets (e.g., “Recent Updates”).
     */
    List<TicketActivity> findTop10ByOrderByCreatedAtDesc();


    
}
