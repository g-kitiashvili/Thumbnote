package com.example.Thumbnote.service;

import com.example.Thumbnote.objects.Note;
import com.itextpdf.text.DocumentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PdfServiceTest {
    @Mock
    private NoteService noteService;

    @InjectMocks
    private PdfService pdfService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }



    @Test
    public void testGeneratePdfFromNote() throws IOException, DocumentException {
        Note note = new Note(1L, 1L, 1L, new Date(), "title", "text", null, null);
        byte[] pdfBytes = pdfService.generatePdfFromNote(note);
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);
    }
}