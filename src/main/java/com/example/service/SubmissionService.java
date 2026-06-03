package com.example.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.example.dto.ExamAttemptDto;
import com.example.dto.ExamSummaryDto;
import com.example.dto.ScoreCardDto;
import com.example.dto.SubmissionAnswerDto;
import com.example.dto.SubmissionDto;
import com.example.entity.ExamEntity;
import com.example.entity.QuestionEntity;
import com.example.entity.SubmissionAnswerEntity;
import com.example.entity.SubmissionExam;
import com.example.repository.ExamRepo;
import com.example.repository.QuestionRepo;
import com.example.repository.SubmissionAnsRepo;
import com.example.repository.SubmissionRepo;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;


@Service
public class SubmissionService {
	
	@Autowired
	private SubmissionRepo submissionRepo;
	
	@Autowired
	private SubmissionAnsRepo ansRepo;
	
	@Autowired
	private ExamRepo examRepo;
	
	@Autowired
	private JavaMailSender sender;
	@Autowired
	private Configuration config;
	
	@Autowired
	private QuestionRepo questionRepo;
	
	@Autowired
	private ActivityService activityService;
	
	// ==========================
    // SUBMIT EXAM
    // ==========================
	public SubmissionExam submitExam(SubmissionDto dto) {

	    ExamEntity exam = examRepo.findById(dto.getExamId())
	            .orElseThrow(() -> new RuntimeException("Exam not found"));

	    Optional<SubmissionExam> existing =
	            submissionRepo.findByEmailAndExam(
	                    dto.getEmail(),
	                    exam
	            );

	    if (existing.isPresent()) {
	        throw new RuntimeException("You already attempted this exam");
	    }
	    
	    SubmissionExam submission = new SubmissionExam();
	    submission.setEmail(dto.getEmail());
	    submission.setExam(exam);
	    submission.setSubmittedAt(LocalDateTime.now());

	    int totalScore = 0;
	    int totalMarks = 0;

	    List<SubmissionAnswerEntity> answerList = new ArrayList<>();

	    for (SubmissionAnswerDto answerDto : dto.getAnswers()) {

	        QuestionEntity question = questionRepo.findById(answerDto.getQuestionId())
	                .orElseThrow(() -> new RuntimeException("Question not found"));

	        SubmissionAnswerEntity answer = new SubmissionAnswerEntity();

	        answer.setQuestion(question);
	        answer.setSubmission(submission);
	        answer.setSelectedOptionIndex(answerDto.getSelectedOptionIndex());

	        boolean isCorrect = false;

	        if (answerDto.getSelectedOptionIndex() != null &&
	                answerDto.getSelectedOptionIndex()
	                        .equals(question.getCorrectOptionIndex())) {

	            isCorrect = true;
	        }

	        answer.setIsCorrect(isCorrect);

	        int obtainedMarks = isCorrect ? question.getMarks() : 0;

	        answer.setObtainedMarks(obtainedMarks);

	        totalScore += obtainedMarks;
	        totalMarks += question.getMarks();

	        answerList.add(answer);
	    }

	    submission.setSubmissionAnswers(answerList);

	    submission.setTotalScore(totalScore);
	    submission.setTotalMarks(totalMarks);

	    double percentage = totalMarks == 0
	            ? 0
	            : (double) totalScore / totalMarks * 100;

	    submission.setPercentage(percentage);

	    if (percentage >= 90)
	        submission.setGrade("A");
	    else if (percentage >= 75)
	        submission.setGrade("B");
	    else if (percentage >= 50)
	        submission.setGrade("C");
	    else
	        submission.setGrade("Fail");

	    // SAVE SUBMISSION
	    SubmissionExam savedSubmission = submissionRepo.save(submission);

	    // ✅ RECENT ACTIVITY SAVE
	    activityService.saveActivity(dto.getEmail(),
	            exam.getTitle() + " Exam Attempted");

	    return savedSubmission;
	}
	
	
    // ==========================
    // EMAIL SENDING
    // ==========================
    private void sendResultEmail(
            SubmissionDto dto,
            SubmissionExam submission) throws Exception {

        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper =
                new MimeMessageHelper(message, true);

        Map<String, Object> model = new HashMap<>();
        model.put("TotalScore", submission.getTotalScore());
        model.put("TotalMarks", submission.getTotalMarks());
        model.put("Percentage", submission.getPercentage());
        model.put("Grade", submission.getGrade());

        Template template =
                config.getTemplate("exam-result.ftl");

        String html = FreeMarkerTemplateUtils
                .processTemplateIntoString(template, model);

        helper.setTo(dto.getEmail());
        helper.setSubject("Exam Result");
        helper.setText(html, true);

        sender.send(message);
    }

