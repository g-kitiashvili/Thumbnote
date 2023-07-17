package com.example.Thumbnote.controller;

import com.example.Thumbnote.annotation.Secure;
import com.example.Thumbnote.objects.Note;
import com.example.Thumbnote.service.NoteService;
import com.example.Thumbnote.service.AccountService;
import com.example.Thumbnote.utils.JwtUtil;
import com.example.Thumbnote.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("api/notes")
@Secure
public class NoteController {

    private final NoteService noteService;
    private final AccountService accService;
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    @Autowired
    public NoteController(NoteService noteService, AccountService accService, JwtUtil jwtUtil, AuthService authService) {
        this.noteService = noteService;
        this.accService = accService;
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }

    @GetMapping("/")
    public ResponseEntity<List<Note>> getAllNotes(HttpServletRequest request) {
        long userId = (long) request.getAttribute("userID");
        List<Note> notes = noteService.getAllNotes(userId);
        return ResponseEntity.ok(notes);
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<Note> getNoteById(HttpServletRequest request, @PathVariable Long noteId) {
        long userId = (long) request.getAttribute("userID");
        Note note = noteService.getNoteById(userId, noteId);
        if (note == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            return ResponseEntity.ok(note);
        }
    }

    @PostMapping("")
    public ResponseEntity<Void> createNote(HttpServletRequest request, @RequestBody Note note) {
        long userId = (long) request.getAttribute("userID");
        note.setUserId(userId);
        if (noteService.createNote(note)) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{noteId}")
    public ResponseEntity<Void> updateNote(HttpServletRequest request, @PathVariable Long noteId, @RequestBody Note note) throws SQLException {
        long userId = (long) request.getAttribute("userID");
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
        long userId = (long) request.getAttribute("userID");
        if (noteService.deleteNoteById(userId, noteId)) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}