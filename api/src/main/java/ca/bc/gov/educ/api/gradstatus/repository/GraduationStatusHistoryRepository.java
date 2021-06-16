package ca.bc.gov.educ.api.gradstatus.repository;

import ca.bc.gov.educ.api.gradstatus.model.entity.GraduationStatusHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GraduationStatusHistoryRepository extends JpaRepository<GraduationStatusHistoryEntity, UUID> {

}
