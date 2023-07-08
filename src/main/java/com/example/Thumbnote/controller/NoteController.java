package com.example.Thumbnote.controller;

import com.example.Thumbnote.objects.Note;
import com.example.Thumbnote.service.AuthService;
import com.example.Thumbnote.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("api/notes")
public class NoteController {

    private final NoteService noteService;
    private final AuthService authService;

    @Autowired
    public NoteController(NoteService noteService, AuthService authService) {
        this.noteService = noteService;
        this.authService = authService;
    }

    @GetMapping("/allnotes")
    public ResponseEntity<List<Note>> getAllNotes(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = authService.getUsernameFromToken(token);
        List<Note> notes = noteService.getAllNotes(username);
        return ResponseEntity.ok(notes);
    }

    @PostMapping("/{id}/addtag")
    public ResponseEntity<?> updateNoteTags(@RequestHeader("Authorization") String authHeader,@PathVariable Long id, @RequestBody List<String> tags) {
        String token = authHeader.replace("Bearer ", "");
        String username = authService.getUsernameFromToken(token);
        Note updatedNote = noteService.updateNoteTags(id, username,tags);
        return ResponseEntity.ok(updatedNote);
    }

    @GetMapping("/{id}/tags")
    public List<String> getNoteTags(@RequestHeader("Authorization") String authHeader,@PathVariable Long id) throws SQLException {
        String token = authHeader.replace("Bearer ", "");
        String username = authService.getUsernameFromToken(token);

        return noteService.getNoteTags(username,id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Note> getNoteById(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {
        String token = authHeader.replace("Bearer ", "");
        String username = authService.getUsernameFromToken(token);
        Note note = noteService.getNoteById(username, id);

        if (note != null) {
            return ResponseEntity.ok(note);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/addnote")
    public ResponseEntity<Void> createNote(@RequestHeader("Authorization") String authHeader, @RequestBody Note note) {
        String token = authHeader.replace("Bearer ", "");
        String username = authService.getUsernameFromToken(token);
        boolean success = noteService.createNote(username, note);

        if (success) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateNote(@RequestHeader("Authorization") String authHeader, @PathVariable Long id, @RequestBody Note note) throws SQLException {
        String token = authHeader.replace("Bearer ", "");
        String username = authService.getUsernameFromToken(token);
        boolean success = noteService.updateNote(username, id, note);

        if (success) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNoteById(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {
        String token = authHeader.replace("Bearer ", "");
        String username = authService.getUsernameFromToken(token);
        boolean success = noteService.deleteNoteById(username, id);

        if (success) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}