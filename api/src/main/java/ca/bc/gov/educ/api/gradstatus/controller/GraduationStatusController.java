package ca.bc.gov.educ.api.gradstatus.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.bc.gov.educ.api.gradstatus.model.dto.GraduationStatus;
import ca.bc.gov.educ.api.gradstatus.service.GraduationStatusService;
import ca.bc.gov.educ.api.gradstatus.util.EducGradStatusApiConstants;

@CrossOrigin
@RestController
@RequestMapping(EducGradStatusApiConstants.GRADUATION_STATUS_API_ROOT_MAPPING)
public class GraduationStatusController {

	private static Logger logger = LoggerFactory.getLogger(GraduationStatusController.class);

    @Autowired
    GraduationStatusService gradStatusService;

    @GetMapping (EducGradStatusApiConstants.GRADUATION_STATUS_BY_PEN)
    public GraduationStatus getStudentGradStatus(@PathVariable String pen) {
        logger.debug("Get Student Grad Status for PEN: " + pen);
        return gradStatusService.getGraduationStatus(pen);
    }
}
