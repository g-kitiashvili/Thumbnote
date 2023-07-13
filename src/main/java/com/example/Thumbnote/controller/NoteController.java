package com.example.Thumbnote.controller;

import com.example.Thumbnote.objects.Note;
import com.example.Thumbnote.service.AuthService;
import com.example.Thumbnote.service.NoteService;

import com.example.Thumbnote.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.UUID;

import java.sql.SQLException;
import java.util.List;


@RestController
@RequestMapping("api/notes")
public class NoteController {

    private final NoteService noteService;
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @Autowired
    private Environment env;

    @Autowired
    public NoteController(NoteService noteService, AuthService authService, JwtUtil jwtUtil) {
        this.noteService = noteService;
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/allnotes")
    public ResponseEntity<List<Note>> getAllNotes(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = authService.getUsernameFromToken(token);
        if (jwtUtil.validateToken(token, username)) {
            List<Note> notes = noteService.getAllNotes(username);
            return ResponseEntity.ok(notes);
        } else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }



    @PostMapping("/{id}/picture")
    public ResponseEntity<Void> uploadNotePicture(@RequestHeader("Authorization") String authHeader, @PathVariable Long id,
                                                  @RequestParam("file") MultipartFile file) throws IOException, SQLException {
        String token = authHeader.replace("Bearer ", "");
        String username = authService.getUsernameFromToken(token);
        if (jwtUtil.validateToken(token, username)) {
            Note note = noteService.getNoteById(username, id);
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

            noteService.attachPicture(username, id, picturePath);

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/{id}/picture")
    public ResponseEntity<?> getNotePicture(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) throws IOException, SQLException {
        String token = authHeader.replace("Bearer ", "");
        String username = authService.getUsernameFromToken(token);
        if (jwtUtil.validateToken(token, username)) {
            Note note = noteService.getNoteById(username, id);
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
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @GetMapping("/{id}/picture/download")
    public ResponseEntity<byte[]> downloadNotePicture(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) throws IOException {
        String token = authHeader.replace("Bearer ", "");
        String username = authService.getUsernameFromToken(token);
        if (jwtUtil.validateToken(token, username)) {
            Note note = noteService.getNoteById(username, id);
            if (note == null) {
                return ResponseEntity.notFound().build();
            }

            String picturePath = note.getPicturePath();
            if (picturePath == null) {
                return ResponseEntity.notFound().build();
            }

            Path filePath = Paths.get(picturePath);
            try {
                byte[] picture = Files.readAllBytes(filePath);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.IMAGE_JPEG);
                headers.setContentLength(picture.length);

                return new ResponseEntity<>(picture, headers, HttpStatus.OK);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Note> getNoteById(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {
        String token = authHeader.replace("Bearer ", "");
        String username = authService.getUsernameFromToken(token);
        if (jwtUtil.validateToken(token, username)) {
            Note note = noteService.getNoteById(username, id);

            if (note != null) {
                return ResponseEntity.ok(note);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/addnote")
    public ResponseEntity<Void> createNote(@RequestHeader("Authorization") String authHeader, @RequestBody Note note) {
        String token = authHeader.replace("Bearer ", "");
        String username = authService.getUsernameFromToken(token);
        if (jwtUtil.validateToken(token, username)) {

            boolean success = noteService.createNote(username, note);

            if (success) {
                return ResponseEntity.status(HttpStatus.CREATED).build();
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateNote(@RequestHeader("Authorization") String authHeader, @PathVariable Long id, @RequestBody Note note) throws SQLException {
        String token = authHeader.replace("Bearer ", "");
        String username = authService.getUsernameFromToken(token);
        if (jwtUtil.validateToken(token, username)) {

            boolean success = noteService.updateNote(username, id, note);

            if (success) {
                return ResponseEntity.status(HttpStatus.OK).build();
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNoteById(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {
        String token = authHeader.replace("Bearer ", "");
        String username = authService.getUsernameFromToken(token);
        if (jwtUtil.validateToken(token, username)) {

            boolean success = noteService.deleteNoteById(username, id);

            if (success) {
                return ResponseEntity.status(HttpStatus.OK).build();
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}