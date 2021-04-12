package ca.bc.gov.educ.api.gradstatus.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.bc.gov.educ.api.gradstatus.model.dto.GradStudentSpecialProgram;
import ca.bc.gov.educ.api.gradstatus.model.dto.GraduationStatus;
import ca.bc.gov.educ.api.gradstatus.service.GraduationStatusService;
import ca.bc.gov.educ.api.gradstatus.util.EducGradStatusApiConstants;
import ca.bc.gov.educ.api.gradstatus.util.GradValidation;
import ca.bc.gov.educ.api.gradstatus.util.PermissionsContants;
import ca.bc.gov.educ.api.gradstatus.util.ResponseHelper;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@CrossOrigin
@RestController
@RequestMapping(EducGradStatusApiConstants.GRADUATION_STATUS_API_ROOT_MAPPING)
@EnableResourceServer
@OpenAPIDefinition(info = @Info(title = "API for Grad Student Status.", description = "This API is for Grad Student Status.", version = "1"), security = {@SecurityRequirement(name = "OAUTH2", scopes = {"UPDATE_GRAD_GRADUATION_STATUS"})})
public class GraduationStatusController {

	private static Logger logger = LoggerFactory.getLogger(GraduationStatusController.class);

    @Autowired
    GraduationStatusService gradStatusService;
    
    @Autowired
	GradValidation validation;
    
    @Autowired
	ResponseHelper response;

    @GetMapping (EducGradStatusApiConstants.GRADUATION_STATUS_BY_PEN)
    @PreAuthorize(PermissionsContants.READ_GRADUATION_STUDENT)
    @Operation(summary = "Find Student Grad Status by PEN", description = "Get Student Grad Status by PEN", tags = { "Student Graduation Status" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "204", description = "NO CONTENT")})
    public ResponseEntity<GraduationStatus> getStudentGradStatus(@PathVariable String pen) {
        logger.debug("Get Student Grad Status for PEN: " + pen);
        GraduationStatus gradResponse = gradStatusService.getGraduationStatus(pen);
        if(gradResponse != null) {
    		return response.GET(gradResponse);
    	}else {
    		return response.NO_CONTENT();
    	}
    }
    
    @PostMapping (EducGradStatusApiConstants.GRADUATION_STATUS_BY_PEN)
    @PreAuthorize(PermissionsContants.UPDATE_GRADUATION_STUDENT)
    @Operation(summary = "Save Student Grad Status by PEN", description = "Save Student Grad Status by PEN", tags = { "Student Graduation Status" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<GraduationStatus> saveStudentGradStatus(@PathVariable String pen, @RequestBody GraduationStatus graduationStatus) {
        logger.debug("Save student Grad Status for PEN: " + pen);        
        return response.GET(gradStatusService.saveGraduationStatus(pen,graduationStatus));
    } 
    
    @PostMapping (EducGradStatusApiConstants.GRAD_STUDENT_UPDATE_BY_PEN)
    @PreAuthorize(PermissionsContants.UPDATE_GRADUATION_STUDENT)
    @Operation(summary = "Update Student Grad Status by PEN", description = "Update Student Grad Status by PEN", tags = { "Student Graduation Status" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "400", description = "BAD REQUEST")})
    public ResponseEntity<GraduationStatus> updateStudentGradStatus(@PathVariable String pen, @RequestBody GraduationStatus graduationStatus) {
        logger.debug("update student Grad Status for PEN: " + pen);
        validation.requiredField(graduationStatus.getPen(), "Pen");
        if(validation.hasErrors()) {
    		validation.stopOnErrors();
    		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    	}
        return response.GET(gradStatusService.updateGraduationStatus(pen,graduationStatus));
    }
    
    @GetMapping (EducGradStatusApiConstants.GRAD_STUDENT_SPECIAL_PROGRAM_BY_PEN)
    @PreAuthorize(PermissionsContants.READ_GRADUATION_STUDENT_SPECIAL_PROGRAM)
    @Operation(summary = "Find all Student Special Grad Status by PEN", description = "Get All Student Special Grad Status by PEN", tags = { "Special Student Graduation Status" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "204", description = "NO CONTENT")})
    public ResponseEntity<List<GradStudentSpecialProgram>> getStudentGradSpecialProgram(@PathVariable String pen) {
        logger.debug("Get Student Grad Status for PEN: " + pen);
        OAuth2AuthenticationDetails auth = (OAuth2AuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails(); 
    	String accessToken = auth.getTokenValue();
        List<GradStudentSpecialProgram> gradResponse = gradStatusService.getStudentGradSpecialProgram(pen,accessToken);
        if(gradResponse.size() > 0) {
    		return response.GET(gradResponse);
    	}else {
    		return response.NO_CONTENT();
    	}
    }
    
    @GetMapping (EducGradStatusApiConstants.GRAD_STUDENT_SPECIAL_PROGRAM_BY_PEN_PROGRAM_SPECIAL_PROGRAM)
    @PreAuthorize(PermissionsContants.READ_GRADUATION_STUDENT_SPECIAL_PROGRAM)
    @Operation(summary = "Find all Student Special Grad Status by PEN,PROGRAM and SPECIAL PROGRAM", description = "Get All Student Special Grad Status by PEN,PROGRAM and SPECIAL PROGRAM", tags = { "Special Student Graduation Status" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "204", description = "NO CONTENT")})
    public ResponseEntity<GradStudentSpecialProgram> getStudentGradSpecialProgram(@PathVariable String pen,@PathVariable String programCode,@PathVariable String specialProgramCode) {
        logger.debug("Get Student Grad Status for PEN: " + pen);
        OAuth2AuthenticationDetails auth = (OAuth2AuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails(); 
    	String accessToken = auth.getTokenValue();
        GradStudentSpecialProgram gradResponse = gradStatusService.getStudentGradSpecialProgramByProgramCodeAndSpecialProgramCode(pen,programCode,specialProgramCode,accessToken);
        if(gradResponse != null) {
    		return response.GET(gradResponse);
    	}else {
    		return response.NO_CONTENT();
    	}
    }
    
    @PostMapping (EducGradStatusApiConstants.SAVE_GRAD_STUDENT_SPECIAL_PROGRAM)
    @PreAuthorize(PermissionsContants.UPDATE_GRADUATION_STUDENT_SPECIAL_PROGRAM)
    @Operation(summary = "Save Student Special Grad Status by PEN", description = "Save Student Special Grad Status by PEN", tags = { "Special Student Graduation Status" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<GradStudentSpecialProgram> saveStudentGradSpecialProgram(@RequestBody GradStudentSpecialProgram gradStudentSpecialProgram) {
        logger.debug("Save student Grad Status for PEN: ");
        return response.GET(gradStatusService.saveStudentGradSpecialProgram(gradStudentSpecialProgram));
    }
    
    @GetMapping (EducGradStatusApiConstants.GRAD_STUDENT_RECALCULATE)
    @PreAuthorize(PermissionsContants.READ_GRADUATION_STUDENT)
    @Operation(summary = "Find Students For Batch Algorithm", description = "Get Students For Batch Algorithm", tags = { "Batch Algorithm" })
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<List<GraduationStatus>> getStudentsForGraduation() {
        logger.debug("getStudentsForGraduation:");
        return response.GET(gradStatusService.getStudentsForGraduation());
    }
    
}