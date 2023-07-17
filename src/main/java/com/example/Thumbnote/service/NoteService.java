package com.example.Thumbnote.service;

import com.example.Thumbnote.dao.AccDAO;
import com.example.Thumbnote.dao.NoteDAO;
import com.example.Thumbnote.objects.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Service
public class NoteService {

    private final NoteDAO noteDAO;
    private final AccDAO accDAO;

    @Autowired
    public NoteService(NoteDAO noteDAO, AccDAO accDAO) {
        this.noteDAO = noteDAO;
        this.accDAO = accDAO;
    }

    public List<Note> getAllNotes(long userId) {
        return noteDAO.getAllNotes(userId);
    }


    public boolean attachPicture(long userId, Long id, String picturePath) {
        Note note = noteDAO.getById(userId, id);
        if (note == null) {
            return false;
        }
        note.setPicturePath(picturePath);
        return noteDAO.attachPictureToNote(note, picturePath);
    }

    public Note getNoteById(long userId, Long id) {
        Note note = noteDAO.getById(userId, id);

        if (note != null) {
            note.setLastAccessDate(new Timestamp(System.currentTimeMillis()));
        }

        return note;
    }

    public boolean createNote( Note note) {
        return noteDAO.AddNote(note);
    }

    public boolean updateNote(long userId, Long id, Note newNote) throws SQLException {
        Note note = noteDAO.getById(userId, id);
        if (note == null) return false;
        note.setNoteName(newNote.getNoteName());
        note.setNoteText(newNote.getNoteText());
        return noteDAO.updateNote(note);
    }

    public boolean deleteNoteById(long userId, Long id) {
        return noteDAO.deleteNote(noteDAO.getById(userId, id), userId);
    }

    public List<Note> getAllNotebookNotes(long userId, long notebookId) {
        return noteDAO.getAllNotebookNotes(userId, notebookId);
    }

    public boolean deleteNoteFromNotebook(Note note, long notebookId) {
        return noteDAO.deleteNoteFromNotebook(note, notebookId);
    }

    public boolean addNoteToNotebook(Note note, long notebookId) {
        return noteDAO.addNoteToNotebook(note, notebookId);
    }

    public List<Note> searchNotes(String name, List<String> tags, String sortBy, String sortOrder, long userId) {
        return noteDAO.searchNotes(name, tags, sortBy, sortOrder, userId);
    }
}