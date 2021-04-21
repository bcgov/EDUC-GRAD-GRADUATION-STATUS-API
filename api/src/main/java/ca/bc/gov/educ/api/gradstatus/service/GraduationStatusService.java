package ca.bc.gov.educ.api.gradstatus.service;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import ca.bc.gov.educ.api.gradstatus.model.dto.GradProgram;
import ca.bc.gov.educ.api.gradstatus.model.dto.GradSpecialProgram;
import ca.bc.gov.educ.api.gradstatus.model.dto.GradStudentSpecialProgram;
import ca.bc.gov.educ.api.gradstatus.model.dto.GraduationStatus;
import ca.bc.gov.educ.api.gradstatus.model.dto.School;
import ca.bc.gov.educ.api.gradstatus.model.dto.StudentStatus;
import ca.bc.gov.educ.api.gradstatus.model.entity.GradStudentSpecialProgramEntity;
import ca.bc.gov.educ.api.gradstatus.model.entity.GraduationStatusEntity;
import ca.bc.gov.educ.api.gradstatus.model.transformer.GradStudentSpecialProgramTransformer;
import ca.bc.gov.educ.api.gradstatus.model.transformer.GraduationStatusTransformer;
import ca.bc.gov.educ.api.gradstatus.repository.GradStudentSpecialProgramRepository;
import ca.bc.gov.educ.api.gradstatus.repository.GraduationStatusRepository;
import ca.bc.gov.educ.api.gradstatus.util.EducGradStatusApiConstants;
import ca.bc.gov.educ.api.gradstatus.util.GradValidation;


@Service
public class GraduationStatusService {

	private static Logger logger = LoggerFactory.getLogger(GraduationStatusService.class);

	@Autowired
    WebClient webClient;
	
	@Autowired
    RestTemplate restTemplate;

    @Autowired
    private GraduationStatusRepository graduationStatusRepository;

    @Autowired
    private GraduationStatusTransformer graduationStatusTransformer;
    
    @Autowired
    private GradStudentSpecialProgramRepository gradStudentSpecialProgramRepository;

    @Autowired
    private GradStudentSpecialProgramTransformer gradStudentSpecialProgramTransformer;
    
    @Autowired
	GradValidation validation;
    
    @Value(EducGradStatusApiConstants.ENDPOINT_GRAD_SPECIAL_PROGRAM_NAME_URL)
    private String getGradSpecialProgramName;
    
    @Value(EducGradStatusApiConstants.ENDPOINT_GRAD_SPECIAL_PROGRAM_DETAILS_URL)
    private String getGradSpecialProgramDetails;
    
    @Value(EducGradStatusApiConstants.ENDPOINT_GRAD_PROGRAM_NAME_URL)
    private String getGradProgramName;
    
    @Value(EducGradStatusApiConstants.ENDPOINT_GRAD_SCHOOL_NAME_URL)
    private String getGradSchoolName;
    
    @Value(EducGradStatusApiConstants.ENDPOINT_STUDENT_STATUS_URL)
    private String getStudentStatusName;
    
    
    

	public GraduationStatus getGraduationStatus(String pen,String accessToken) {
		logger.info("getGraduationStatus");
		Optional<GraduationStatusEntity> responseOptional = graduationStatusRepository.findById(pen);
		if(responseOptional.isPresent()) {
			GraduationStatus gradStatus =  graduationStatusTransformer.transformToDTO(responseOptional.get());
			if(gradStatus.getProgram() != null) {
				GradProgram gradProgram = webClient.get().uri(String.format(getGradProgramName,gradStatus.getProgram())).headers(h -> h.setBearerAuth(accessToken)).retrieve().bodyToMono(GradProgram.class).block();
				gradStatus.setProgramName(gradProgram.getProgramName());
			}
			if(gradStatus.getSchoolOfRecord() != null) {
				School schObj = webClient.get().uri(String.format(getGradSchoolName,gradStatus.getSchoolOfRecord())).headers(h -> h.setBearerAuth(accessToken)).retrieve().bodyToMono(School.class).block();
				gradStatus.setSchoolName(schObj.getSchoolName());
			}
			
			if(gradStatus.getStudentStatus() != null) {
				StudentStatus statusObj = webClient.get().uri(String.format(getStudentStatusName,gradStatus.getStudentStatus())).headers(h -> h.setBearerAuth(accessToken)).retrieve().bodyToMono(StudentStatus.class).block();
				gradStatus.setSchoolName(statusObj.getDescription());
			}
			return gradStatus;
		}else {
			return null;
		}
	    
	}

	public GraduationStatus saveGraduationStatus(String pen, GraduationStatus graduationStatus) {
		Optional<GraduationStatusEntity> gradStatusOptional = graduationStatusRepository.findById(pen);
		GraduationStatusEntity sourceObject = graduationStatusTransformer.transformToEntity(graduationStatus);
		if(gradStatusOptional.isPresent()) {
			GraduationStatusEntity gradEnity = gradStatusOptional.get();			
			BeanUtils.copyProperties(sourceObject,gradEnity,"createdBy","createdTimestamp","studentGrade","studentStatus");
			gradEnity.setProgramCompletionDate(sourceObject.getProgramCompletionDate());
			return graduationStatusTransformer.transformToDTO(graduationStatusRepository.save(gradEnity));
		}else {
			return graduationStatusTransformer.transformToDTO(graduationStatusRepository.save(sourceObject));
		}
	}
	
