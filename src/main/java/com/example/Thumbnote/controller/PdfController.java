package com.example.Thumbnote.controller;

import com.example.Thumbnote.objects.Note;
import com.example.Thumbnote.service.AuthService;
import com.example.Thumbnote.service.NoteService;
import com.example.Thumbnote.service.PdfService;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    private final NoteService noteService;
    private final AuthService authService;
    private final PdfService pdfService;

    @Autowired
    public PdfController(NoteService noteService, AuthService authService, PdfService pdfService) {
        this.noteService = noteService;
        this.authService = authService;
        this.pdfService = pdfService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Void> uploadNote(@RequestHeader("Authorization") String authHeader,
                                           @RequestParam("file") MultipartFile file,
                                           @RequestParam("title") String title) {
        String token = authHeader.replace("Bearer ", "");
        String username = authService.getUsernameFromToken(token);
        Note note = pdfService.createNoteFromPdf(username, file);
        noteService.createNote(username, note);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadNoteAsPdf(@RequestHeader("Authorization") String authHeader,
                                                    @PathVariable Long id) throws IOException, DocumentException {
        String token = authHeader.replace("Bearer ", "");
        String username = authService.getUsernameFromToken(token);
        Note note = noteService.getNoteById(username, id);

        if (note != null) {
            byte[] pdfBytes = pdfService.generatePdfFromNote(note);

            // Send the PDF file to the client
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