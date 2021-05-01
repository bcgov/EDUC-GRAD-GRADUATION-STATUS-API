package ca.bc.gov.educ.api.gradstatus.util;

import java.util.Date;

public class EducGradStatusApiConstants {

    //API end-point Mapping constants
	public static final String API_ROOT_MAPPING = "";
    public static final String API_VERSION = "v1";
    public static final String GRADUATION_STATUS_API_ROOT_MAPPING = "/api/" + API_VERSION + "/gradstatus";
    public static final String GRADUATION_STATUS_BY_STUDENT_ID = "/studentid/{studentID}";
    public static final String GRADUATION_STATUS_BY_STUDENT_ID_FOR_ALGORITHM = "/studentid/{studentID}/algorithm";
    public static final String GRAD_STUDENT_UPDATE_BY_STUDENT_ID = "/gradstudent/studentid/{studentID}";
    public static final String GRADUATE_STUDENT_BY_PEN = "/pen/{pen}";
    public static final String GRAD_STUDENT_SPECIAL_PROGRAM_BY_PEN = "/specialprogram/studentid/{studentID}";
    public static final String GRAD_STUDENT_SPECIAL_PROGRAM_BY_PEN_PROGRAM_SPECIAL_PROGRAM = "/specialprogram/{studentID}/{specialProgramID}";
    public static final String SAVE_GRAD_STUDENT_SPECIAL_PROGRAM = "/specialprogram";
    public static final String GRAD_STUDENT_RECALCULATE = "/recalculate";
    public static final String GET_STUDENT_STATUS_BY_STATUS_CODE_MAPPING = "/checkstudentstatus/{statusCode}";
    
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
	public static final String ENDPOINT_GRAD_PROGRAM_NAME_URL="${endpoint.grad-program-management-api.program_name_by_program_code.url}";
	public static final String ENDPOINT_GRAD_SCHOOL_NAME_URL="${endpoint.school-api.school-name-by-mincode.url}";
	public static final String ENDPOINT_STUDENT_STATUS_URL="${endpoint.code-api.student-status.student-status-by-status-code.url}";
	public static final String ENDPOINT_PEN_STUDENT_API_BY_STUDENT_ID_URL = "${endpoint.pen-student-api.by-studentid.url}";
	
}
