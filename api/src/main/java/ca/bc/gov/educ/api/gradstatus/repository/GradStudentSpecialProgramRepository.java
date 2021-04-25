package ca.bc.gov.educ.api.gradstatus.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ca.bc.gov.educ.api.gradstatus.model.entity.GradStudentSpecialProgramEntity;

@Repository
public interface GradStudentSpecialProgramRepository extends JpaRepository<GradStudentSpecialProgramEntity, UUID> {

	List<GradStudentSpecialProgramEntity> findByStudentID(UUID studentID);
	Optional<GradStudentSpecialProgramEntity> findByStudentIDAndSpecialProgramID(UUID studentID,UUID specialProgramID);
}
