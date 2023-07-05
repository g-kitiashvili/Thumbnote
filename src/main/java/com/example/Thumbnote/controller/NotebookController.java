package com.example.Thumbnote.controller;

import com.example.Thumbnote.dao.AccDAO;
import com.example.Thumbnote.dao.NoteDAO;
import com.example.Thumbnote.dao.NotebookDAO;
import com.example.Thumbnote.objects.Note;
import com.example.Thumbnote.objects.Notebook;
import com.example.Thumbnote.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/notebooks")
public class NotebookController {

    private final NotebookDAO notebookDao;
    private final NoteDAO noteDao;
    private final AccDAO accDao;
    private final JwtUtil jwtUtil;

    @Autowired
    public NotebookController(NotebookDAO notebookDao, NoteDAO noteDao, AccDAO accDao, JwtUtil jwtUtil) {
        this.notebookDao = notebookDao;
        this.noteDao = noteDao;
        this.accDao = accDao;
        this.jwtUtil = jwtUtil;
    }
    @GetMapping("/{notebookId}/notelist")
    public ResponseEntity<List<Note>> getAllNotesInNotebook(@RequestHeader("Authorization") String authHeader, @PathVariable long notebookId) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.getUsernameFromToken(token);
        long userId = accDao.getUserID(username);

        Notebook notebook = notebookDao.getById(notebookId);
        if (notebook == null || notebook.getUserId() != userId) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        List<Note> notes = noteDao.getAllNotebookNotes(userId,notebookId);
        return ResponseEntity.ok(notes);
    }    @PostMapping("/{notebookId}/notelist/add")
    public ResponseEntity<Void> addNoteToNotebook(@RequestHeader("Authorization") String authHeader, @PathVariable long notebookId, @RequestParam long noteId) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.getUsernameFromToken(token);
        long userId = accDao.getUserID(username);
        Note note = noteDao.getById(noteId);

        if (note.getUserId() == userId && noteDao.addNoteToNotebook(note, notebookId)) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{notebookId}/notelist/{noteId}")
    public ResponseEntity<Void> removeNoteFromNotebook(@RequestHeader("Authorization") String authHeader, @PathVariable long notebookId, @PathVariable long noteId) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.getUsernameFromToken(token);
        long userId = accDao.getUserID(username);

        Note note = noteDao.getById(noteId);
        if (note == null || note.getUserId() != userId) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (noteDao.deleteNoteFromNotebook(note, notebookId)) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    @GetMapping("/all")
    public ResponseEntity<List<Notebook>> getAllNotebooks(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.getUsernameFromToken(token);
        long userId = accDao.getUserID(username);
        List<Notebook> notebooks = notebookDao.getAllNotebooks(userId);

        return ResponseEntity.ok(notebooks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notebook> getNotebookById(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.getUsernameFromToken(token);
        long userId = accDao.getUserID(username);
        Notebook notebook = notebookDao.getById(id);
        List<Note> notes = noteDao.getAllNotes(id);

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
        long userId = accDao.getUserID(username);
        notebook.setUserId(userId);

        if (!notebookDao.doesExist(userId, notebook.getNotebookName()) && notebookDao.addNotebook(notebook)) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateNotebook(@RequestHeader("Authorization") String authHeader, @PathVariable Long id, @RequestBody Notebook notebook) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.getUsernameFromToken(token);
        long userId = accDao.getUserID(username);
        notebook.setNotebookId(id);
        notebook.setUserId(userId);

        if (notebook.getUserId() == userId && notebookDao.updateNotebook(notebook)) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotebookById(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.getUsernameFromToken(token);
        long userId = accDao.getUserID(username);

        if (notebookDao.getById(id) != null && notebookDao.getById(id).getUserId() == userId && notebookDao.deleteNotebook(id)) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}