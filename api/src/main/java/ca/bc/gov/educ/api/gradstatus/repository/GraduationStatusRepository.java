package ca.bc.gov.educ.api.gradstatus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ca.bc.gov.educ.api.gradstatus.model.entity.GraduationStatusEntity;

@Repository
public interface GraduationStatusRepository extends JpaRepository<GraduationStatusEntity, String> {

    List<GraduationStatusEntity> findAll();
}
