package ca.bc.gov.educ.api.gradstatus.service;

import ca.bc.gov.educ.api.gradstatus.model.dto.*;
import ca.bc.gov.educ.api.gradstatus.model.entity.GradStudentSpecialProgramEntity;
import ca.bc.gov.educ.api.gradstatus.model.entity.GraduationStatusEntity;
import ca.bc.gov.educ.api.gradstatus.repository.GradStudentSpecialProgramRepository;
import ca.bc.gov.educ.api.gradstatus.repository.GraduationStatusRepository;
import ca.bc.gov.educ.api.gradstatus.util.EducGradStatusApiConstants;
import ca.bc.gov.educ.api.gradstatus.util.EducGradStatusApiUtils;
import ca.bc.gov.educ.api.gradstatus.util.GradValidation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.sql.Date;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class GraduationStatusServiceTest {

    @Autowired
    EducGradStatusApiConstants constants;

    @Autowired
    private GraduationStatusService graduationStatusService;

    @MockBean
    private GraduationStatusRepository graduationStatusRepository;

    @MockBean
    private GradStudentSpecialProgramRepository gradStudentSpecialProgramRepository;

    @MockBean
    GradValidation validation;

    @MockBean
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersMock;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriMock;
    @Mock
    private WebClient.RequestBodySpec requestBodyMock;
    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriMock;
    @Mock
    private WebClient.ResponseSpec responseMock;

    @Before
    public void setUp() {
        openMocks(this);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testGetGraduationStatusForAlgorithm() {
        // ID
        UUID studentID = UUID.randomUUID();
        String mincode = "12345678";

        GraduationStatusEntity graduationStatusEntity = new GraduationStatusEntity();
        graduationStatusEntity.setStudentID(studentID);
        graduationStatusEntity.setPen("123456789");
        graduationStatusEntity.setStudentStatus("A");
        graduationStatusEntity.setRecalculateGradStatus("Y");
        graduationStatusEntity.setProgram("2018-en");
        graduationStatusEntity.setSchoolOfRecord(mincode);
        graduationStatusEntity.setGpa("4");

        when(graduationStatusRepository.findById(studentID)).thenReturn(Optional.of(graduationStatusEntity));
        var result = graduationStatusService.getGraduationStatusForAlgorithm(studentID);
        assertThat(result).isNotNull();
        assertThat(result.getStudentID()).isEqualTo(graduationStatusEntity.getStudentID());
        assertThat(result.getPen()).isEqualTo(graduationStatusEntity.getPen());
        assertThat(result.getStudentStatus()).isEqualTo(graduationStatusEntity.getStudentStatus());
        assertThat(result.getRecalculateGradStatus()).isEqualTo(graduationStatusEntity.getRecalculateGradStatus());
        assertThat(result.getProgram()).isEqualTo(graduationStatusEntity.getProgram());
        assertThat(result.getSchoolOfRecord()).isEqualTo(graduationStatusEntity.getSchoolOfRecord());
        assertThat(result.getGpa()).isEqualTo(graduationStatusEntity.getGpa());
    }

    @Test
    public void testGetGraduationStatus() {
        // ID
        UUID studentID = UUID.randomUUID();
        String mincode = "12345678";

        GraduationStatusEntity graduationStatusEntity = new GraduationStatusEntity();
        graduationStatusEntity.setStudentID(studentID);
        graduationStatusEntity.setPen("123456789");
        graduationStatusEntity.setStudentStatus("A");
        graduationStatusEntity.setRecalculateGradStatus("Y");
        graduationStatusEntity.setProgram("2018-en");
        graduationStatusEntity.setSchoolOfRecord(mincode);
        graduationStatusEntity.setSchoolAtGrad(mincode);
        graduationStatusEntity.setGpa("4");

        StudentStatus studentStatus = new StudentStatus();
        studentStatus.setCode("A");
        studentStatus.setDescription("Active");

        GradProgram program = new GradProgram();
        program.setProgramCode("2018-en");
        program.setProgramName("Graduation Program 2018");

        School school = new School();
        school.setMinCode(mincode);
        school.setSchoolName("Test School");

        when(graduationStatusRepository.findById(studentID)).thenReturn(Optional.of(graduationStatusEntity));

        when(this.webClient.get()).thenReturn(this.requestHeadersUriMock);
        when(this.requestHeadersUriMock.uri(String.format(constants.getStudentStatusUrl(),graduationStatusEntity.getStudentStatus()))).thenReturn(this.requestHeadersMock);
        when(this.requestHeadersMock.headers(any(Consumer.class))).thenReturn(this.requestHeadersMock);
        when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
        when(this.responseMock.bodyToMono(StudentStatus.class)).thenReturn(Mono.just(studentStatus));

        when(this.webClient.get()).thenReturn(this.requestHeadersUriMock);
        when(this.requestHeadersUriMock.uri(String.format(constants.getGradProgramNameUrl(),program.getProgramCode()))).thenReturn(this.requestHeadersMock);
        when(this.requestHeadersMock.headers(any(Consumer.class))).thenReturn(this.requestHeadersMock);
        when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
        when(this.responseMock.bodyToMono(GradProgram.class)).thenReturn(Mono.just(program));

        when(this.webClient.get()).thenReturn(this.requestHeadersUriMock);
        when(this.requestHeadersUriMock.uri(String.format(constants.getGradSchoolNameUrl(),mincode))).thenReturn(this.requestHeadersMock);
        when(this.requestHeadersMock.headers(any(Consumer.class))).thenReturn(this.requestHeadersMock);
        when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
        when(this.responseMock.bodyToMono(School.class)).thenReturn(Mono.just(school));

        var result = graduationStatusService.getGraduationStatus(studentID, "accessToken");

        assertThat(result).isNotNull();
        assertThat(result.getStudentID()).isEqualTo(graduationStatusEntity.getStudentID());
        assertThat(result.getPen()).isEqualTo(graduationStatusEntity.getPen());
        assertThat(result.getStudentStatus()).isEqualTo(graduationStatusEntity.getStudentStatus());
        assertThat(result.getRecalculateGradStatus()).isEqualTo(graduationStatusEntity.getRecalculateGradStatus());
        assertThat(result.getProgram()).isEqualTo(graduationStatusEntity.getProgram());
        assertThat(result.getSchoolOfRecord()).isEqualTo(graduationStatusEntity.getSchoolOfRecord());
        assertThat(result.getGpa()).isEqualTo(graduationStatusEntity.getGpa());

        assertThat(result.getStudentStatusName()).isEqualTo(studentStatus.getDescription());
        assertThat(result.getProgramName()).isEqualTo(program.getProgramName());
        assertThat(result.getSchoolName()).isEqualTo(school.getSchoolName());
        assertThat(result.getSchoolAtGradName()).isEqualTo(school.getSchoolName());
    }

    @Test
    public void testSaveGraduationStatusAsNew() {
        // ID
        UUID studentID = UUID.randomUUID();
        String mincode = "12345678";

        GraduationStatus graduationStatus = new GraduationStatus();
        graduationStatus.setStudentID(studentID);
        graduationStatus.setPen("123456789");
        graduationStatus.setStudentStatus("A");
        graduationStatus.setRecalculateGradStatus("Y");
        graduationStatus.setProgram("2018-en");
        graduationStatus.setSchoolOfRecord(mincode);
        graduationStatus.setSchoolAtGrad(mincode);
        graduationStatus.setGpa("4");
        graduationStatus.setProgramCompletionDate(EducGradStatusApiUtils.formatDate(new Date(System.currentTimeMillis()), "yyyy/MM"));

        GraduationStatusEntity graduationStatusEntity = new GraduationStatusEntity();
        BeanUtils.copyProperties(graduationStatus, graduationStatusEntity);
        graduationStatusEntity.setProgramCompletionDate(new Date(System.currentTimeMillis()));

        when(graduationStatusRepository.findById(studentID)).thenReturn(Optional.empty());
        when(graduationStatusRepository.save(any(GraduationStatusEntity.class))).thenReturn(graduationStatusEntity);

        var result = graduationStatusService.saveGraduationStatus(studentID, graduationStatus);

        assertThat(result).isNotNull();
        assertThat(result.getStudentID()).isEqualTo(graduationStatusEntity.getStudentID());
        assertThat(result.getPen()).isEqualTo(graduationStatusEntity.getPen());
        assertThat(result.getStudentStatus()).isEqualTo(graduationStatusEntity.getStudentStatus());
        assertThat(result.getProgram()).isEqualTo(graduationStatusEntity.getProgram());
        assertThat(result.getSchoolOfRecord()).isEqualTo(graduationStatusEntity.getSchoolOfRecord());
        assertThat(result.getGpa()).isEqualTo(graduationStatusEntity.getGpa());

        assertThat(result.getRecalculateGradStatus()).isEqualTo(graduationStatus.getRecalculateGradStatus());
        assertThat(result.getProgramCompletionDate()).isEqualTo(graduationStatus.getProgramCompletionDate());
    }

    @Test
    public void testSaveGraduationStatus() {
        // ID
        UUID studentID = UUID.randomUUID();
        String mincode = "12345678";

        GraduationStatusEntity graduationStatusEntity = new GraduationStatusEntity();
        graduationStatusEntity.setStudentID(studentID);
        graduationStatusEntity.setPen("123456789");
        graduationStatusEntity.setStudentStatus("A");
        graduationStatusEntity.setRecalculateGradStatus("Y");
        graduationStatusEntity.setProgram("2018-en");
        graduationStatusEntity.setSchoolOfRecord(mincode);
        graduationStatusEntity.setSchoolAtGrad(mincode);
        graduationStatusEntity.setGpa("4");
        graduationStatusEntity.setProgramCompletionDate(new Date(System.currentTimeMillis()));

        GraduationStatus input = new GraduationStatus();
        BeanUtils.copyProperties(graduationStatusEntity, input);
        input.setProgramCompletionDate(EducGradStatusApiUtils.formatDate(graduationStatusEntity.getProgramCompletionDate(), "yyyy/MM" ));

        GraduationStatusEntity savedGraduationStatus = new GraduationStatusEntity();
        BeanUtils.copyProperties(graduationStatusEntity, savedGraduationStatus);
        savedGraduationStatus.setRecalculateGradStatus(null);
        savedGraduationStatus.setProgramCompletionDate(graduationStatusEntity.getProgramCompletionDate());

        when(graduationStatusRepository.findById(studentID)).thenReturn(Optional.of(graduationStatusEntity));
        when(graduationStatusRepository.save(graduationStatusEntity)).thenReturn(savedGraduationStatus);

        var result = graduationStatusService.saveGraduationStatus(studentID, input);

        assertThat(result).isNotNull();
        assertThat(result.getStudentID()).isEqualTo(graduationStatusEntity.getStudentID());
        assertThat(result.getPen()).isEqualTo(graduationStatusEntity.getPen());
        assertThat(result.getStudentStatus()).isEqualTo(graduationStatusEntity.getStudentStatus());
        assertThat(result.getProgram()).isEqualTo(graduationStatusEntity.getProgram());
        assertThat(result.getSchoolOfRecord()).isEqualTo(graduationStatusEntity.getSchoolOfRecord());
        assertThat(result.getGpa()).isEqualTo(graduationStatusEntity.getGpa());

        assertThat(result.getRecalculateGradStatus()).isNull();
        assertThat(result.getProgramCompletionDate()).isEqualTo(input.getProgramCompletionDate());
    }

    @Test
    public void testUpdateGraduationStatus_givenSameData_whenDataIsValidated_thenReturnSuccess() {
        // ID
        UUID studentID = UUID.randomUUID();
        String pen = "123456789";
        String mincode = "12345678";

        GraduationStatusEntity graduationStatusEntity = new GraduationStatusEntity();
        graduationStatusEntity.setStudentID(studentID);
        graduationStatusEntity.setPen(pen);
        graduationStatusEntity.setStudentStatus("A");
        graduationStatusEntity.setStudentGrade("12");
        graduationStatusEntity.setProgram("2018-en");
        graduationStatusEntity.setSchoolOfRecord(mincode);
        graduationStatusEntity.setSchoolAtGrad(mincode);
        graduationStatusEntity.setGpa("4");
        graduationStatusEntity.setProgramCompletionDate(new Date(System.currentTimeMillis()));

        GraduationStatus input = new GraduationStatus();
        BeanUtils.copyProperties(graduationStatusEntity, input);
        input.setProgramCompletionDate(EducGradStatusApiUtils.formatDate(graduationStatusEntity.getProgramCompletionDate(), "yyyy/MM" ));

        GraduationStatusEntity savedGraduationStatus = new GraduationStatusEntity();
        BeanUtils.copyProperties(graduationStatusEntity, savedGraduationStatus);
        savedGraduationStatus.setProgramCompletionDate(graduationStatusEntity.getProgramCompletionDate());

        when(graduationStatusRepository.findById(studentID)).thenReturn(Optional.of(graduationStatusEntity));
        when(graduationStatusRepository.save(graduationStatusEntity)).thenReturn(savedGraduationStatus);

        var result = graduationStatusService.updateGraduationStatus(studentID, input, "accessToken");

        assertThat(result).isNotNull();
        assertThat(result.getStudentID()).isEqualTo(savedGraduationStatus.getStudentID());
        assertThat(result.getPen()).isEqualTo(savedGraduationStatus.getPen());
        assertThat(result.getStudentStatus()).isEqualTo(savedGraduationStatus.getStudentStatus());
        assertThat(result.getStudentGrade()).isEqualTo(savedGraduationStatus.getStudentGrade());
        assertThat(result.getProgram()).isEqualTo(savedGraduationStatus.getProgram());
        assertThat(result.getSchoolOfRecord()).isEqualTo(savedGraduationStatus.getSchoolOfRecord());
        assertThat(result.getGpa()).isEqualTo(savedGraduationStatus.getGpa());

        assertThat(result.getRecalculateGradStatus()).isNull();
        assertThat(result.getProgramCompletionDate()).isEqualTo(input.getProgramCompletionDate());
    }

    @Test
    public void testUpdateGraduationStatus_givenDifferentStudentGrades_whenStudentGradeIsValidated_thenReturnSuccess() {
        // ID
        UUID studentID = UUID.randomUUID();
        String pen = "123456789";
        String mincode = "12345678";

        GraduationStatusEntity graduationStatusEntity = new GraduationStatusEntity();
        graduationStatusEntity.setStudentID(studentID);
        graduationStatusEntity.setPen(pen);
        graduationStatusEntity.setStudentStatus("A");
        graduationStatusEntity.setStudentGrade("11");
        graduationStatusEntity.setProgram("2018-en");
        graduationStatusEntity.setSchoolOfRecord(mincode);
        graduationStatusEntity.setSchoolAtGrad(mincode);
        graduationStatusEntity.setGpa("4");
        graduationStatusEntity.setProgramCompletionDate(new Date(System.currentTimeMillis()));

        GraduationStatus input = new GraduationStatus();
        BeanUtils.copyProperties(graduationStatusEntity, input);
        input.setRecalculateGradStatus(null);
        input.setStudentGrade("12");
        input.setProgramCompletionDate(EducGradStatusApiUtils.formatDate(graduationStatusEntity.getProgramCompletionDate(), "yyyy/MM" ));

        GraduationStatusEntity savedGraduationStatus = new GraduationStatusEntity();
        BeanUtils.copyProperties(graduationStatusEntity, savedGraduationStatus);
        savedGraduationStatus.setRecalculateGradStatus("Y");
        savedGraduationStatus.setStudentGrade("12");
        savedGraduationStatus.setProgramCompletionDate(graduationStatusEntity.getProgramCompletionDate());

        Student student = new Student();
        student.setStudentID(studentID.toString());
        student.setPen(pen);
        student.setStatusCode("A");
        student.setGradeCode("12");
        student.setEmail("qa@test.com");

        when(graduationStatusRepository.findById(studentID)).thenReturn(Optional.of(graduationStatusEntity));
        when(graduationStatusRepository.save(graduationStatusEntity)).thenReturn(savedGraduationStatus);

        when(this.webClient.get()).thenReturn(this.requestHeadersUriMock);
        when(this.requestHeadersUriMock.uri(String.format(constants.getPenStudentApiByStudentIdUrl(),studentID))).thenReturn(this.requestHeadersMock);
        when(this.requestHeadersMock.headers(any(Consumer.class))).thenReturn(this.requestHeadersMock);
        when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
        when(this.responseMock.bodyToMono(Student.class)).thenReturn(Mono.just(student));

        var result = graduationStatusService.updateGraduationStatus(studentID, input, "accessToken");

        assertThat(result).isNotNull();
        assertThat(result.getStudentID()).isEqualTo(savedGraduationStatus.getStudentID());
        assertThat(result.getPen()).isEqualTo(savedGraduationStatus.getPen());
        assertThat(result.getStudentStatus()).isEqualTo(savedGraduationStatus.getStudentStatus());
        assertThat(result.getStudentGrade()).isEqualTo(savedGraduationStatus.getStudentGrade());
        assertThat(result.getProgram()).isEqualTo(savedGraduationStatus.getProgram());
        assertThat(result.getSchoolOfRecord()).isEqualTo(savedGraduationStatus.getSchoolOfRecord());
        assertThat(result.getGpa()).isEqualTo(savedGraduationStatus.getGpa());

        assertThat(result.getRecalculateGradStatus()).isEqualTo(savedGraduationStatus.getRecalculateGradStatus());
        assertThat(result.getProgramCompletionDate()).isEqualTo(input.getProgramCompletionDate());
    }

    @Test
    public void testUpdateGraduationStatus_givenDifferentPrograms_whenProgramIsValidated_thenReturnSuccess() {
        // ID
        UUID studentID = UUID.randomUUID();
        String pen = "123456789";
        String mincode = "12345678";

        GraduationStatusEntity graduationStatusEntity = new GraduationStatusEntity();
        graduationStatusEntity.setStudentID(studentID);
        graduationStatusEntity.setPen(pen);
        graduationStatusEntity.setStudentStatus("A");
        graduationStatusEntity.setStudentGrade("12");
        graduationStatusEntity.setProgram("2018-pf");
        graduationStatusEntity.setSchoolOfRecord(mincode);
        graduationStatusEntity.setSchoolAtGrad(mincode);
        graduationStatusEntity.setGpa("4");
        graduationStatusEntity.setProgramCompletionDate(new Date(System.currentTimeMillis()));

        GraduationStatus input = new GraduationStatus();
        BeanUtils.copyProperties(graduationStatusEntity, input);
        input.setRecalculateGradStatus(null);
        input.setProgram("2018-en");
        input.setProgramCompletionDate(EducGradStatusApiUtils.formatDate(graduationStatusEntity.getProgramCompletionDate(), "yyyy/MM" ));

        GraduationStatusEntity savedGraduationStatus = new GraduationStatusEntity();
        BeanUtils.copyProperties(graduationStatusEntity, savedGraduationStatus);
        savedGraduationStatus.setRecalculateGradStatus("Y");
        savedGraduationStatus.setProgram("2018-en");
        savedGraduationStatus.setProgramCompletionDate(graduationStatusEntity.getProgramCompletionDate());

        GradProgram program = new GradProgram();
        program.setProgramCode("2018-en");
        program.setProgramName("Graduation Program 2018");

        when(graduationStatusRepository.findById(studentID)).thenReturn(Optional.of(graduationStatusEntity));
        when(graduationStatusRepository.save(graduationStatusEntity)).thenReturn(savedGraduationStatus);

        when(this.webClient.get()).thenReturn(this.requestHeadersUriMock);
        when(this.requestHeadersUriMock.uri(String.format(constants.getGradProgramNameUrl(),program.getProgramCode()))).thenReturn(this.requestHeadersMock);
        when(this.requestHeadersMock.headers(any(Consumer.class))).thenReturn(this.requestHeadersMock);
        when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
        when(this.responseMock.bodyToMono(GradProgram.class)).thenReturn(Mono.just(program));

        var result = graduationStatusService.updateGraduationStatus(studentID, input, "accessToken");

        assertThat(result).isNotNull();
        assertThat(result.getStudentID()).isEqualTo(savedGraduationStatus.getStudentID());
        assertThat(result.getPen()).isEqualTo(savedGraduationStatus.getPen());
        assertThat(result.getStudentStatus()).isEqualTo(savedGraduationStatus.getStudentStatus());
        assertThat(result.getProgram()).isEqualTo(savedGraduationStatus.getProgram());
        assertThat(result.getSchoolOfRecord()).isEqualTo(savedGraduationStatus.getSchoolOfRecord());
        assertThat(result.getGpa()).isEqualTo(savedGraduationStatus.getGpa());

        assertThat(result.getRecalculateGradStatus()).isEqualTo(savedGraduationStatus.getRecalculateGradStatus());
        assertThat(result.getProgramCompletionDate()).isEqualTo(input.getProgramCompletionDate());
    }

    @Test
    public void testUpdateGraduationStatus_givenDifferentPrograms_when1950ProgramIsValidated_thenReturnErrorWithEmptyObject() {
        // ID
        UUID studentID = UUID.randomUUID();
        String pen = "123456789";
        String mincode = "12345678";

        GraduationStatusEntity graduationStatusEntity = new GraduationStatusEntity();
        graduationStatusEntity.setStudentID(studentID);
        graduationStatusEntity.setPen(pen);
        graduationStatusEntity.setStudentStatus("A");
        graduationStatusEntity.setStudentGrade("12");
        graduationStatusEntity.setProgram("2018-en");
        graduationStatusEntity.setSchoolOfRecord(mincode);
        graduationStatusEntity.setSchoolAtGrad(mincode);
        graduationStatusEntity.setGpa("4");
        graduationStatusEntity.setProgramCompletionDate(new Date(System.currentTimeMillis()));

        GraduationStatus input = new GraduationStatus();
        BeanUtils.copyProperties(graduationStatusEntity, input);
        input.setRecalculateGradStatus(null);
        input.setProgram("1950-en");
        input.setProgramCompletionDate(EducGradStatusApiUtils.formatDate(graduationStatusEntity.getProgramCompletionDate(), "yyyy/MM" ));

        GraduationStatusEntity savedGraduationStatus = new GraduationStatusEntity();
        BeanUtils.copyProperties(graduationStatusEntity, savedGraduationStatus);
        savedGraduationStatus.setRecalculateGradStatus("Y");
        savedGraduationStatus.setProgram("2018-en");
        savedGraduationStatus.setProgramCompletionDate(graduationStatusEntity.getProgramCompletionDate());

        GradProgram program = new GradProgram();
        program.setProgramCode("1950-en");
        program.setProgramName("Graduation Program 1950");

        when(graduationStatusRepository.findById(studentID)).thenReturn(Optional.of(graduationStatusEntity));
        when(graduationStatusRepository.save(graduationStatusEntity)).thenReturn(savedGraduationStatus);
        when(validation.hasErrors()).thenReturn(true);

        when(this.webClient.get()).thenReturn(this.requestHeadersUriMock);
        when(this.requestHeadersUriMock.uri(String.format(constants.getGradProgramNameUrl(),program.getProgramCode()))).thenReturn(this.requestHeadersMock);
        when(this.requestHeadersMock.headers(any(Consumer.class))).thenReturn(this.requestHeadersMock);
        when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
        when(this.responseMock.bodyToMono(GradProgram.class)).thenReturn(Mono.just(program));

        var result = graduationStatusService.updateGraduationStatus(studentID, input, "accessToken");

        assertThat(result).isNotNull();
        assertThat(result.getStudentID()).isNull();
        assertThat(result.getPen()).isNull();
        assertThat(result.getStudentStatus()).isNull();
        assertThat(result.getProgram()).isNull();
        assertThat(result.getSchoolOfRecord()).isNull();
        assertThat(result.getGpa()).isNull();

        assertThat(result.getRecalculateGradStatus()).isNull();
        assertThat(result.getProgramCompletionDate()).isNull();
    }

    @Test
    public void testUpdateGraduationStatus_givenDifferentPrograms_whenProgramIsValidatedForAdultGrade_thenReturnErrorWithEmptyObject() {
        // ID
        UUID studentID = UUID.randomUUID();
        String pen = "123456789";
        String mincode = "12345678";

        GraduationStatusEntity graduationStatusEntity = new GraduationStatusEntity();
        graduationStatusEntity.setStudentID(studentID);
        graduationStatusEntity.setPen(pen);
        graduationStatusEntity.setStudentStatus("A");
        graduationStatusEntity.setStudentGrade("AD");
        graduationStatusEntity.setProgram("2018-pf");
        graduationStatusEntity.setSchoolOfRecord(mincode);
        graduationStatusEntity.setSchoolAtGrad(mincode);
        graduationStatusEntity.setGpa("4");
        graduationStatusEntity.setProgramCompletionDate(new Date(System.currentTimeMillis()));

        GraduationStatus input = new GraduationStatus();
        BeanUtils.copyProperties(graduationStatusEntity, input);
        input.setRecalculateGradStatus(null);
        input.setProgram("2018-en");
        input.setProgramCompletionDate(EducGradStatusApiUtils.formatDate(graduationStatusEntity.getProgramCompletionDate(), "yyyy/MM" ));

        GraduationStatusEntity savedGraduationStatus = new GraduationStatusEntity();
        BeanUtils.copyProperties(graduationStatusEntity, savedGraduationStatus);
        savedGraduationStatus.setRecalculateGradStatus("Y");
        savedGraduationStatus.setProgram("2018-en");
        savedGraduationStatus.setProgramCompletionDate(graduationStatusEntity.getProgramCompletionDate());

        GradProgram program = new GradProgram();
        program.setProgramCode("2018-en");
        program.setProgramName("Graduation Program 2018");

        when(graduationStatusRepository.findById(studentID)).thenReturn(Optional.of(graduationStatusEntity));
        when(graduationStatusRepository.save(graduationStatusEntity)).thenReturn(savedGraduationStatus);
        when(validation.hasErrors()).thenReturn(true);

        when(this.webClient.get()).thenReturn(this.requestHeadersUriMock);
        when(this.requestHeadersUriMock.uri(String.format(constants.getGradProgramNameUrl(),program.getProgramCode()))).thenReturn(this.requestHeadersMock);
        when(this.requestHeadersMock.headers(any(Consumer.class))).thenReturn(this.requestHeadersMock);
        when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
        when(this.responseMock.bodyToMono(GradProgram.class)).thenReturn(Mono.just(program));

        var result = graduationStatusService.updateGraduationStatus(studentID, input, "accessToken");

        assertThat(result).isNotNull();
        assertThat(result.getStudentID()).isNull();
        assertThat(result.getPen()).isNull();
        assertThat(result.getStudentStatus()).isNull();
        assertThat(result.getProgram()).isNull();
        assertThat(result.getSchoolOfRecord()).isNull();
        assertThat(result.getGpa()).isNull();

        assertThat(result.getRecalculateGradStatus()).isNull();
        assertThat(result.getProgramCompletionDate()).isNull();
    }

    @Test
    public void testUpdateGraduationStatus_givenDifferentSchoolOfRecords_whenSchoolIsValidated_thenReturnSuccess() {
        // ID
        UUID studentID = UUID.randomUUID();
        String pen = "123456789";
        String mincode = "12345678";
        String newMincode = "87654321";

        GraduationStatusEntity graduationStatusEntity = new GraduationStatusEntity();
        graduationStatusEntity.setStudentID(studentID);
        graduationStatusEntity.setPen(pen);
        graduationStatusEntity.setStudentStatus("A");
        graduationStatusEntity.setStudentGrade("12");
        graduationStatusEntity.setProgram("2018-en");
        graduationStatusEntity.setSchoolOfRecord(mincode);
        graduationStatusEntity.setSchoolAtGrad(mincode);
        graduationStatusEntity.setGpa("4");
        graduationStatusEntity.setProgramCompletionDate(new Date(System.currentTimeMillis()));

        GraduationStatus input = new GraduationStatus();
        BeanUtils.copyProperties(graduationStatusEntity, input);
        input.setRecalculateGradStatus(null);
        input.setSchoolOfRecord(newMincode);
        input.setProgramCompletionDate(EducGradStatusApiUtils.formatDate(graduationStatusEntity.getProgramCompletionDate(), "yyyy/MM" ));

        GraduationStatusEntity savedGraduationStatus = new GraduationStatusEntity();
        BeanUtils.copyProperties(graduationStatusEntity, savedGraduationStatus);
        savedGraduationStatus.setRecalculateGradStatus("Y");
        savedGraduationStatus.setSchoolOfRecord(newMincode);
        savedGraduationStatus.setProgramCompletionDate(graduationStatusEntity.getProgramCompletionDate());

        School school = new School();
        school.setMinCode(newMincode);
        school.setSchoolName("Test School");
        school.setOpenFlag("Y");

        when(graduationStatusRepository.findById(studentID)).thenReturn(Optional.of(graduationStatusEntity));
        when(graduationStatusRepository.save(graduationStatusEntity)).thenReturn(savedGraduationStatus);

        when(this.webClient.get()).thenReturn(this.requestHeadersUriMock);
        when(this.requestHeadersUriMock.uri(String.format(constants.getGradSchoolNameUrl(),newMincode))).thenReturn(this.requestHeadersMock);
        when(this.requestHeadersMock.headers(any(Consumer.class))).thenReturn(this.requestHeadersMock);
        when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
        when(this.responseMock.bodyToMono(School.class)).thenReturn(Mono.just(school));

        var result = graduationStatusService.updateGraduationStatus(studentID, input, "accessToken");

        assertThat(result).isNotNull();
        assertThat(result.getStudentID()).isEqualTo(savedGraduationStatus.getStudentID());
        assertThat(result.getPen()).isEqualTo(savedGraduationStatus.getPen());
        assertThat(result.getStudentStatus()).isEqualTo(savedGraduationStatus.getStudentStatus());
        assertThat(result.getProgram()).isEqualTo(savedGraduationStatus.getProgram());
        assertThat(result.getSchoolOfRecord()).isEqualTo(savedGraduationStatus.getSchoolOfRecord());
        assertThat(result.getGpa()).isEqualTo(savedGraduationStatus.getGpa());

        assertThat(result.getRecalculateGradStatus()).isEqualTo(savedGraduationStatus.getRecalculateGradStatus());
        assertThat(result.getProgramCompletionDate()).isEqualTo(input.getProgramCompletionDate());
    }

    @Test
    public void testUpdateGraduationStatus_givenDifferentSchoolOfGrads_whenSchoolIsValidated_thenReturnSuccess() {
        // ID
        UUID studentID = UUID.randomUUID();
        String pen = "123456789";
        String mincode = "12345678";
        String newMincode = "87654321";

        GraduationStatusEntity graduationStatusEntity = new GraduationStatusEntity();
        graduationStatusEntity.setStudentID(studentID);
        graduationStatusEntity.setPen(pen);
        graduationStatusEntity.setStudentStatus("A");
        graduationStatusEntity.setStudentGrade("12");
        graduationStatusEntity.setProgram("2018-en");
        graduationStatusEntity.setSchoolOfRecord(mincode);
        graduationStatusEntity.setSchoolAtGrad(mincode);
        graduationStatusEntity.setGpa("4");
        graduationStatusEntity.setProgramCompletionDate(new Date(System.currentTimeMillis()));

        GraduationStatus input = new GraduationStatus();
        BeanUtils.copyProperties(graduationStatusEntity, input);
        input.setRecalculateGradStatus(null);
        input.setSchoolAtGrad(newMincode);
        input.setProgramCompletionDate(EducGradStatusApiUtils.formatDate(graduationStatusEntity.getProgramCompletionDate(), "yyyy/MM" ));

        GraduationStatusEntity savedGraduationStatus = new GraduationStatusEntity();
        BeanUtils.copyProperties(graduationStatusEntity, savedGraduationStatus);
        savedGraduationStatus.setRecalculateGradStatus("Y");
        savedGraduationStatus.setSchoolAtGrad(newMincode);
        savedGraduationStatus.setProgramCompletionDate(graduationStatusEntity.getProgramCompletionDate());

        School school = new School();
        school.setMinCode(newMincode);
        school.setSchoolName("Test School");
        school.setOpenFlag("Y");

        when(graduationStatusRepository.findById(studentID)).thenReturn(Optional.of(graduationStatusEntity));
        when(graduationStatusRepository.save(graduationStatusEntity)).thenReturn(savedGraduationStatus);

        when(this.webClient.get()).thenReturn(this.requestHeadersUriMock);
        when(this.requestHeadersUriMock.uri(String.format(constants.getGradSchoolNameUrl(),newMincode))).thenReturn(this.requestHeadersMock);
        when(this.requestHeadersMock.headers(any(Consumer.class))).thenReturn(this.requestHeadersMock);
        when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
        when(this.responseMock.bodyToMono(School.class)).thenReturn(Mono.just(school));

        var result = graduationStatusService.updateGraduationStatus(studentID, input, "accessToken");

        assertThat(result).isNotNull();
        assertThat(result.getStudentID()).isEqualTo(savedGraduationStatus.getStudentID());
        assertThat(result.getPen()).isEqualTo(savedGraduationStatus.getPen());
        assertThat(result.getStudentStatus()).isEqualTo(savedGraduationStatus.getStudentStatus());
        assertThat(result.getProgram()).isEqualTo(savedGraduationStatus.getProgram());
        assertThat(result.getSchoolAtGrad()).isEqualTo(savedGraduationStatus.getSchoolAtGrad());
        assertThat(result.getGpa()).isEqualTo(savedGraduationStatus.getGpa());

        assertThat(result.getRecalculateGradStatus()).isEqualTo(savedGraduationStatus.getRecalculateGradStatus());
        assertThat(result.getProgramCompletionDate()).isEqualTo(input.getProgramCompletionDate());
    }

    @Test
    public void testUpdateGraduationStatus_givenDifferentGPAs_whenHonoursStandingIsValidated_thenReturnSuccess() {
        // ID
        UUID studentID = UUID.randomUUID();
        String pen = "123456789";
        String mincode = "12345678";
        String newMincode = "87654321";

        GraduationStatusEntity graduationStatusEntity = new GraduationStatusEntity();
        graduationStatusEntity.setStudentID(studentID);
        graduationStatusEntity.setPen(pen);
        graduationStatusEntity.setStudentStatus("A");
        graduationStatusEntity.setStudentGrade("12");
        graduationStatusEntity.setProgram("2018-en");
        graduationStatusEntity.setSchoolOfRecord(mincode);
        graduationStatusEntity.setSchoolAtGrad(mincode);
        graduationStatusEntity.setGpa("3");
        graduationStatusEntity.setProgramCompletionDate(new Date(System.currentTimeMillis()));

        GraduationStatus input = new GraduationStatus();
        BeanUtils.copyProperties(graduationStatusEntity, input);
        input.setRecalculateGradStatus(null);
        input.setGpa("4");
        input.setProgramCompletionDate(EducGradStatusApiUtils.formatDate(graduationStatusEntity.getProgramCompletionDate(), "yyyy/MM" ));

        GraduationStatusEntity savedGraduationStatus = new GraduationStatusEntity();
        BeanUtils.copyProperties(graduationStatusEntity, savedGraduationStatus);
        savedGraduationStatus.setRecalculateGradStatus("Y");
        savedGraduationStatus.setGpa("4");
        savedGraduationStatus.setProgramCompletionDate(graduationStatusEntity.getProgramCompletionDate());

        when(graduationStatusRepository.findById(studentID)).thenReturn(Optional.of(graduationStatusEntity));
        when(graduationStatusRepository.save(graduationStatusEntity)).thenReturn(savedGraduationStatus);

        var result = graduationStatusService.updateGraduationStatus(studentID, input, "accessToken");

        assertThat(result).isNotNull();
        assertThat(result.getStudentID()).isEqualTo(savedGraduationStatus.getStudentID());
        assertThat(result.getPen()).isEqualTo(savedGraduationStatus.getPen());
        assertThat(result.getStudentStatus()).isEqualTo(savedGraduationStatus.getStudentStatus());
        assertThat(result.getProgram()).isEqualTo(savedGraduationStatus.getProgram());
        assertThat(result.getSchoolAtGrad()).isEqualTo(savedGraduationStatus.getSchoolAtGrad());
        assertThat(result.getGpa()).isEqualTo(savedGraduationStatus.getGpa());

        assertThat(result.getRecalculateGradStatus()).isEqualTo(savedGraduationStatus.getRecalculateGradStatus());
        assertThat(result.getProgramCompletionDate()).isEqualTo(input.getProgramCompletionDate());
    }

    @Test
    public void testGetStudentGradSpecialProgram() {
        // ID
        UUID gradStudentSpecialProgramID = UUID.randomUUID();
        UUID studentID = UUID.randomUUID();
        UUID specialProgramID = UUID.randomUUID();
        String pen = "123456789";

        GradStudentSpecialProgramEntity gradStudentSpecialProgramEntity = new GradStudentSpecialProgramEntity();
        gradStudentSpecialProgramEntity.setId(gradStudentSpecialProgramID);
        gradStudentSpecialProgramEntity.setStudentID(studentID);
        gradStudentSpecialProgramEntity.setSpecialProgramID(specialProgramID);
        gradStudentSpecialProgramEntity.setPen(pen);
        gradStudentSpecialProgramEntity.setSpecialProgramCompletionDate(new Date(System.currentTimeMillis()));

        GradSpecialProgram specialProgram = new GradSpecialProgram();
        specialProgram.setId(specialProgramID);
        specialProgram.setProgramCode("2018-en");
        specialProgram.setSpecialProgramCode("FI");
        specialProgram.setSpecialProgramName("French Immersion");

        when(gradStudentSpecialProgramRepository.findByStudentID(studentID)).thenReturn(Arrays.asList(gradStudentSpecialProgramEntity));

        when(this.webClient.get()).thenReturn(this.requestHeadersUriMock);
        when(this.requestHeadersUriMock.uri(String.format(constants.getGradSpecialProgramNameUrl(),specialProgramID))).thenReturn(this.requestHeadersMock);
        when(this.requestHeadersMock.headers(any(Consumer.class))).thenReturn(this.requestHeadersMock);
        when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
        when(this.responseMock.bodyToMono(GradSpecialProgram.class)).thenReturn(Mono.just(specialProgram));

        var result = graduationStatusService.getStudentGradSpecialProgram(studentID, "accessToken");

        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        GradStudentSpecialProgram responseStudentSpecialProgram = result.get(0);
        assertThat(responseStudentSpecialProgram.getStudentID()).isEqualTo(gradStudentSpecialProgramEntity.getStudentID());
        assertThat(responseStudentSpecialProgram.getSpecialProgramID()).isEqualTo(gradStudentSpecialProgramEntity.getSpecialProgramID());
        assertThat(responseStudentSpecialProgram.getSpecialProgramName()).isEqualTo(specialProgram.getSpecialProgramName());
        assertThat(responseStudentSpecialProgram.getSpecialProgramCode()).isEqualTo(specialProgram.getSpecialProgramCode());
        assertThat(responseStudentSpecialProgram.getMainProgramCode()).isEqualTo(specialProgram.getProgramCode());
    }

    @Test
    public void testSaveStudentGradSpecialProgram() {
        // ID
        UUID gradStudentSpecialProgramID = UUID.randomUUID();
        UUID studentID = UUID.randomUUID();
        UUID specialProgramID = UUID.randomUUID();
        String pen = "123456789";

        GradStudentSpecialProgramEntity gradStudentSpecialProgramEntity = new GradStudentSpecialProgramEntity();
        gradStudentSpecialProgramEntity.setId(gradStudentSpecialProgramID);
        gradStudentSpecialProgramEntity.setStudentID(studentID);
        gradStudentSpecialProgramEntity.setSpecialProgramID(specialProgramID);
        gradStudentSpecialProgramEntity.setPen(pen);
        gradStudentSpecialProgramEntity.setSpecialProgramCompletionDate(new Date(System.currentTimeMillis()));

        GradStudentSpecialProgram studentSpecialProgram = new GradStudentSpecialProgram();
        BeanUtils.copyProperties(gradStudentSpecialProgramEntity, studentSpecialProgram);
        studentSpecialProgram.setSpecialProgramCompletionDate(EducGradStatusApiUtils.formatDate(gradStudentSpecialProgramEntity.getSpecialProgramCompletionDate(), "yyyy-MM-dd" ));

        when(gradStudentSpecialProgramRepository.findById(gradStudentSpecialProgramID)).thenReturn(Optional.of(gradStudentSpecialProgramEntity));
        when(gradStudentSpecialProgramRepository.save(gradStudentSpecialProgramEntity)).thenReturn(gradStudentSpecialProgramEntity);

        var result = graduationStatusService.saveStudentGradSpecialProgram(studentSpecialProgram);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(gradStudentSpecialProgramEntity.getId());
        assertThat(result.getSpecialProgramID()).isEqualTo(gradStudentSpecialProgramEntity.getSpecialProgramID());
        assertThat(result.getSpecialProgramCompletionDate()).isEqualTo(EducGradStatusApiUtils.parseDateFromString(studentSpecialProgram.getSpecialProgramCompletionDate()));
    }

    @Test
    public void testUpdateStudentGradSpecialProgram() {
        // ID
        UUID gradStudentSpecialProgramID = UUID.randomUUID();
        UUID studentID = UUID.randomUUID();
        UUID specialProgramID = UUID.randomUUID();
        String pen = "123456789";

        GradStudentSpecialProgramEntity gradStudentSpecialProgramEntity = new GradStudentSpecialProgramEntity();
        gradStudentSpecialProgramEntity.setId(gradStudentSpecialProgramID);
        gradStudentSpecialProgramEntity.setStudentID(studentID);
        gradStudentSpecialProgramEntity.setSpecialProgramID(specialProgramID);
        gradStudentSpecialProgramEntity.setPen(pen);
        gradStudentSpecialProgramEntity.setSpecialProgramCompletionDate(new Date(System.currentTimeMillis()));

        GradStudentSpecialProgram studentSpecialProgram = new GradStudentSpecialProgram();
        BeanUtils.copyProperties(gradStudentSpecialProgramEntity, studentSpecialProgram);
        studentSpecialProgram.setSpecialProgramCompletionDate(EducGradStatusApiUtils.formatDate(gradStudentSpecialProgramEntity.getSpecialProgramCompletionDate(), "yyyy-MM-dd" ));

        GradStudentSpecialProgramReq gradStudentSpecialProgramReq = new GradStudentSpecialProgramReq();
        gradStudentSpecialProgramReq.setId(gradStudentSpecialProgramID);
        gradStudentSpecialProgramReq.setStudentID(studentID);
        gradStudentSpecialProgramReq.setPen(pen);
        gradStudentSpecialProgramReq.setMainProgramCode("2018-en");
        gradStudentSpecialProgramReq.setSpecialProgramCode("FI");
        gradStudentSpecialProgramReq.setSpecialProgramCompletionDate(studentSpecialProgram.getSpecialProgramCompletionDate());

        GradSpecialProgram specialProgram = new GradSpecialProgram();
        specialProgram.setId(specialProgramID);
        specialProgram.setProgramCode("2018-en");
        specialProgram.setSpecialProgramCode("FI");
        specialProgram.setSpecialProgramName("French Immersion");

        when(gradStudentSpecialProgramRepository.findById(gradStudentSpecialProgramID)).thenReturn(Optional.of(gradStudentSpecialProgramEntity));
        when(gradStudentSpecialProgramRepository.save(gradStudentSpecialProgramEntity)).thenReturn(gradStudentSpecialProgramEntity);

        when(this.webClient.get()).thenReturn(this.requestHeadersUriMock);
        when(this.requestHeadersUriMock.uri(String.format(constants.getGradSpecialProgramDetailsUrl(),specialProgram.getProgramCode(), specialProgram.getSpecialProgramCode()))).thenReturn(this.requestHeadersMock);
        when(this.requestHeadersMock.headers(any(Consumer.class))).thenReturn(this.requestHeadersMock);
        when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
        when(this.responseMock.bodyToMono(GradSpecialProgram.class)).thenReturn(Mono.just(specialProgram));

        var result = graduationStatusService.updateStudentGradSpecialProgram(gradStudentSpecialProgramReq, "accessToken");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(gradStudentSpecialProgramEntity.getId());
        assertThat(result.getSpecialProgramID()).isEqualTo(gradStudentSpecialProgramEntity.getSpecialProgramID());
        assertThat(result.getSpecialProgramCompletionDate()).isEqualTo(EducGradStatusApiUtils.parseDateFromString(studentSpecialProgram.getSpecialProgramCompletionDate()));
    }

    @Test
    public void testGetStudentsForGraduation() {
        GraduationStatusEntity graduationStatusEntity = new GraduationStatusEntity();
        graduationStatusEntity.setStudentID(UUID.randomUUID());
        graduationStatusEntity.setPen("123456789");
        graduationStatusEntity.setStudentStatus("A");
        graduationStatusEntity.setSchoolOfRecord("12345678");
        graduationStatusEntity.setRecalculateGradStatus("Y");

        when(graduationStatusRepository.findByRecalculateGradStatus(graduationStatusEntity.getRecalculateGradStatus())).thenReturn(Arrays.asList(graduationStatusEntity));
        var result = graduationStatusService.getStudentsForGraduation();
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        GraduationStatus responseGraduationStatus = result.get(0);
        assertThat(responseGraduationStatus.getStudentID()).isEqualTo(graduationStatusEntity.getStudentID());
        assertThat(responseGraduationStatus.getPen()).isEqualTo(graduationStatusEntity.getPen());
        assertThat(responseGraduationStatus.getStudentStatus()).isEqualTo(graduationStatusEntity.getStudentStatus());
        assertThat(responseGraduationStatus.getSchoolOfRecord()).isEqualTo(graduationStatusEntity.getSchoolOfRecord());
    }

    @Test
    public void testGetStudentGradSpecialProgramByProgramCodeAndSpecialProgramCode() {
        // ID
        UUID gradStudentSpecialProgramID = UUID.randomUUID();
        UUID studentID = UUID.randomUUID();
        UUID specialProgramID = UUID.randomUUID();
        String pen = "123456789";

        GradStudentSpecialProgramEntity gradStudentSpecialProgramEntity = new GradStudentSpecialProgramEntity();
        gradStudentSpecialProgramEntity.setId(gradStudentSpecialProgramID);
        gradStudentSpecialProgramEntity.setStudentID(studentID);
        gradStudentSpecialProgramEntity.setSpecialProgramID(specialProgramID);
        gradStudentSpecialProgramEntity.setPen(pen);
        gradStudentSpecialProgramEntity.setSpecialProgramCompletionDate(new Date(System.currentTimeMillis()));

        GradStudentSpecialProgram studentSpecialProgram = new GradStudentSpecialProgram();
        BeanUtils.copyProperties(gradStudentSpecialProgramEntity, studentSpecialProgram);
        studentSpecialProgram.setSpecialProgramCompletionDate(EducGradStatusApiUtils.formatDate(gradStudentSpecialProgramEntity.getSpecialProgramCompletionDate(), "yyyy-MM-dd" ));

        GradSpecialProgram specialProgram = new GradSpecialProgram();
        specialProgram.setId(specialProgramID);
        specialProgram.setProgramCode("2018-en");
        specialProgram.setSpecialProgramCode("FI");
        specialProgram.setSpecialProgramName("French Immersion");

        when(gradStudentSpecialProgramRepository.findByStudentIDAndSpecialProgramID(studentID, specialProgramID)).thenReturn(Optional.of(gradStudentSpecialProgramEntity));

        when(this.webClient.get()).thenReturn(this.requestHeadersUriMock);
        when(this.requestHeadersUriMock.uri(String.format(constants.getGradSpecialProgramNameUrl(),specialProgramID))).thenReturn(this.requestHeadersMock);
        when(this.requestHeadersMock.headers(any(Consumer.class))).thenReturn(this.requestHeadersMock);
        when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
        when(this.responseMock.bodyToMono(GradSpecialProgram.class)).thenReturn(Mono.just(specialProgram));

        var result = graduationStatusService.getStudentGradSpecialProgramByProgramCodeAndSpecialProgramCode(studentID, specialProgramID.toString(), "accessToken");

        assertThat(result).isNotNull();
        assertThat(result.getStudentID()).isEqualTo(gradStudentSpecialProgramEntity.getStudentID());
        assertThat(result.getSpecialProgramID()).isEqualTo(gradStudentSpecialProgramEntity.getSpecialProgramID());
        assertThat(result.getMainProgramCode()).isEqualTo(specialProgram.getProgramCode());
        assertThat(result.getSpecialProgramCode()).isEqualTo(specialProgram.getSpecialProgramCode());
        assertThat(result.getSpecialProgramName()).isEqualTo(specialProgram.getSpecialProgramName());
    }

    @Test
    public void testGetStudentStatus() {
        String statusCode = "A";

        GraduationStatusEntity graduationStatusEntity = new GraduationStatusEntity();
        graduationStatusEntity.setStudentID(UUID.randomUUID());
        graduationStatusEntity.setPen("123456789");
        graduationStatusEntity.setStudentStatus("A");
        graduationStatusEntity.setSchoolOfRecord("12345678");
        graduationStatusEntity.setRecalculateGradStatus("Y");

        when(graduationStatusRepository.existsByStatusCode(statusCode)).thenReturn(Arrays.asList(graduationStatusEntity));
        var result = graduationStatusService.getStudentStatus(statusCode);
        assertThat(result).isTrue();
    }

    @Test
    public void testUgradStudent() {
        // ID
        UUID studentID = UUID.randomUUID();
        String pen = "123456789";
        String ungradReasonCode = "NM";
        String ungradReasonDesc = "FDFS";

        GradUngradReasons ungradReasons = new GradUngradReasons();
        ungradReasons.setCode(ungradReasonCode);
        ungradReasons.setDescription("Not Met");

        GraduationStatusEntity graduationStatusEntity = new GraduationStatusEntity();
        graduationStatusEntity.setStudentID(studentID);
        graduationStatusEntity.setPen(pen);
        graduationStatusEntity.setStudentStatus("A");
        graduationStatusEntity.setSchoolOfRecord("12345678");

        GraduationStatusEntity responseGraduationStatus = new GraduationStatusEntity();
        BeanUtils.copyProperties(graduationStatusEntity, responseGraduationStatus);
        responseGraduationStatus.setRecalculateGradStatus("Y");
        responseGraduationStatus.setProgramCompletionDate(null);
        responseGraduationStatus.setHonoursStanding(null);
        responseGraduationStatus.setGpa(null);
        responseGraduationStatus.setSchoolAtGrad(null);

        GradStudentUngradReasons responseStudentUngradReasons = new GradStudentUngradReasons();
        responseStudentUngradReasons.setStudentID(studentID);
        responseStudentUngradReasons.setPen(pen);
        responseStudentUngradReasons.setUngradReasonCode(ungradReasonCode);

        when(this.webClient.get()).thenReturn(this.requestHeadersUriMock);
        when(this.requestHeadersUriMock.uri(String.format(constants.getUngradReasonDetailsUrl(),ungradReasonCode))).thenReturn(this.requestHeadersMock);
        when(this.requestHeadersMock.headers(any(Consumer.class))).thenReturn(this.requestHeadersMock);
        when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
        when(this.responseMock.bodyToMono(GradUngradReasons.class)).thenReturn(Mono.just(ungradReasons));

        when(this.webClient.post()).thenReturn(this.requestBodyUriMock);
        when(this.requestBodyUriMock.uri(String.format(constants.getSaveStudentUngradReasonByStudentIdUrl(),studentID))).thenReturn(this.requestBodyUriMock);
        when(this.requestBodyUriMock.headers(any(Consumer.class))).thenReturn(this.requestBodyMock);
        when(this.requestBodyMock.body(any(BodyInserter.class))).thenReturn(this.requestHeadersMock);
        when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
        when(this.responseMock.bodyToMono(GradStudentUngradReasons.class)).thenReturn(Mono.just(responseStudentUngradReasons));

        when(graduationStatusRepository.findById(studentID)).thenReturn(Optional.of(graduationStatusEntity));
        when(graduationStatusRepository.save(responseGraduationStatus)).thenReturn(responseGraduationStatus);

        var result = graduationStatusService.ungradStudent(studentID, ungradReasonCode,ungradReasonDesc, "accessToken");

        assertThat(result).isNotNull();
        assertThat(result.getStudentID()).isEqualTo(graduationStatusEntity.getStudentID());
        assertThat(result.getRecalculateGradStatus()).isEqualTo("Y");
        assertThat(result.getProgramCompletionDate()).isNull();
        assertThat(result.getHonoursStanding()).isNull();
        assertThat(result.getGpa()).isNull();
        assertThat(result.getSchoolAtGrad()).isNull();
    }

    @Test
    public void saveUngradReason() {
        // ID
        UUID studentID = UUID.randomUUID();
        String pen = "123456789";
        String ungradReasonCode = "NM";
        String ungradReasonDesc= "FDFS";

        GradStudentUngradReasons responseStudentUngradReasons = new GradStudentUngradReasons();
        responseStudentUngradReasons.setStudentID(studentID);
        responseStudentUngradReasons.setPen(pen);
        responseStudentUngradReasons.setUngradReasonCode(ungradReasonCode);

        when(this.webClient.post()).thenReturn(this.requestBodyUriMock);
        when(this.requestBodyUriMock.uri(String.format(constants.getSaveStudentUngradReasonByStudentIdUrl(),studentID))).thenReturn(this.requestBodyUriMock);
        when(this.requestBodyUriMock.headers(any(Consumer.class))).thenReturn(this.requestBodyMock);
        when(this.requestBodyMock.body(any(BodyInserter.class))).thenReturn(this.requestHeadersMock);
        when(this.requestHeadersMock.retrieve()).thenReturn(this.responseMock);
        when(this.responseMock.bodyToMono(GradStudentUngradReasons.class)).thenReturn(Mono.just(responseStudentUngradReasons));

        graduationStatusService.saveUngradReason(pen, studentID, ungradReasonCode,ungradReasonDesc, "accessToken");

    }
}