    private SubmissionAnswerEntity mapAnswer(
            SubmissionAnswerDto dto,
            SubmissionExam submission) {

        SubmissionAnswerEntity ans = new SubmissionAnswerEntity();

        QuestionEntity question = questionRepo
                .findById(dto.getQuestionId())   // ✅ no casting
                .orElseThrow(() ->
                        new RuntimeException("Question not found"));

        ans.setQuestion(question);
        ans.setSelectedOptionIndex(dto.getSelectedOptionIndex());
        ans.setSubmission(submission);

        return ans;
    }

    // ==========================
    // OTHER METHODS (same logic)
    // ==========================

    public List<SubmissionExam> getAll() {
        return submissionRepo.findAll();
    }

    public ScoreCardDto getScoreCard(Long submissionId) {

        SubmissionExam submission = submissionRepo.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Result not found"));

        ScoreCardDto dto = new ScoreCardDto();

        
        dto.setExamName(submission.getExam().getTitle());
        dto.setEmail(submission.getEmail());
        dto.setTotalScore(submission.getTotalScore());
        dto.setTotalMarks(submission.getTotalMarks());
        dto.setPercentage(submission.getPercentage());
        dto.setGrade(submission.getGrade());
        dto.setSubmittedAt(submission.getSubmittedAt());

        return dto;
    }

    public Long countExam() {
        return submissionRepo.countTotalExam();
    }

    public Long countExamAttempt() {
        return submissionRepo.countTotalAttempt();
    }

    public Long attemptToday() {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(23, 59, 59);
        return submissionRepo.countAttemptToday(start, end);
    }

    public double getPassingPercentage() {
        Long passed = submissionRepo.countPassedStudents();
        Long total = submissionRepo.countTotalAttempt();
        return total == 0 ? 0 : (passed * 100.0) / total;
    }

    public Double getFailedPercentage() {
        return submissionRepo.findFailedPercentage();
    }

    public ExamSummaryDto getSummary(String email) {
        // same logic
        return null;
    }

    public List<ExamAttemptDto> getStudentExamHistory(String email) {
        return submissionRepo
                .findByEmailOrderBySubmittedAtDesc(email)
                .stream()
                .map(s -> {
                    ExamAttemptDto dto =
                            new ExamAttemptDto();
                    dto.setSubmissionId(s.getSubmissionId());
                    dto.setExamName(s.getExam().getTitle());
                    dto.setScore(s.getTotalScore());
                    dto.setTotalMarks(s.getTotalMarks());
                    dto.setPercentage(s.getPercentage());
                    dto.setGrade(s.getGrade());
                    dto.setSubmittedAt(s.getSubmittedAt());
                    return dto;
                }).toList();
    }

    private static final double PASS_PERCENTAGE = 40.0;

    public boolean isExamPassed(String email, String courseName) {

        SubmissionExam exam = submissionRepo
                .findTopByEmailAndExam_CourseNameOrderBySubmittedAtDesc(
                        email, courseName)
                .orElseThrow(() ->
                        new RuntimeException("Exam not found"));

        return exam.getPercentage() >= PASS_PERCENTAGE;
    }

	

}
