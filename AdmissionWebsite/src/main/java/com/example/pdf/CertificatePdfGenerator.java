package com.example.pdf;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

import com.example.entity.CertificateEntity;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

public class CertificatePdfGenerator {
	
	public static byte[] generate(CertificateEntity cert){

	    try{
	        ByteArrayOutputStream out = new ByteArrayOutputStream();

	        Document document = new Document(PageSize.A4.rotate()); // Landscape
	        PdfWriter writer = PdfWriter.getInstance(document, out);

	        document.open();
	        
	        try {

	            Image logo = Image.getInstance(
	                    CertificatePdfGenerator.class
	                    .getResource("/static/image/icons/iseesLogo.jpg")
	            );

	            // LOGO SIZE
	            logo.scaleAbsolute(70, 70);

	            // LEFT TOP POSITION
	            logo.setAbsolutePosition(40, 500);

	            document.add(logo);

	        } catch (Exception e) {

	            System.out.println("Logo not found");

	        }

	        // 🎨 Colors
	        BaseColor borderColor = new BaseColor(255, 140, 0); // Orange
	        BaseColor titleColor = new BaseColor(0, 51, 102); // Dark Blue

	        // 🖋 Fonts
	        Font titleFont = new Font(Font.FontFamily.HELVETICA, 30, Font.BOLD, titleColor);
	        Font subTitle = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
	        Font normal = new Font(Font.FontFamily.HELVETICA, 14);
	        Font nameFont = new Font(Font.FontFamily.HELVETICA, 26, Font.BOLD);
	        Font courseFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, BaseColor.BLUE);

	        // 🔲 BORDER
	        PdfContentByte canvas = writer.getDirectContent();
	        Rectangle rect = new Rectangle(20, 20, 820, 575);
	        rect.setBorder(Rectangle.BOX);
	        rect.setBorderWidth(5);
	        rect.setBorderColor(borderColor);
	        canvas.rectangle(rect);

	        // 🏫 Institute Name
	        Paragraph institute = new Paragraph("ISEES TECHNOLOGY", subTitle);
	        institute.setAlignment(Element.ALIGN_CENTER);
	        document.add(institute);

	        document.add(new Paragraph(" "));

	        // 🎓 TITLE
	        Paragraph title = new Paragraph("CERTIFICATE OF COMPLETION", titleFont);
	        title.setAlignment(Element.ALIGN_CENTER);
	        document.add(title);

	        document.add(new Paragraph(" "));
	        document.add(new Paragraph(" "));

	        // 📄 Body
	        Paragraph line1 = new Paragraph("This certificate is proudly presented to", normal);
	        line1.setAlignment(Element.ALIGN_CENTER);
	        document.add(line1);

	        document.add(new Paragraph(" "));

	        // 👤 STUDENT NAME
	        Paragraph name = new Paragraph(cert.getStudentName(), nameFont);
	        name.setAlignment(Element.ALIGN_CENTER);
	        document.add(name);

	        document.add(new Paragraph(" "));

	        Paragraph line2 = new Paragraph("has successfully completed the course", normal);
	        line2.setAlignment(Element.ALIGN_CENTER);
	        document.add(line2);

	        document.add(new Paragraph(" "));

	        // 📚 COURSE NAME
	        Paragraph course = new Paragraph(cert.getCourseName(), courseFont);
	        course.setAlignment(Element.ALIGN_CENTER);
	        document.add(course);

	        document.add(new Paragraph(" "));
	        document.add(new Paragraph(" "));

	        // 📅 DATE + CERT NO
	        String date = cert.getIssuedate().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));

	        Paragraph info = new Paragraph(
	                "Date: " + date + "        Certificate No: " + cert.getCertificateNo(),
	                normal);
	        info.setAlignment(Element.ALIGN_CENTER);
	        document.add(info);

	        document.add(new Paragraph(" "));
	        document.add(new Paragraph(" "));
	        document.add(new Paragraph(" "));

	        // ✍️ SIGNATURES
	        Paragraph sign1 = new Paragraph("____________________", normal);
	        sign1.setAlignment(Element.ALIGN_LEFT);

	        Paragraph sign2 = new Paragraph("____________________", normal);
	        sign2.setAlignment(Element.ALIGN_RIGHT);

	        document.add(sign1);
	        document.add(sign2);

	        Paragraph signText1 = new Paragraph("Instructor", normal);
	        signText1.setAlignment(Element.ALIGN_LEFT);

	        Paragraph signText2 = new Paragraph("Authorized Sign", normal);
	        signText2.setAlignment(Element.ALIGN_RIGHT);

	        document.add(signText1);
	        document.add(signText2);

	        document.close();

	        return out.toByteArray();

	    } catch(Exception e){
	        e.printStackTrace();
	        throw new RuntimeException("Error generating PDF");
	    }
	}



}
