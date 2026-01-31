package com.sellspark.SellsHRMS.exception;

import org.springframework.http.HttpStatus;

import com.sellspark.SellsHRMS.exception.core.HRMSException;

/**
 * Exception thrown when an employee is not a member of a specific project.
 */
public class ProjectMemberNotFoundException extends HRMSException {

    public ProjectMemberNotFoundException(Long employeeId, Long projectId) {
        super(
                String.format("Employee with ID %d is not a member of Project with ID %d", employeeId, projectId),
                "PROJECT_MEMBER_NOT_FOUND",
                HttpStatus.FORBIDDEN);
    }

    public ProjectMemberNotFoundException(String message) {
        super(
                message,
                "PROJECT_MEMBER_NOT_FOUND",
                HttpStatus.FORBIDDEN);
    }
}
