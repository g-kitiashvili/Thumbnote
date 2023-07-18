package com.example.Thumbnote.service;

import com.example.Thumbnote.objects.Note;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import org.springframework.beans.factory.annotation.Autowired;
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

    public Note createNoteFromPdf(long userId, MultipartFile pdfFile) {
        if (pdfFile == null || pdfFile.getOriginalFilename() == null) {
            return null;
        }
        if (!pdfFile.getOriginalFilename().toLowerCase().endsWith(".pdf")) {
            return null;
        }
        try (PDDocument document = PDDocument.load(pdfFile.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String pdfText = stripper.getText(document);
            Note note = new Note(0, 0, 0, new Date(), pdfFile.getOriginalFilename(), pdfText, null, null);
            note.setUserId(userId);

            boolean success = noteService.createNote(note);
            if (success) {
                return note;
            } else {
                return null;
            }
        } catch (IOException e) {
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

    public byte[] generatePdfBytesFromNote(Long userId, Long noteId) throws IOException, DocumentException {
        Note note = noteService.getNoteById(userId, noteId);

        if (note != null) {
            return generatePdfFromNote(note);
        } else {
            return null;
        }
    }
}