package ca.bc.gov.educ.api.gradstatus.util;

import java.util.Date;

public class EducGradStatusApiConstants {

    //API end-point Mapping constants
	public static final String API_ROOT_MAPPING = "";
    public static final String API_VERSION = "v1";
    public static final String GRADUATION_STATUS_API_ROOT_MAPPING = "/api/" + API_VERSION + "/gradstatus";
    public static final String GRADUATION_STATUS_BY_PEN = "/pen/{pen}";
    public static final String GRAD_STUDENT_UPDATE_BY_PEN = "/gradstudent/pen/{pen}";
    public static final String GRADUATE_STUDENT_BY_PEN = "/pen/{pen}";
    public static final String GRAD_STUDENT_SPECIAL_PROGRAM_BY_PEN = "/specialprogram/pen/{pen}";
    public static final String GRAD_STUDENT_SPECIAL_PROGRAM_BY_PEN_PROGRAM_SPECIAL_PROGRAM = "/specialprogram/{pen}/{programCode}/{specialProgramCode}";
    public static final String SAVE_GRAD_STUDENT_SPECIAL_PROGRAM = "/specialprogram";
    public static final String GRAD_STUDENT_RECALCULATE = "/recalculate";
    
    //Default Attribute value constants
    public static final String DEFAULT_CREATED_BY = "GraduationStatusAPI";
    public static final Date DEFAULT_CREATED_TIMESTAMP = new Date();
    public static final String DEFAULT_UPDATED_BY = "GraduationStatusAPI";
    public static final Date DEFAULT_UPDATED_TIMESTAMP = new Date();

    //Default Date format constants
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    
    public static final String TRAX_DATE_FORMAT = "yyyyMM";
	public static final String ENDPOINT_GRAD_SPECIAL_PROGRAM_NAME_URL = "${endpoint.grad-program-management-api.special_program_name_by_special_program_id.url}";
	public static final String ENDPOINT_GRAD_SPECIAL_PROGRAM_DETAILS_URL ="${endpoint.grad-program-management-api.special_program_id_by_program_code_special_program_code.url}";
}
