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

    public Note createNoteFromPdf(String username, MultipartFile file) {
        Note note = null;

        try {
            // Extract the text content from the PDF file
            PDDocument document = PDDocument.load(file.getInputStream());
            PDFTextStripper stripper = new PDFTextStripper();
            String content = stripper.getText(document);
            document.close();
            // Create a new note with the extracted content
            note = new Note(0, 0,0, new Date(),  file.getOriginalFilename(), content, null);
            noteService.createNote(username,note);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return note;
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