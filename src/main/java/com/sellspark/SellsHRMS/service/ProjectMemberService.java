package com.sellspark.SellsHRMS.service;

import java.util.List;

import com.sellspark.SellsHRMS.dto.project.ProjectMemberDTO;

public interface ProjectMemberService {


    ProjectMemberDTO addMember(ProjectMemberDTO dto, Long organisationId, Long actorEmpId);

    ProjectMemberDTO updateMember(Long memberId, ProjectMemberDTO dto, Long organisationId, Long actorEmpId);

    void removeMember(Long memberId, Long organisationId, Long actorEmpId);

    ProjectMemberDTO getMemberById(Long memberId, Long organisationId);

    List<ProjectMemberDTO> getMembersByProject(Long projectId, Long organisationId);

    List<ProjectMemberDTO> getMembersByEmployee(Long employeeId, Long organisationId);
    
}