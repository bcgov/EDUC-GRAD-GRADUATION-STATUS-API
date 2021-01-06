package ca.bc.gov.educ.api.gradstatus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ca.bc.gov.educ.api.gradstatus.model.entity.GraduationStatusEntity;

@Repository
public interface GraduationStatusRepository extends JpaRepository<GraduationStatusEntity, String> {

    List<GraduationStatusEntity> findAll();

    @Query("select c from GraduationStatusEntity c where c.certificateType1=:certificateType or c.certificateType2=:certificateType")
	List<GraduationStatusEntity> existsByCertificateType(String certificateType);

}
