package com.example.Thumbnote.controller;

import com.example.Thumbnote.annotation.Secure;
import com.example.Thumbnote.objects.Note;
import com.example.Thumbnote.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/notes")
@Secure
public class NoteController {

    private final NoteService noteService;

    @Autowired
    private Environment env;
    @Autowired
    public NoteController(NoteService noteService) {
        this.noteService = noteService;

    }

    @GetMapping("/")
    public ResponseEntity<List<Note>> getAllNotes(HttpServletRequest request) {
        long userId = (long) request.getAttribute("userId");
        List<Note> notes = noteService.getAllNotes(userId);
        return ResponseEntity.ok(notes);
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<Note> getNoteById(HttpServletRequest request, @PathVariable Long noteId) {
        long userId = (long) request.getAttribute("userId");
        Note note = noteService.getNoteById(userId, noteId);
        if (note == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            return ResponseEntity.ok(note);
        }
    }

    @PostMapping("/addnote")
    public ResponseEntity<Void> createNote(HttpServletRequest request, @RequestBody Note note) {
        long userId = (long) request.getAttribute("userId");
        note.setUserId(userId);
        if (noteService.createNote(note)) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{noteId}")
    public ResponseEntity<Void> updateNote(HttpServletRequest request, @PathVariable Long noteId, @RequestBody Note note) throws SQLException {
        long userId = (long) request.getAttribute("userId");
        note.setUserId(userId);
        note.setNoteId(noteId);
        if (noteService.updateNote(userId,noteId, note)) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{noteId}")
    public ResponseEntity<Void> deleteNoteById(HttpServletRequest request, @PathVariable Long noteId) {
        long userId = (long) request.getAttribute("userId");
        if (noteService.deleteNoteById(userId, noteId)) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    @GetMapping("/{id}/picture")
    public ResponseEntity<?> getNotePicture(HttpServletRequest request, @PathVariable Long id) throws IOException, SQLException {
        long userId = (long) request.getAttribute("userId");


        Note note = noteService.getNoteById(userId, id);
            if (note == null || note.getPicturePath() == null) {
                return ResponseEntity.notFound().build();
            }

            Path picturePath = Paths.get(note.getPicturePath());
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(picturePath));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentLength(resource.contentLength());
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setContentDisposition(ContentDisposition.inline().filename(picturePath.getFileName().toString()).build());

            return ResponseEntity.ok().headers(headers).body(resource);
        }
    @PostMapping("/{id}/picture")
    public ResponseEntity<Void> uploadNotePicture(HttpServletRequest request, @PathVariable Long id,
                                                  @RequestParam("file") MultipartFile file) throws IOException, SQLException {

        long userId = (long) request.getAttribute("userId");

        Note note = noteService.getNoteById(userId, id);
            if (note == null) {
                return ResponseEntity.notFound().build();
            }

            if (!file.getContentType().startsWith("image/")) {
                return ResponseEntity.badRequest().build();
            }

            String pictureName = UUID.randomUUID() + ".jpg";
            String picturePath = env.getProperty("thumbnails.path") + File.separator + pictureName;
            Path filePath = Paths.get(picturePath);
        try {
            Files.write(filePath, file.getBytes());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        noteService.attachPicture(userId, id, picturePath);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}