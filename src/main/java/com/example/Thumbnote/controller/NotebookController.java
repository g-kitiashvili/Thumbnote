package com.example.Thumbnote.controller;

import com.example.Thumbnote.annotation.Secure;
import com.example.Thumbnote.objects.Note;
import com.example.Thumbnote.objects.Notebook;
import com.example.Thumbnote.service.*;
import com.example.Thumbnote.service.AccountService;
import com.example.Thumbnote.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/notebooks")
@Secure
public class NotebookController {

    private final NotebookService notebookService;
    private final NoteService noteService;
    private final AccountService accService;
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    @Autowired
    public NotebookController(NotebookService notebookService, NoteService noteService, AccountService accService, JwtUtil jwtUtil, AuthService authService) {
        this.notebookService = notebookService;
        this.noteService = noteService;
        this.accService = accService;
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }

    @GetMapping("/{notebookId}/notelist")
    public ResponseEntity<List<Note>> getAllNotesInNotebook(HttpServletRequest request, @PathVariable long notebookId) {
        long userId = (long) request.getAttribute("userID");

        Notebook notebook = notebookService.getById(notebookId);
        if (notebook == null || notebook.getUserId() != userId) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        List<Note> notes = noteService.getAllNotebookNotes(userId, notebookId);
        return ResponseEntity.ok(notes);
    }

    @PostMapping("/{notebookId}/notelist/add")
    public ResponseEntity<Void> addNoteToNotebook(HttpServletRequest request, @PathVariable long notebookId, @RequestParam long noteId) {
        long userId = (long) request.getAttribute("userID");
        Note note = noteService.getNoteById(userId, noteId);
        if (note == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (noteService.addNoteToNotebook(note, notebookId)) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{notebookId}/notelist/{noteId}")
    public ResponseEntity<Void> removeNoteFromNotebook(HttpServletRequest request, @PathVariable long notebookId, @PathVariable long noteId) {
        long userId = (long) request.getAttribute("userID");
        Note note = noteService.getNoteById(userId, noteId);
        if (note == null || note.getUserId() != userId) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (noteService.deleteNoteFromNotebook(note, notebookId)) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Notebook>> getAllNotebooks(HttpServletRequest request) {
        long userId = (long) request.getAttribute("userID");
        List<Notebook> notebooks = notebookService.getAllNotebooks(userId);
        for (Notebook nb : notebooks) {
            nb.setNotes(noteService.getAllNotebookNotes(userId, nb.getNotebookId()));
        }

        return ResponseEntity.ok(notebooks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notebook> getNotebookById(HttpServletRequest request, @PathVariable Long id) {
        long userId = (long) request.getAttribute("userID");
        Notebook notebook = notebookService.getById(id);
        List<Note> notes = noteService.getAllNotes(userId);

        if (notebook != null && notebook.getUserId() == userId) {
            notebook.setNotes(notes);
            return ResponseEntity.ok(notebook);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Void> createNotebook(HttpServletRequest request, @RequestBody Notebook notebook) {
        long userId = (long) request.getAttribute("userID");
        notebook.setUserId(userId);

        if (!notebookService.doesExist(userId, notebook.getNotebookName()) && notebookService.addNotebook(notebook)) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateNotebook(HttpServletRequest request, @PathVariable Long id, @RequestBody Notebook notebook) {
        long userId = (long) request.getAttribute("userID");
        notebook.setNotebookId(id);
        notebook.setUserId(userId);

        if (notebookService.updateNotebook(notebook)) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotebookById(HttpServletRequest request, @PathVariable Long id) {
        long userId = (long) request.getAttribute("userID");

        if (notebookService.getById(id) != null && notebookService.getById(id).getUserId() == userId && notebookService.deleteNotebook(id)) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}