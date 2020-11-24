package ca.bc.gov.educ.api.gradstatus.util;

import java.util.Date;

public class EducGradStatusApiConstants {

    //API end-point Mapping constants
	public static final String API_ROOT_MAPPING = "";
    public static final String API_VERSION = "v1";
    public static final String GRADUATION_STATUS_API_ROOT_MAPPING = "/api/" + API_VERSION + "/gradstatus";
    public static final String GRADUATION_STATUS_BY_PEN = "pen/{pen}";
    public static final String GRADUATE_STUDENT_BY_PEN = "pen/{pen}";
    public static final String STUDENT_ACHIEVEMENT_REPORT_BY_PEN = "pen/{pen}/achievementreport";
    public static final String STUDENT_TRANSCRIPT_BY_PEN = "pen/{pen}/transcript";

    
    //Default Attribute value constants
    public static final String DEFAULT_CREATED_BY = "GraduationStatusAPI";
    public static final Date DEFAULT_CREATED_TIMESTAMP = new Date();
    public static final String DEFAULT_UPDATED_BY = "GraduationStatusAPI";
    public static final Date DEFAULT_UPDATED_TIMESTAMP = new Date();

    //Default Date format constants
    public static final String DEFAULT_DATE_FORMAT = "dd-MMM-yyyy";
    
    public static final String TRAX_DATE_FORMAT = "yyyyMM";
}
