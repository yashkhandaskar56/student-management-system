package com.example.service;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.dto.ExamDto;
import com.example.dto.OptionDto;
import com.example.dto.QuestionDto;
import com.example.entity.AdmissionApplication;
import com.example.entity.ExamAssign;
import com.example.entity.ExamEntity;
import com.example.entity.OptionEntity;
import com.example.entity.QuestionEntity;
import com.example.repository.AdmissionRepo;
import com.example.repository.ExamAssignRepo;
import com.example.repository.ExamRepo;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class CreateExamSer {

    @Autowired
    private ExamRepo examRepo;
    
    @Autowired
    private AdmissionRepo admissionRepo;
    
    @Autowired
    private ExamAssignRepo assignRepo;

    // ==========================
    // ✅ CREATE EXAM
    // ==========================
    public ExamEntity createExam(ExamDto dto) {

        ExamEntity exam = new ExamEntity();
        exam.setTitle(dto.getTitle());
        exam.setCourseName(dto.getCourseName());
        exam.setDurationMinutes(dto.getDurationMinutes());

        List<QuestionEntity> questions = dto.getQuestionList()
                .stream()
                .map(qdto -> mapQuestion(qdto, exam))
                .collect(Collectors.toList());

        exam.setQuestionList(questions);

        return examRepo.save(exam);
    }


    // ==========================
    // GET ALL
    // ==========================
    public List<ExamEntity> getAllExam() {
        return examRepo.findAll();
    }

    // ==========================
    // GET BY ID
    // ==========================
    public ExamEntity getExamById(Long id) {
        return examRepo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Exam not found with id: " + id));
    }

    // ==========================
    // ADD QUESTIONS
    // ==========================
    public ExamEntity addQuestionToExam(Long id, ExamDto dto) {

        ExamEntity exam = getExamById(id);

        List<QuestionEntity> newQuestions = dto.getQuestionList()
                .stream()
                .map(qdto -> mapQuestion(qdto, exam))
                .collect(Collectors.toList());

        exam.getQuestionList().addAll(newQuestions);

        return examRepo.save(exam);
    }

    // ==========================
    // DELETE
    // ==========================
    public void deleteExam(Long id) {
        examRepo.deleteById(id);
    }

    // ==========================
    // GENERATE HALL TICKET
    // ==========================
    public void generateHallticket(
            String email,
            HttpServletResponse response)
            throws DocumentException, IOException {

        List<AdmissionApplication> students =
                admissionRepo.findByEmail(email);

        if (students.isEmpty())
            throw new RuntimeException("Student not found");

        response.setContentType("application/pdf");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=HallTicket.pdf");

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document,
                response.getOutputStream());

        document.open();

        document.add(new Paragraph("INSTITUTE OF ISEES TECHNOLOGIES"));
        document.add(new Paragraph("HALL TICKET"));
        document.add(new Paragraph("------------------------------------"));

        for (AdmissionApplication student : students) {

            document.add(new Paragraph("Name: " + student.getSname()));
            document.add(new Paragraph("Email: " + student.getEmail()));
            document.add(new Paragraph("Course: " + student.getCourseName()));
            document.add(new Paragraph("Hall Ticket No: HT-" + student.getId()));
            document.add(new Paragraph(" "));
        }

        document.close();
    }

    // ==========================
    // PRIVATE MAPPING METHOD
    // ==========================
    private QuestionEntity mapQuestion(
            QuestionDto dto,
            ExamEntity exam) {

        QuestionEntity question = new QuestionEntity();
        question.setQuestion(dto.getQuestion());
        question.setMarks(dto.getMarks());
        question.setCorrectOptionIndex(
        	    dto.getCorrectOptionIndex() != null 
        	        ? dto.getCorrectOptionIndex() 
        	        : 0
        	);
        question.setExam(exam);

        List<OptionEntity> options = dto.getOptionList()
                .stream()
                .map(optionDto -> mapOption(optionDto, question))
                .collect(Collectors.toList());

        question.setOptionList(options);

        return question;
    }
    
    private OptionEntity mapOption(
            OptionDto dto,
            QuestionEntity question) {

        OptionEntity option = new OptionEntity();
        option.setOptionText(dto.getOptionText());
        option.setQuestion(question);

        return option;
    }


    public ExamEntity startExam(Long id, String email) {

        ExamAssign assign = assignRepo
            .findByStudentEmailIgnoreCaseAndExam_ExamId(email, id)
            .orElseThrow(() -> new RuntimeException("Exam not assigned to this student"));

        if (!assign.isAvailable()) {
            throw new RuntimeException("Exam already attempted");
        }

        return assign.getExam();
    }
    
    public List<ExamEntity> getExamForStudent(String email) {

        List<ExamAssign> assigned =
                assignRepo.findByStudentEmailIgnoreCaseAndAvailableTrue(email);

        return assigned.stream()
                .map(ExamAssign::getExam)
                .toList();
    }
    
	public ExamEntity updateExam(ExamDto dto){

	    ExamEntity exam = examRepo.findById(dto.getExamId())
	            .orElseThrow(() -> new RuntimeException("Exam not found"));

	    exam.setTitle(dto.getTitle());
	    exam.setCourseName(dto.getCourseName());
	    exam.setDurationMinutes(dto.getDurationMinutes());

	    List<QuestionEntity> existingQuestions = exam.getQuestionList();

	    Map<Long, QuestionEntity> existingMap = existingQuestions.stream()
	            .filter(q -> q.getQuestionId() != null)
	            .collect(Collectors.toMap(QuestionEntity::getQuestionId, q -> q));

	    List<QuestionEntity> updatedList = new ArrayList<>();

	    for (QuestionDto qdto : dto.getQuestionList()) {

	        // ✅ EXISTING UPDATE
	        if (qdto.getQuestionId() != null && existingMap.containsKey(qdto.getQuestionId())) {

	            QuestionEntity existingQ = existingMap.get(qdto.getQuestionId());

	            existingQ.setQuestion(qdto.getQuestion());
	            existingQ.setMarks(qdto.getMarks());
	            existingQ.setCorrectOptionIndex(qdto.getCorrectOptionIndex());

	            // options update
	            existingQ.getOptionList().clear();

	            List<OptionEntity> options = qdto.getOptionList()
	                    .stream()
	                    .map(opt -> mapOption(opt, existingQ))
	                    .collect(Collectors.toList());

	            existingQ.getOptionList().addAll(options);

	            updatedList.add(existingQ);

	        } else {
	            // ✅ NEW QUESTION
	            QuestionEntity newQ = mapQuestion(qdto, exam);
	            updatedList.add(newQ);
	        }
	    }

	    // ✅ REMOVE ONLY DELETED QUESTIONS
	    existingQuestions.removeIf(q -> 
	        updatedList.stream()
	            .noneMatch(u -> u.getQuestionId() != null && u.getQuestionId().equals(q.getQuestionId()))
	    );

	    // ✅ ADD NEW QUESTIONS ONLY
	    for (QuestionEntity q : updatedList) {
	        if (q.getQuestionId() == null) {
	            existingQuestions.add(q);
	        }
	    }

	    return examRepo.save(exam);
	}





}
	


