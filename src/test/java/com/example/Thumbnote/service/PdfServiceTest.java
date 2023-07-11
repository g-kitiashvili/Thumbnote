package com.example.Thumbnote.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.example.Thumbnote.objects.Note;
import com.itextpdf.text.DocumentException;

import java.io.IOException;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class PdfServiceTest {

    @Mock
    private NoteService noteService;

    @InjectMocks
    private PdfService pdfService;



    @Test
    public void testGeneratePdfFromNote() throws IOException, DocumentException {
        Note note = new Note(1L, 1L, 1L, new Date(), "title", "text", null, null);
        byte[] pdfBytes = pdfService.generatePdfFromNote(note);
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    public void testDownloadNoteAsPdf() throws IOException, DocumentException {
        Note note = new Note(1L, 1L, 1L, new Date(), "title", "text", null, null);
        when(noteService.getNoteById(eq("user"), eq(1L))).thenReturn(note);
        ResponseEntity<byte[]> response = pdfService.downloadNoteAsPdf("user", 1L);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertNotNull(response.getBody());
        assertEquals(response.getHeaders().getContentType(), MediaType.APPLICATION_PDF);
        assertEquals(response.getHeaders().getContentDisposition().getFilename(), "note.pdf");
        assertEquals(response.getHeaders().getContentLength(), pdfService.generatePdfFromNote(note).length);
    }
}