package ca.bc.gov.educ.api.gradstatus.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ca.bc.gov.educ.api.gradstatus.model.entity.GraduationStatusEntity;

@Repository
public interface GraduationStatusRepository extends JpaRepository<GraduationStatusEntity, UUID> {

    List<GraduationStatusEntity> findAll();

	List<GraduationStatusEntity> findByRecalculateGradStatus(String recalulateFlag);

	@Query("select c from GraduationStatusEntity c where c.studentStatus=:statusCode")
	List<GraduationStatusEntity> existsByStatusCode(String statusCode);
}
