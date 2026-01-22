package com.sellspark.SellsHRMS.service;

import com.sellspark.SellsHRMS.entity.Leave;
import com.sellspark.SellsHRMS.entity.Employee;

public interface NotificationService {

    void notifyLeaveApplied(Leave leave, Employee approver);

    void notifyLeaveApproved(Leave leave);

    void notifyLeaveRejected(Leave leave);

    void notifyLeaveCancelled(Leave leave);
}