package ca.bc.gov.educ.api.gradstatus.service;


import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import ca.bc.gov.educ.api.gradstatus.model.dto.GraduationStatus;
import ca.bc.gov.educ.api.gradstatus.model.entity.GraduationStatusEntity;
import ca.bc.gov.educ.api.gradstatus.model.transformer.GraduationStatusTransformer;
import ca.bc.gov.educ.api.gradstatus.repository.GraduationStatusRepository;

@Service
public class GraduationStatusService {

	private static Logger logger = LoggerFactory.getLogger(GraduationStatusService.class);

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private GraduationStatusRepository graduationStatusRepository;

    @Autowired
    private GraduationStatusTransformer graduationStatusTransformer;

	public GraduationStatus getGraduationStatus(String pen) {
		logger.info("getGraduationStatus");
		return graduationStatusTransformer.transformToDTO(graduationStatusRepository.findById(pen));
	    
	}

	public GraduationStatus saveGraduationStatus(String pen, GraduationStatus graduationStatus) {
		Optional<GraduationStatusEntity> gradStatusOptional = graduationStatusRepository.findById(pen);
		GraduationStatusEntity sourceObject = graduationStatusTransformer.transformToEntity(graduationStatus);
		if(gradStatusOptional.isPresent()) {
			GraduationStatusEntity gradEnity = gradStatusOptional.get();			
			BeanUtils.copyProperties(sourceObject,gradEnity,"createdBy","createdTimestamp");
			gradEnity.setGraduationDate(sourceObject.getGraduationDate());
			return graduationStatusTransformer.transformToDTO(graduationStatusRepository.save(gradEnity));
		}else {
			return graduationStatusTransformer.transformToDTO(graduationStatusRepository.save(sourceObject));
		}
	}
}
