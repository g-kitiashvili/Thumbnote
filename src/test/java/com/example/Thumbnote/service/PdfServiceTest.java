package com.example.Thumbnote.service;

import com.example.Thumbnote.objects.Note;
import com.itextpdf.text.DocumentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PdfServiceTest {

    @Mock
    private NoteService noteService;

    private PdfService pdfService;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        pdfService = new PdfService(noteService);
    }

    @Test
    public void testCreateNoteFromPdf() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenReturn(mock(InputStream.class));
        Note note = pdfService.createNoteFromPdf("user", file);
        assertEquals(note.getNoteName(), file.getOriginalFilename());
        assertEquals(note.getNoteText(), "sample pdf text");
        verify(noteService, times(1)).createNote(eq("user"), any(Note.class));
    }

    @Test
    public void testGeneratePdfFromNote() throws IOException, DocumentException {
        Note note = new Note(1L, 1L, 1L, new Date(), "title", "text", null);
        byte[] pdfBytes = pdfService.generatePdfFromNote(note);
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }

    @Test
    public void testDownloadNoteAsPdf() throws IOException, DocumentException {
        Note note = new Note(1L, 1L, 1L, new Date(), "title", "text", null);
        when(noteService.getNoteById(eq("user"), eq(1L))).thenReturn(note);
        ResponseEntity<byte[]> response = pdfService.downloadNoteAsPdf("user", 1L);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertNotNull(response.getBody());
        assertEquals(response.getHeaders().getContentType(), MediaType.APPLICATION_PDF);
        assertEquals(response.getHeaders().getContentDisposition().getFilename(), "note.pdf");
        assertEquals(response.getHeaders().getContentLength(), pdfService.generatePdfFromNote(note).length);
    }
}