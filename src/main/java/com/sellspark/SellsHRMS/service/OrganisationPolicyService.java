package com.sellspark.SellsHRMS.service;


import java.util.Optional;

import com.sellspark.SellsHRMS.dto.organisation.OrganisationPolicyDTO;
import com.sellspark.SellsHRMS.entity.OrganisationPolicy;

public interface OrganisationPolicyService {

    OrganisationPolicy createOrUpdatePolicy(Long orgId, OrganisationPolicyDTO policyDTO);

    Optional<OrganisationPolicy> getPolicyForOrg(Long orgId);

    OrganisationPolicyDTO getOrganisationPolicyByOrgId(Long orgId);

    // Integer getMaxLeavesPerYear(Long orgId);

    boolean isLeaveEncashmentEnabled(Long orgId);

    // boolean isHolidayCountedInLeave(Long orgId);

    int getAutoPunchOutAfterHours(Long orgId);
}
