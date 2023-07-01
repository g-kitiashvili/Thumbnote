package com.example.Thumbnote.controller;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.example.Thumbnote.dao.AccDAO;
import com.example.Thumbnote.dao.NoteDAO;
import com.example.Thumbnote.objects.Acc;
import com.example.Thumbnote.objects.Note;
import com.example.Thumbnote.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/notes")
public class NoteController {
    private final NoteDAO noteDao;
    private final AccDAO userDAO;
    private final JwtUtil jwtUtil;

    @Autowired
    public NoteController(NoteDAO noteDAO, AccDAO userDAO, JwtUtil jwtUtil) {
        this.noteDao = noteDAO;
        this.userDAO = userDAO;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/allnotes")
    public ResponseEntity<List<Note>> getAllNotes(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.getUsernameFromToken(token);
        long userId = userDAO.getUserID(username);
        List<Note> notes = noteDao.getAllNotes(userId);


        return ResponseEntity.ok(notes);
    }
    @PostMapping("/{id}/addtag")
        public ResponseEntity<?> updateNoteTags(@PathVariable Long id, @RequestBody List<String> tags) {
            Note updatedNote = noteDao.updateNoteTags(id, tags);
            return ResponseEntity.ok(updatedNote);
    }

    @GetMapping("/{id}/tags")
    public List<String> getNoteTags(@PathVariable Long id) throws SQLException {
        List<String> tags = noteDao.getTagsForNoteId(id);

        return tags;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Note> getNoteById(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.getUsernameFromToken(token);
        long userId = userDAO.getUserID(username);
        Note note = noteDao.getById(id);

        if (note != null) {
            note.setLastAccessDate(new Timestamp( System.currentTimeMillis()));
            return ResponseEntity.ok(note);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/addnote")
    public ResponseEntity<Void> createNote(@RequestHeader("Authorization") String authHeader, @RequestBody Note note) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.getUsernameFromToken(token);
        long userId = userDAO.getUserID(username);
        if (noteDao.AddNote(note)) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateNote(@RequestHeader("Authorization") String authHeader, @PathVariable Long id, @RequestBody Note note) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.getUsernameFromToken(token);
        long userId = userDAO.getUserID(username);
        if (noteDao.updateNote(note)) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNoteById(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.getUsernameFromToken(token);
        long userId = userDAO.getUserID(username);
        if (noteDao.deleteNote(noteDao.getById(id))) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}