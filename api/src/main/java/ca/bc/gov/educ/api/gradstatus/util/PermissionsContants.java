package ca.bc.gov.educ.api.gradstatus.util;

public interface PermissionsContants {
	String _PREFIX = "#oauth2.hasAnyScope('";
	String _SUFFIX = "')";

	String UPDATE_GRADUATION_STUDENT = _PREFIX + "UPDATE_GRAD_GRADUATION_STATUS" + _SUFFIX;
	String READ_GRADUATION_STUDENT = _PREFIX + "READ_GRAD_GRADUATION_STATUS" + _SUFFIX;
	String UPDATE_GRADUATION_STUDENT_SPECIAL_PROGRAM = _PREFIX + "UPDATE_GRAD_STUDENT_SPECIAL_DATA" + _SUFFIX;
	String READ_GRADUATION_STUDENT_SPECIAL_PROGRAM = _PREFIX + "READ_GRAD_STUDENT_SPECIAL_DATA" + _SUFFIX;
}
