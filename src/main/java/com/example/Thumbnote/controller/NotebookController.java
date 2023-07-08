package com.example.Thumbnote.controller;

import com.example.Thumbnote.objects.Note;
import com.example.Thumbnote.objects.Notebook;
import com.example.Thumbnote.service.AccountService;
import com.example.Thumbnote.service.AccountService;
import com.example.Thumbnote.service.NoteService;
import com.example.Thumbnote.service.NotebookService;
import com.example.Thumbnote.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/notebooks")
public class NotebookController {

    private final NotebookService notebookService;
    private final NoteService noteService;
    private final AccountService accService;
    private final JwtUtil jwtUtil;

    @Autowired
    public NotebookController(NotebookService notebookService, NoteService noteService, AccountService accService, JwtUtil jwtUtil) {
        this.notebookService = notebookService;
        this.noteService = noteService;
        this.accService = accService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/{notebookId}/notelist")
    public ResponseEntity<List<Note>> getAllNotesInNotebook(@RequestHeader("Authorization") String authHeader, @PathVariable long notebookId) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.getUsernameFromToken(token);
        long userId = accService.getUserID(username);

        Notebook notebook = notebookService.getById(notebookId);
        if (notebook == null || notebook.getUserId() != userId) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        List<Note> notes = noteService.getAllNotebookNotes(userId,notebookId);
        return ResponseEntity.ok(notes);
    }

    @PostMapping("/{notebookId}/notelist/add")
    public ResponseEntity<Void> addNoteToNotebook(@RequestHeader("Authorization") String authHeader, @PathVariable long notebookId, @RequestParam long noteId) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.getUsernameFromToken(token);
        long userId = accService.getUserID(username);
        Note note = noteService.getNoteById(username,noteId);
        if(note==null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        if (note.getUserId() == userId && noteService.addNoteToNotebook(note, notebookId)) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{notebookId}/notelist/{noteId}")
    public ResponseEntity<Void> removeNoteFromNotebook(@RequestHeader("Authorization") String authHeader, @PathVariable long notebookId, @PathVariable long noteId) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.getUsernameFromToken(token);
        long userId = accService.getUserID(username);

        Note note = noteService.getNoteById(username,noteId);
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
    public ResponseEntity<List<Notebook>> getAllNotebooks(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.getUsernameFromToken(token);
        long userId = accService.getUserID(username);
        List<Notebook> notebooks = notebookService.getAllNotebooks(userId);
        for (Notebook nb : notebooks) {
            nb.setNotes(noteService.getAllNotebookNotes(userId, nb.getNotebookId()));
        }

        return ResponseEntity.ok(notebooks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notebook> getNotebookById(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.getUsernameFromToken(token);
        long userId = accService.getUserID(username);
        Notebook notebook = notebookService.getById(id);
        List<Note> notes = noteService.getAllNotes(username);

        if (notebook != null && notebook.getUserId() == userId) {
            notebook.setNotes(notes);
            return ResponseEntity.ok(notebook);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Void> createNotebook(@RequestHeader("Authorization") String authHeader, @RequestBody Notebook notebook) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.getUsernameFromToken(token);
        long userId = accService.getUserID(username);
        notebook.setUserId(userId);

        if (!notebookService.doesExist(userId, notebook.getNotebookName()) && notebookService.addNotebook(notebook)) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateNotebook(@RequestHeader("Authorization") String authHeader, @PathVariable Long id, @RequestBody Notebook notebook) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.getUsernameFromToken(token);
        long userId = accService.getUserID(username);
        notebook.setNotebookId(id);
        notebook.setUserId(userId);

        if (notebook.getUserId() == userId && notebookService.updateNotebook(notebook)) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotebookById(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.getUsernameFromToken(token);
        long userId = accService.getUserID(username);

        if (notebookService.getById(id) != null && notebookService.getById(id).getUserId() == userId && notebookService.deleteNotebook(id)) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}