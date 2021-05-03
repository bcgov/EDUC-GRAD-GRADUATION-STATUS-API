package ca.bc.gov.educ.api.gradstatus.service;


import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
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
import ca.bc.gov.educ.api.gradstatus.model.dto.Student;
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
    
    @Value(EducGradStatusApiConstants.ENDPOINT_PEN_STUDENT_API_BY_STUDENT_ID_URL)
    private String getPenStudentAPIByStudentIDURL;
    
    
    public GraduationStatus getGraduationStatusForAlgorithm(UUID studentID,String accessToken) {
		logger.info("getGraduationStatus");
		Optional<GraduationStatusEntity> responseOptional = graduationStatusRepository.findById(studentID);
		if(responseOptional.isPresent()) {
			GraduationStatus gradStatus =  graduationStatusTransformer.transformToDTO(responseOptional.get());
			return gradStatus;
		}else {
			return null;
		}
	    
	}
	public GraduationStatus getGraduationStatus(UUID studentID,String accessToken) {
		logger.info("getGraduationStatus");
		Optional<GraduationStatusEntity> responseOptional = graduationStatusRepository.findById(studentID);
		if(responseOptional.isPresent()) {
			GraduationStatus gradStatus =  graduationStatusTransformer.transformToDTO(responseOptional.get());
			if(gradStatus.getProgram() != null) {
				GradProgram gradProgram = webClient.get().uri(String.format(getGradProgramName,gradStatus.getProgram())).headers(h -> h.setBearerAuth(accessToken)).retrieve().bodyToMono(GradProgram.class).block();
				if(gradProgram != null)
					gradStatus.setProgramName(gradProgram.getProgramName());
			}
			if(gradStatus.getSchoolOfRecord() != null) {
				School schObj = webClient.get().uri(String.format(getGradSchoolName,gradStatus.getSchoolOfRecord())).headers(h -> h.setBearerAuth(accessToken)).retrieve().bodyToMono(School.class).block();
				if(schObj != null)
					gradStatus.setSchoolName(schObj.getSchoolName());
			}
			
			if(gradStatus.getStudentStatus() != null) {
				StudentStatus statusObj = webClient.get().uri(String.format(getStudentStatusName,gradStatus.getStudentStatus())).headers(h -> h.setBearerAuth(accessToken)).retrieve().bodyToMono(StudentStatus.class).block();
				if(statusObj != null)
					gradStatus.setStudentStatusName(statusObj.getDescription());
			}
			
			if(gradStatus.getSchoolOfRecord() != null) {
				School schObj = webClient.get().uri(String.format(getGradSchoolName,gradStatus.getSchoolAtGrad())).headers(h -> h.setBearerAuth(accessToken)).retrieve().bodyToMono(School.class).block();
				if(schObj != null)
					gradStatus.setSchoolAtGradName(schObj.getSchoolName());
			}
			return gradStatus;
		}else {
			return null;
		}
	    
	}

	public GraduationStatus saveGraduationStatus(UUID studentID, GraduationStatus graduationStatus) {
		Optional<GraduationStatusEntity> gradStatusOptional = graduationStatusRepository.findById(studentID);
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
	
	public GraduationStatus updateGraduationStatus(UUID studentID, GraduationStatus graduationStatus,String accessToken) {
		Optional<GraduationStatusEntity> gradStatusOptional = graduationStatusRepository.findById(studentID);
		GraduationStatusEntity sourceObject = graduationStatusTransformer.transformToEntity(graduationStatus);
		if(gradStatusOptional.isPresent()) {
			GraduationStatusEntity gradEnity = gradStatusOptional.get();
			boolean hasDataChanged = validateData(sourceObject,gradEnity,accessToken);			
			if(validation.hasErrors()) {
				validation.stopOnErrors();
				return new GraduationStatus();
			}
			if(hasDataChanged) {
				gradEnity.setRecalculateGradStatus("Y");	
			}else {
				gradEnity.setRecalculateGradStatus(null);
			}
			BeanUtils.copyProperties(sourceObject,gradEnity,"createdBy","createdTimestamp","studentGradData","recalculateGradStatus");
			gradEnity.setProgramCompletionDate(sourceObject.getProgramCompletionDate());
			return graduationStatusTransformer.transformToDTO(graduationStatusRepository.save(gradEnity));
		}else {
			validation.addErrorAndStop(String.format("Student ID [%s] does not exists",studentID));
			return graduationStatus;
		}
	}

	private void validateStudentStatus(String studentStatus) {
		if(studentStatus.equalsIgnoreCase("M")) {
			validation.addErrorAndStop("Student GRAD data cannot be updated for students with a status of 'M' merged");
		}
		if(studentStatus.equalsIgnoreCase("T")) {
			validation.addErrorAndStop("This student has been terminated.  Re-activate by setting their status to 'A' if they are currently attending school");
		}
		if(studentStatus.equalsIgnoreCase("D")) {
			validation.addErrorAndStop("This student is showing as deceased.  Confirm the students' status before re-activating by setting their status to 'A' if they are currently attending school");
		}		
	}
	private boolean validateData(GraduationStatusEntity sourceEntity,GraduationStatusEntity existingEntity,String accessToken) {
		boolean hasDataChangd = false;
		validateStudentStatus(existingEntity.getStudentStatus());
		if(!sourceEntity.getProgram().equalsIgnoreCase(existingEntity.getProgram())) {
			hasDataChangd = true;
			GradProgram gradProgram = webClient.get().uri(String.format(getGradProgramName,sourceEntity.getProgram())).headers(h -> h.setBearerAuth(accessToken)).retrieve().bodyToMono(GradProgram.class).block();
			if(gradProgram == null) {
				validation.addError(String.format("Program [%s] is invalid",sourceEntity.getProgram()));
			}else {
				if(sourceEntity.getProgram().contains("1950")) {
					if(!sourceEntity.getStudentGrade().equalsIgnoreCase("AD") && !sourceEntity.getStudentGrade().equalsIgnoreCase("AN")) {
						validation.addWarning(String.format("Student grade should be one of AD or AN if the student program is [%s]",sourceEntity.getProgram()));
					}
				}else {
					if(sourceEntity.getStudentGrade().equalsIgnoreCase("AD") || sourceEntity.getStudentGrade().equalsIgnoreCase("AN")) {
						validation.addWarning(String.format("Student grade should not be AD or AN for this program [%s]",sourceEntity.getProgram()));
					}
				}
			}
		}
		if(!sourceEntity.getSchoolOfRecord().equalsIgnoreCase(existingEntity.getSchoolOfRecord())) {
			hasDataChangd = true;
			School schObj = webClient.get().uri(String.format(getGradSchoolName,sourceEntity.getSchoolOfRecord())).headers(h -> h.setBearerAuth(accessToken)).retrieve().bodyToMono(School.class).block();
			if(schObj == null) {
				validation.addError(String.format("Invalid School entered, School [%s] does not exist on the School table",sourceEntity.getSchoolOfRecord()));
			}else {
				if(schObj.getOpenFlag().equalsIgnoreCase("N")) {
					validation.addError(String.format("This School [%s] is Closed",sourceEntity.getSchoolOfRecord()));
				}
			}
		}
		if(!sourceEntity.getSchoolAtGrad().equalsIgnoreCase(existingEntity.getSchoolAtGrad())) {
			hasDataChangd = true;
			School schAtGradObj = webClient.get().uri(String.format(getGradSchoolName,sourceEntity.getSchoolAtGrad())).headers(h -> h.setBearerAuth(accessToken)).retrieve().bodyToMono(School.class).block();
			if(schAtGradObj == null) {
				validation.addError(String.format("Invalid School entered, School [%s] does not exist on the School table",sourceEntity.getSchoolAtGrad()));
			}else {
				if(schAtGradObj.getOpenFlag().equalsIgnoreCase("N")) {
					validation.addError(String.format("This School [%s] is Closed",sourceEntity.getSchoolAtGrad()));
				}
			}
		}
		if(!sourceEntity.getStudentGrade().equalsIgnoreCase(existingEntity.getStudentGrade()) || !sourceEntity.getStudentStatus().equalsIgnoreCase(existingEntity.getStudentStatus())) {
			hasDataChangd = true;
			Student studentObj = webClient.get().uri(String.format(getPenStudentAPIByStudentIDURL, sourceEntity.getStudentID())).headers(h -> h.setBearerAuth(accessToken)).retrieve().bodyToMono(Student.class).block();
			if(sourceEntity.getStudentStatus().equalsIgnoreCase("D") || sourceEntity.getStudentStatus().equalsIgnoreCase("M")) {
				if(!sourceEntity.getStudentStatus().equalsIgnoreCase(studentObj.getStatusCode())) {
					validation.addError("Status code selected is at odds with the PEN data for this student");
				}
			}else {
				if(!"A".equalsIgnoreCase(studentObj.getStatusCode())) {
					validation.addError("Status code selected is at odds with the PEN data for this student");
				}
			}
			
			if(sourceEntity.getStudentGrade().equalsIgnoreCase("AN") || sourceEntity.getStudentGrade().equalsIgnoreCase("AD")) {
				if(calculateAge(studentObj.getDob()) < 18) {
					validation.addError("Adult student should be at least 18 years old");
				}
			}
		}
		return hasDataChangd;
	}
	
	public List<GradStudentSpecialProgram> getStudentGradSpecialProgram(UUID studentID,String accessToken) {
		List<GradStudentSpecialProgram> specialProgramList = gradStudentSpecialProgramTransformer.transformToDTO(gradStudentSpecialProgramRepository.findByStudentID(studentID));
		specialProgramList.forEach(sP -> {
			GradSpecialProgram gradSpecialProgram = webClient.get().uri(String.format(getGradSpecialProgramName,sP.getSpecialProgramID())).headers(h -> h.setBearerAuth(accessToken)).retrieve().bodyToMono(GradSpecialProgram.class).block();
			sP.setSpecialProgramName(gradSpecialProgram.getSpecialProgramName());
			sP.setSpecialProgramCode(gradSpecialProgram.getSpecialProgramCode());
			sP.setMainProgramCode(gradSpecialProgram.getProgramCode());
		});
		return specialProgramList;
	}
	
	public int calculateAge(String dob) {
		DateTimeFormatter DATEFORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate birthDate = LocalDate.parse(dob, DATEFORMATTER);	   
		LocalDate currentDate = LocalDate.now();
	    return Period.between(birthDate, currentDate).getYears();
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

	public GradStudentSpecialProgram getStudentGradSpecialProgramByProgramCodeAndSpecialProgramCode(UUID studentID,String specialProgramID,String accessToken) {
		UUID specialProgramIDUUID = UUID.fromString(specialProgramID);
		Optional<GradStudentSpecialProgramEntity> gradStudentSpecialOptional = gradStudentSpecialProgramRepository.findByStudentIDAndSpecialProgramID(studentID,specialProgramIDUUID);
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
