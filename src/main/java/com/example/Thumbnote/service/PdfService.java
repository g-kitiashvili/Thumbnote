package com.example.Thumbnote.service;

import com.example.Thumbnote.objects.Note;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

@Service
public class PdfService {

    private final NoteService noteService;

    @Autowired
    public PdfService(NoteService noteService) {
        this.noteService = noteService;
    }

    public Note createNoteFromPdf(String username, MultipartFile pdfFile) {
        if (pdfFile == null || pdfFile.getOriginalFilename() == null) {
            // Return null if the pdfFile parameter is null or the original filename is null
            return null;
        }        if(!pdfFile.getOriginalFilename().toLowerCase().endsWith(".pdf")) {
            return null;
        }
        try (PDDocument document = PDDocument.load(pdfFile.getInputStream())) {
            // Parse the PDF document and extract the text content
            PDFTextStripper stripper = new PDFTextStripper();
            String pdfText = stripper.getText(document);

            // Create a new Note object with the extracted text content and other data
            Note note = new Note(0,0,0,new Date(),pdfFile.getOriginalFilename(),pdfText,null);


            // Save the Note object to the database
            boolean success = noteService.createNote(username, note);
            if (success) {
                return note;
            } else {
                return null;
            }
        } catch (IOException e) {
            // Handle the IOException and return null
            e.printStackTrace();
            return null;
        }
    }

    public byte[] generatePdfFromNote(Note note) throws IOException, DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();
        document.add(new Paragraph(note.getNoteText()));
        document.close();
        return outputStream.toByteArray();
    }

    public ResponseEntity<byte[]> downloadNoteAsPdf(String username, Long noteId) throws IOException, DocumentException {
        Note note = noteService.getNoteById(username, noteId);
        if (note != null) {
            byte[] pdfBytes = generatePdfFromNote(note);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.builder("attachment").filename("note.pdf").build());
            headers.setContentLength(pdfBytes.length);
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}