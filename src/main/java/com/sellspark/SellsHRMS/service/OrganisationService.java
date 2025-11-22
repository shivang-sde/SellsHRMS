package com.sellspark.SellsHRMS.service;

import java.util.List;
import java.util.Optional;

import com.sellspark.SellsHRMS.entity.Organisation;

public interface OrganisationService {

    Organisation create(Organisation organisation);

    Optional<Organisation> getById(Long id);

    List<Organisation> getAll();

    Organisation update(Long id, Organisation updated);

    void delete(Long id);
}
