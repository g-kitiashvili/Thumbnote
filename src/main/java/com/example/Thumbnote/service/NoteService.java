package com.example.Thumbnote.service;

import com.example.Thumbnote.dao.AccDAO;
import com.example.Thumbnote.dao.NoteDAO;
import com.example.Thumbnote.dao.TagDAO;
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

    public List<Note> getAllNotes(String username) {
        long userId = accDAO.getUserID(username);
        return noteDAO.getAllNotes(userId);
    }



    public boolean attachPicture(String username, Long id, String picturePath) {
        long userId = accDAO.getUserID(username);
        Note note = noteDAO.getById(userId, id);
        if (note == null) {
            return false;
        }
        note.setPicturePath(picturePath);
        return noteDAO.attachPictureToNote(note,picturePath);
    }

    public Note getNoteById(String username, Long id) {
        long userId = accDAO.getUserID(username);
        Note note = noteDAO.getById(userId,id);

        if (note != null) {
            note.setLastAccessDate(new Timestamp(System.currentTimeMillis()));
        }

        return note;
    }

    public boolean createNote(String username, Note note) {
        long userId = accDAO.getUserID(username);
        note.setUserId(userId);
        return noteDAO.AddNote(note);
    }

    public boolean updateNote(String username, Long id, Note newNote) throws SQLException {
        long userId = accDAO.getUserID(username);
        Note note =noteDAO.getById(userId,id);
        if(note==null) return false;
        note.setNoteName(newNote.getNoteName());
        note.setNoteText(newNote.getNoteText());
        return noteDAO.updateNote(note);
    }

    public boolean deleteNoteById(String username, Long id) {
        long userId = accDAO.getUserID(username);
        return noteDAO.deleteNote(noteDAO.getById(userId, id), userId);
    }

    public List<Note> getAllNotebookNotes(long userId, long notebookId) {
        return noteDAO.getAllNotebookNotes(userId,notebookId);
    }

    public boolean deleteNoteFromNotebook(Note note, long notebookId) {
        return noteDAO.deleteNoteFromNotebook(note,notebookId);
    }

    public boolean addNoteToNotebook(Note note, long notebookId) {
        return noteDAO.addNoteToNotebook(note,notebookId);
    }

    public List<Note> searchNotes(String name, List<String> tags, String sortBy, String sortOrder, long userId) {
        return noteDAO.searchNotes(name,tags,sortBy,sortOrder,userId);
    }
}