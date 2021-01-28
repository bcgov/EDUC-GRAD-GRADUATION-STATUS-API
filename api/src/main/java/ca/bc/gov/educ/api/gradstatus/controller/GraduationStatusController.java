package ca.bc.gov.educ.api.gradstatus.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.bc.gov.educ.api.gradstatus.model.dto.GraduationStatus;
import ca.bc.gov.educ.api.gradstatus.service.GraduationStatusService;
import ca.bc.gov.educ.api.gradstatus.util.ApiResponseModel;
import ca.bc.gov.educ.api.gradstatus.util.EducGradStatusApiConstants;
import ca.bc.gov.educ.api.gradstatus.util.GradValidation;
import ca.bc.gov.educ.api.gradstatus.util.PermissionsContants;
import ca.bc.gov.educ.api.gradstatus.util.ResponseHelper;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@CrossOrigin
@RestController
@RequestMapping(EducGradStatusApiConstants.GRADUATION_STATUS_API_ROOT_MAPPING)
@EnableResourceServer
@OpenAPIDefinition(info = @Info(title = "API for Student Grad Status .", description = "This Read API is for Student Grad Status.", version = "1"), security = {@SecurityRequirement(name = "OAUTH2", scopes = {"UPDATE_GRAD_GRADUATION_STATUS"})})
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
    public ResponseEntity<GraduationStatus> getStudentGradStatus(@PathVariable String pen) {
        logger.debug("Get Student Grad Status for PEN: " + pen);
        GraduationStatus gradResponse = gradStatusService.getGraduationStatus(pen);
        if(gradResponse != null) {
    		return response.GET(gradResponse);
    	}else {
    		return response.NOT_FOUND();
    	}
    }
    
    @PostMapping (EducGradStatusApiConstants.GRADUATION_STATUS_BY_PEN)
    @PreAuthorize(PermissionsContants.UPDATE_GRADUATION_STUDENT)
    public ResponseEntity<GraduationStatus> saveStudentGradStatus(@PathVariable String pen, @RequestBody GraduationStatus graduationStatus) {
        logger.debug("Save student Grad Status for PEN: " + pen);
        return response.GET(gradStatusService.saveGraduationStatus(pen,graduationStatus));
    }
    
    @GetMapping (EducGradStatusApiConstants.GRADUATION_STATUS_BY_CERTIFICATE_TYPE)
    @PreAuthorize(PermissionsContants.READ_GRADUATION_STUDENT)
    public ResponseEntity<Boolean> getStudentGradStatusByCertificateType(@PathVariable String certificateType) {
        logger.debug("Get Student Grad Status for CERT: " + certificateType);
        return response.GET(gradStatusService.getStudentGradStatusByCertificateType(certificateType));
    }
}
