package ca.bc.gov.educ.api.gradstatus.service;

import ca.bc.gov.educ.api.gradstatus.model.entity.GraduationStatusEntity;
import ca.bc.gov.educ.api.gradstatus.model.entity.GraduationStatusHistoryEntity;
import ca.bc.gov.educ.api.gradstatus.repository.GraduationStatusHistoryRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
public class GraduationStatusHistoryService {

    private static final String CREATED_TIMESTAMP = "createdTimestamp";
    private static final String UPDATED_TIMESTAMP = "updatedTimestamp";

    private final GraduationStatusHistoryRepository graduationStatusHistoryRepository;

    public GraduationStatusHistoryService(GraduationStatusHistoryRepository graduationStatusHistoryRepository) {
        this.graduationStatusHistoryRepository = graduationStatusHistoryRepository;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public GraduationStatusHistoryEntity createGraduationStatusHistory(GraduationStatusEntity graduationStatusEntity) {
        final GraduationStatusHistoryEntity history = new GraduationStatusHistoryEntity();
        BeanUtils.copyProperties(graduationStatusEntity, history, CREATED_TIMESTAMP, UPDATED_TIMESTAMP);
        history.setCreatedTimestamp(new Date(System.currentTimeMillis()));
        history.setUpdatedTimestamp(new Date(System.currentTimeMillis()));
        history.setHistoryID(UUID.randomUUID());
        return graduationStatusHistoryRepository.save(history);
    }
}
