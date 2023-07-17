package com.example.Thumbnote.controller;

import com.example.Thumbnote.annotation.Secure;
import com.example.Thumbnote.objects.Note;
import com.example.Thumbnote.service.AuthService;
import com.example.Thumbnote.service.NoteService;
import com.example.Thumbnote.service.PdfService;
import com.example.Thumbnote.utils.JwtUtil;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/pdf")
@Secure
public class PdfController {

    private final NoteService noteService;
    private final AuthService authService;
    private final PdfService pdfService;
    private final JwtUtil jwtUtil;

    @Autowired
    public PdfController(NoteService noteService, AuthService authService, PdfService pdfService, JwtUtil jwtUtil) {
        this.noteService = noteService;
        this.authService = authService;
        this.pdfService = pdfService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/upload")
    public ResponseEntity<Void> uploadNote(
                                           @RequestParam("file") MultipartFile file) {
        Long userId = (Long) RequestContextHolder.currentRequestAttributes().getAttribute("userId", RequestAttributes.SCOPE_REQUEST);
        Note note = pdfService.createNoteFromPdf(userId, file);
        if (note != null) {
            noteService.createNote(note);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadNoteAsPdf(
                                                    @PathVariable Long id) throws IOException, DocumentException {
        Long userId = (Long) RequestContextHolder.currentRequestAttributes().getAttribute("userId", RequestAttributes.SCOPE_REQUEST);
        Note note = noteService.getNoteById(userId, id);

        if (note != null) {
            byte[] pdfBytes = pdfService.generatePdfFromNote(note);

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