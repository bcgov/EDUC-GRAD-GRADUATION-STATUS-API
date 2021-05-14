package ca.bc.gov.educ.api.gradstatus.service;


import ca.bc.gov.educ.api.gradstatus.model.dto.*;
import ca.bc.gov.educ.api.gradstatus.model.entity.GradStudentSpecialProgramEntity;
import ca.bc.gov.educ.api.gradstatus.model.entity.GraduationStatusEntity;
import ca.bc.gov.educ.api.gradstatus.model.transformer.GradStudentSpecialProgramTransformer;
import ca.bc.gov.educ.api.gradstatus.model.transformer.GraduationStatusTransformer;
import ca.bc.gov.educ.api.gradstatus.repository.GradStudentSpecialProgramRepository;
import ca.bc.gov.educ.api.gradstatus.repository.GraduationStatusRepository;
import ca.bc.gov.educ.api.gradstatus.util.EducGradStatusApiConstants;
import ca.bc.gov.educ.api.gradstatus.util.GradValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


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

    private static final String CREATED_BY = "createdBy";
    private static final String CREATED_TIMESTAMP = "createdTimestamp";


    public GraduationStatus getGraduationStatusForAlgorithm(UUID studentID) {
        logger.info("getGraduationStatus");
        Optional<GraduationStatusEntity> responseOptional = graduationStatusRepository.findById(studentID);
        if (responseOptional.isPresent()) {
            return graduationStatusTransformer.transformToDTO(responseOptional.get());
        } else {
            return null;
        }

    }

    public GraduationStatus getGraduationStatus(UUID studentID, String accessToken) {
        logger.info("getGraduationStatus");
        Optional<GraduationStatusEntity> responseOptional = graduationStatusRepository.findById(studentID);
        if (responseOptional.isPresent()) {
            GraduationStatus gradStatus = graduationStatusTransformer.transformToDTO(responseOptional.get());
            if (gradStatus.getProgram() != null) {
                gradStatus.setProgramName(getProgramName(gradStatus.getProgram(), accessToken));
            }
            if (gradStatus.getSchoolOfRecord() != null)
                gradStatus.setSchoolName(getSchoolName(gradStatus.getSchoolOfRecord(), accessToken));

            if (gradStatus.getStudentStatus() != null) {
                StudentStatus statusObj = webClient.get()
                        .uri(String.format(getStudentStatusName, gradStatus.getStudentStatus()))
                        .headers(h -> h.setBearerAuth(accessToken))
                        .retrieve()
                        .bodyToMono(StudentStatus.class)
                        .block();
                if (statusObj != null)
                    gradStatus.setStudentStatusName(statusObj.getDescription());
            }

            if (gradStatus.getSchoolOfRecord() != null)
                gradStatus.setSchoolAtGradName(getSchoolName(gradStatus.getSchoolOfRecord(), accessToken));

            return gradStatus;
        } else {
            return null;
        }

    }

    public GraduationStatus saveGraduationStatus(UUID studentID, GraduationStatus graduationStatus) {
        Optional<GraduationStatusEntity> gradStatusOptional = graduationStatusRepository.findById(studentID);
        GraduationStatusEntity sourceObject = graduationStatusTransformer.transformToEntity(graduationStatus);
        if (gradStatusOptional.isPresent()) {
            GraduationStatusEntity gradEnity = gradStatusOptional.get();
            BeanUtils.copyProperties(sourceObject, gradEnity, CREATED_BY, CREATED_TIMESTAMP);
            gradEnity.setRecalculateGradStatus(null);
            gradEnity.setProgramCompletionDate(sourceObject.getProgramCompletionDate());
            return graduationStatusTransformer.transformToDTO(graduationStatusRepository.save(gradEnity));
        } else {
            return graduationStatusTransformer.transformToDTO(graduationStatusRepository.save(sourceObject));
        }
    }

    public GraduationStatus updateGraduationStatus(UUID studentID, GraduationStatus graduationStatus, String accessToken) {
        Optional<GraduationStatusEntity> gradStatusOptional = graduationStatusRepository.findById(studentID);
        GraduationStatusEntity sourceObject = graduationStatusTransformer.transformToEntity(graduationStatus);
        if (gradStatusOptional.isPresent()) {
            GraduationStatusEntity gradEnity = gradStatusOptional.get();
            boolean hasDataChanged = validateData(sourceObject, gradEnity, accessToken);
            if (validation.hasErrors()) {
                validation.stopOnErrors();
                return new GraduationStatus();
            }
            if (hasDataChanged) {
                gradEnity.setRecalculateGradStatus("Y");
            } else {
                gradEnity.setRecalculateGradStatus(null);
            }
            BeanUtils.copyProperties(sourceObject, gradEnity, CREATED_BY, CREATED_TIMESTAMP, "studentGradData", "recalculateGradStatus");
            gradEnity.setProgramCompletionDate(sourceObject.getProgramCompletionDate());
            return graduationStatusTransformer.transformToDTO(graduationStatusRepository.save(gradEnity));
        } else {
            validation.addErrorAndStop(String.format("Student ID [%s] does not exists", studentID));
            return graduationStatus;
        }
    }

    private String getSchoolName(String minCode, String accessToken) {
        School schObj = webClient.get()
                .uri(String.format(getGradSchoolName, minCode))
                .headers(h -> h.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(School.class)
                .block();
        if (schObj != null)
            return schObj.getSchoolName();
        else
            return null;
    }

    private String getProgramName(String programCode, String accessToken) {
        GradProgram gradProgram = webClient.get()
                .uri(String.format(getGradProgramName, programCode))
                .headers(h -> h.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(GradProgram.class)
                .block();
        if (gradProgram != null)
            return gradProgram.getProgramName();
        return null;
    }

    private void validateStudentStatus(String studentStatus) {
        if (studentStatus.equalsIgnoreCase("M")) {
            validation.addErrorAndStop("Student GRAD data cannot be updated for students with a status of 'M' merged");
        }
        if (studentStatus.equalsIgnoreCase("D")) {
            validation.addErrorAndStop("This student is showing as deceased.  Confirm the students' status before re-activating by setting their status to 'A' if they are currently attending school");
        }
    }

    private void validateProgram(GraduationStatusEntity sourceEntity, String accessToken) {
        GradProgram gradProgram = webClient.get()
				.uri(String.format(getGradProgramName, sourceEntity.getProgram()))
				.headers(h -> h.setBearerAuth(accessToken))
				.retrieve()
				.bodyToMono(GradProgram.class)
				.block();
        if (gradProgram == null) {
            validation.addError(String.format("Program [%s] is invalid", sourceEntity.getProgram()));
        } else {
            if (sourceEntity.getProgram().contains("1950")) {
                if (!sourceEntity.getStudentGrade().equalsIgnoreCase("AD")
						&& !sourceEntity.getStudentGrade().equalsIgnoreCase("AN")) {
                    validation.addError(
                    		String.format("Student grade should be one of AD or AN if the student program is [%s]",
									sourceEntity.getProgram()));
                }
            } else {
                if (sourceEntity.getStudentGrade().equalsIgnoreCase("AD")
						|| sourceEntity.getStudentGrade().equalsIgnoreCase("AN")) {
                    validation.addError(
                    		String.format("Student grade should not be AD or AN for this program [%s]",
									sourceEntity.getProgram()));
                }
            }
        }
    }

    private void validateSchool(String minCode, String accessToken) {
        School schObj = webClient.get()
				.uri(String.format(getGradSchoolName, minCode))
				.headers(h -> h.setBearerAuth(accessToken))
				.retrieve()
				.bodyToMono(School.class)
				.block();
        if (schObj == null) {
            validation.addError(
            		String.format("Invalid School entered, School [%s] does not exist on the School table", minCode));
        } else {
            if (schObj.getOpenFlag().equalsIgnoreCase("N")) {
                validation.addWarning(String.format("This School [%s] is Closed", minCode));
            }
        }
    }

    private void validateStudentGrade(GraduationStatusEntity sourceEntity, String accessToken) {
        Student studentObj = webClient.get()
				.uri(String.format(getPenStudentAPIByStudentIDURL, sourceEntity.getStudentID()))
				.headers(h -> h.setBearerAuth(accessToken))
				.retrieve()
				.bodyToMono(Student.class)
				.block();
        if (sourceEntity.getStudentStatus().equalsIgnoreCase("D")
				|| sourceEntity.getStudentStatus().equalsIgnoreCase("M")) {
            if (!sourceEntity.getStudentStatus().equalsIgnoreCase(studentObj.getStatusCode())) {
                validation.addError("Status code selected is at odds with the PEN data for this student");
            }
        } else {
            if (!"A".equalsIgnoreCase(studentObj.getStatusCode())) {
                validation.addError("Status code selected is at odds with the PEN data for this student");
            }
        }

        if ((sourceEntity.getStudentGrade().equalsIgnoreCase("AN")
				|| sourceEntity.getStudentGrade().equalsIgnoreCase("AD"))
				&& calculateAge(studentObj.getDob()) < 18) {
            validation.addError("Adult student should be at least 18 years old");
        }
    }

    private boolean validateData(GraduationStatusEntity sourceEntity, GraduationStatusEntity existingEntity, String accessToken) {
        boolean hasDataChangd = false;
        validateStudentStatus(existingEntity.getStudentStatus());
        if (!sourceEntity.getProgram().equalsIgnoreCase(existingEntity.getProgram())) {
            hasDataChangd = true;
            validateProgram(sourceEntity, accessToken);
        }
        if (!sourceEntity.getSchoolOfRecord().equalsIgnoreCase(existingEntity.getSchoolOfRecord())) {
            hasDataChangd = true;
            validateSchool(sourceEntity.getSchoolOfRecord(), accessToken);
        }
        if (!sourceEntity.getSchoolAtGrad().equalsIgnoreCase(existingEntity.getSchoolAtGrad())) {
            hasDataChangd = true;
            validateSchool(sourceEntity.getSchoolAtGrad(), accessToken);
        }
        if (!sourceEntity.getStudentGrade().equalsIgnoreCase(existingEntity.getStudentGrade())
				|| !sourceEntity.getStudentStatus().equalsIgnoreCase(existingEntity.getStudentStatus())) {
            hasDataChangd = true;
            validateStudentGrade(sourceEntity, accessToken);
        }
        if (sourceEntity.getGpa() != null && !sourceEntity.getGpa().equalsIgnoreCase(existingEntity.getGpa())) {
            hasDataChangd = true;
            sourceEntity.setHonoursStanding(getHonoursFlag(sourceEntity.getGpa()));
        }
        return hasDataChangd;
    }

    private String getHonoursFlag(String gPA) {
        if (Float.parseFloat(gPA) > 3)
            return "Y";
        else
            return "N";
    }

    public List<GradStudentSpecialProgram> getStudentGradSpecialProgram(UUID studentID, String accessToken) {
        List<GradStudentSpecialProgram> specialProgramList =
				gradStudentSpecialProgramTransformer.transformToDTO(gradStudentSpecialProgramRepository.findByStudentID(studentID));
        specialProgramList.forEach(sP -> {
            GradSpecialProgram gradSpecialProgram = webClient.get()
					.uri(String.format(getGradSpecialProgramName, sP.getSpecialProgramID()))
					.headers(h -> h.setBearerAuth(accessToken))
					.retrieve()
					.bodyToMono(GradSpecialProgram.class)
					.block();
            sP.setSpecialProgramName(gradSpecialProgram.getSpecialProgramName());
            sP.setSpecialProgramCode(gradSpecialProgram.getSpecialProgramCode());
            sP.setMainProgramCode(gradSpecialProgram.getProgramCode());
        });
        return specialProgramList;
    }

    public int calculateAge(String dob) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate birthDate = LocalDate.parse(dob, dateFormatter);
        LocalDate currentDate = LocalDate.now();
        return Period.between(birthDate, currentDate).getYears();
    }

    public GradStudentSpecialProgram saveStudentGradSpecialProgram(GradStudentSpecialProgram gradStudentSpecialProgram) {
        Optional<GradStudentSpecialProgramEntity> gradStudentSpecialOptional =
				gradStudentSpecialProgramRepository.findById(gradStudentSpecialProgram.getId());
        GradStudentSpecialProgramEntity sourceObject = gradStudentSpecialProgramTransformer.transformToEntity(gradStudentSpecialProgram);
        if (gradStudentSpecialOptional.isPresent()) {
            GradStudentSpecialProgramEntity gradEnity = gradStudentSpecialOptional.get();
            BeanUtils.copyProperties(sourceObject, gradEnity, CREATED_BY, CREATED_TIMESTAMP);
            gradEnity.setSpecialProgramCompletionDate(sourceObject.getSpecialProgramCompletionDate());
            return gradStudentSpecialProgramTransformer.transformToDTO(gradStudentSpecialProgramRepository.save(gradEnity));
        } else {
            return gradStudentSpecialProgramTransformer.transformToDTO(gradStudentSpecialProgramRepository.save(sourceObject));
        }
    }

    public List<GraduationStatus> getStudentsForGraduation() {
        return graduationStatusTransformer.transformToDTO(graduationStatusRepository.findByRecalculateGradStatus("Y"));
    }

    public GradStudentSpecialProgram getStudentGradSpecialProgramByProgramCodeAndSpecialProgramCode(
    		UUID studentID, String specialProgramID, String accessToken) {
        UUID specialProgramIDUUID = UUID.fromString(specialProgramID);
        Optional<GradStudentSpecialProgramEntity> gradStudentSpecialOptional =
				gradStudentSpecialProgramRepository.findByStudentIDAndSpecialProgramID(studentID, specialProgramIDUUID);
        if (gradStudentSpecialOptional.isPresent()) {
            GradStudentSpecialProgram responseObj = gradStudentSpecialProgramTransformer.transformToDTO(gradStudentSpecialOptional);
            GradSpecialProgram gradSpecialProgram = webClient.get()
					.uri(String.format(getGradSpecialProgramName, responseObj.getSpecialProgramID()))
					.headers(h -> h.setBearerAuth(accessToken))
					.retrieve()
					.bodyToMono(GradSpecialProgram.class)
					.block();
            responseObj.setSpecialProgramName(gradSpecialProgram.getSpecialProgramName());
            responseObj.setSpecialProgramCode(gradSpecialProgram.getSpecialProgramCode());
            responseObj.setMainProgramCode(gradSpecialProgram.getProgramCode());
            return responseObj;
        }
        return null;
    }

    public boolean getStudentStatus(String statusCode) {
        List<GraduationStatusEntity> gradList = graduationStatusRepository.existsByStatusCode(statusCode);
        return !gradList.isEmpty();
    }
}
