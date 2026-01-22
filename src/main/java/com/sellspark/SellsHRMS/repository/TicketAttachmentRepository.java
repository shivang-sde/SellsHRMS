package com.sellspark.SellsHRMS.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


import com.sellspark.SellsHRMS.entity.TicketAttachment;

public interface TicketAttachmentRepository extends JpaRepository<TicketAttachment, Long> {

    List<TicketAttachment> findByTicketId(Long taskId);

    //  List<TicketAttachment> findByCommentId(Long commentId);

     List<TicketAttachment> findByTicket_Id(Long ticketId);

    void deleteByTicket_Id(Long taskId);
    
} 