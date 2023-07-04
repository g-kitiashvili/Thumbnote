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
    private final AccDAO userDAO;
    private final JwtUtil jwtUtil;

    @Autowired
    public NotebookController(NotebookDAO notebookDAO, NoteDAO noteDAO, AccDAO userDAO, JwtUtil jwtUtil) {
        this.notebookDao = notebookDAO;
        this.noteDao = noteDAO;
        this.userDAO = userDAO;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Notebook>> getAllNotebooks(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.getUsernameFromToken(token);
        long userId = userDAO.getUserID(username);
        List<Notebook> notebooks = notebookDao.getAllNotebooks(userId);

        return ResponseEntity.ok(notebooks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notebook> getNotebookById(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.getUsernameFromToken(token);
        long userId = userDAO.getUserID(username);
        Notebook notebook = notebookDao.getById(id);
        List<Note> notes = noteDao.getAllNotebookNotes(userId,notebook.getNotebookId());

        if (notebook != null && notebook.getUserId() == userId) {
            return ResponseEntity.ok(notebook);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Void> createNotebook(@RequestHeader("Authorization") String authHeader, @RequestBody Notebook notebook) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.getUsernameFromToken(token);
        long userId = userDAO.getUserID(username);

        if (notebookDao.addNotebook(notebook)) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateNotebook(@RequestHeader("Authorization") String authHeader, @PathVariable Long id, @RequestBody Notebook notebook) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.getUsernameFromToken(token);
        long userId = userDAO.getUserID(username);

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
        long userId = userDAO.getUserID(username);

        if (notebookDao.deleteNotebook(id)) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}