	public GraduationStatus updateGraduationStatus(String pen, GraduationStatus graduationStatus) {
		Optional<GraduationStatusEntity> gradStatusOptional = graduationStatusRepository.findById(pen);
		GraduationStatusEntity sourceObject = graduationStatusTransformer.transformToEntity(graduationStatus);
		if(gradStatusOptional.isPresent()) {
			GraduationStatusEntity gradEnity = gradStatusOptional.get();			
			BeanUtils.copyProperties(sourceObject,gradEnity,"createdBy","createdTimestamp","studentGradData");
			gradEnity.setProgramCompletionDate(sourceObject.getProgramCompletionDate());
			return graduationStatusTransformer.transformToDTO(graduationStatusRepository.save(gradEnity));
		}else {
			validation.addErrorAndStop(String.format("Pen [%s] does not exists",pen));
			return graduationStatus;
		}
	}

	public List<GradStudentSpecialProgram> getStudentGradSpecialProgram(String pen,String accessToken) {
		List<GradStudentSpecialProgram> specialProgramList = gradStudentSpecialProgramTransformer.transformToDTO(gradStudentSpecialProgramRepository.findByPen(pen));
		specialProgramList.forEach(sP -> {
			GradSpecialProgram gradSpecialProgram = webClient.get().uri(String.format(getGradSpecialProgramName,sP.getSpecialProgramID())).headers(h -> h.setBearerAuth(accessToken)).retrieve().bodyToMono(GradSpecialProgram.class).block();
			sP.setSpecialProgramName(gradSpecialProgram.getSpecialProgramName());
			sP.setSpecialProgramCode(gradSpecialProgram.getSpecialProgramCode());
			sP.setMainProgramCode(gradSpecialProgram.getProgramCode());
		});
		return specialProgramList;
	}

	public GradStudentSpecialProgram saveStudentGradSpecialProgram(GradStudentSpecialProgram gradStudentSpecialProgram) {
		Optional<GradStudentSpecialProgramEntity> gradStudentSpecialOptional = gradStudentSpecialProgramRepository.findById(gradStudentSpecialProgram.getId());
		GradStudentSpecialProgramEntity sourceObject = gradStudentSpecialProgramTransformer.transformToEntity(gradStudentSpecialProgram);
		if(gradStudentSpecialOptional.isPresent()) {
			GradStudentSpecialProgramEntity gradEnity = gradStudentSpecialOptional.get();			
			BeanUtils.copyProperties(sourceObject,gradEnity,"createdBy","createdTimestamp");
			gradEnity.setSpecialProgramCompletionDate(sourceObject.getSpecialProgramCompletionDate());
			return gradStudentSpecialProgramTransformer.transformToDTO(gradStudentSpecialProgramRepository.save(gradEnity));
		}else {
			return gradStudentSpecialProgramTransformer.transformToDTO(gradStudentSpecialProgramRepository.save(sourceObject));
		}
	}

	public List<GraduationStatus> getStudentsForGraduation() {
		return graduationStatusTransformer.transformToDTO(graduationStatusRepository.findByRecalculateGradStatus("Y"));
	}

	public GradStudentSpecialProgram getStudentGradSpecialProgramByProgramCodeAndSpecialProgramCode(String pen,String specialProgramID,String accessToken) {
		UUID specialProgramIDUUID = UUID.fromString(specialProgramID);
		Optional<GradStudentSpecialProgramEntity> gradStudentSpecialOptional = gradStudentSpecialProgramRepository.findByPenAndSpecialProgramID(pen,specialProgramIDUUID);
		if(gradStudentSpecialOptional.isPresent()) {
			GradStudentSpecialProgram responseObj= gradStudentSpecialProgramTransformer.transformToDTO(gradStudentSpecialOptional);
			GradSpecialProgram gradSpecialProgram = webClient.get().uri(String.format(getGradSpecialProgramName,responseObj.getSpecialProgramID())).headers(h -> h.setBearerAuth(accessToken)).retrieve().bodyToMono(GradSpecialProgram.class).block();
			responseObj.setSpecialProgramName(gradSpecialProgram.getSpecialProgramName());
			responseObj.setSpecialProgramCode(gradSpecialProgram.getSpecialProgramCode());
			responseObj.setMainProgramCode(gradSpecialProgram.getProgramCode());
			return responseObj;
		}
		return null;
	}

	public boolean getStudentStatus(String statusCode) {
		List<GraduationStatusEntity> gradList = graduationStatusRepository.existsByStatusCode(statusCode);
		if(gradList.size() > 0) {
			return true;
		}else {
			return false;
		}
	}
}
