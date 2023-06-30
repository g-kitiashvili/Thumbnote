package com.example.Thumbnote.dao;

import com.example.Thumbnote.objects.Note;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

public class   NoteDAO {
    private final DataSource dataSource;

    public NoteDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean doesExist(long userId, String note_name){
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM notes WHERE user_id = ? AND note_name = ?");
            stmt.setLong(1, userId);
            stmt.setString(2, note_name);

            ResultSet rs = stmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            rs.close();
            stmt.close();

            return count > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean AddNote(Note note) {
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO notes (user_id, upload_date, last_update, last_access_date, note_name, note) VALUES (?, ?, ?, ?, ?, ?)");
            stmt.setLong(1, note.getUserId());
            stmt.setTimestamp(2, (Timestamp) note.getUploadDate());
            stmt.setTimestamp(3, null);
            stmt.setTimestamp(4, (Timestamp) note.getLastAccessDate());
            stmt.setString(5, note.getNoteName());
            stmt.setString(6, note.getNoteText());
            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Note getById(long noteId) {
        Note note = null;
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM notes WHERE note_id = ?");
            stmt.setLong(1, noteId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                long userId = rs.getLong("user_id");
                Date uploadDate = rs.getTimestamp("upload_date");
                Date lastUpdateDate = rs.getTimestamp("last_update");
                Date lastAccessDate = rs.getTimestamp("last_access_date");
                String noteName = rs.getString("note_name");
                String noteText = rs.getString("note");
//                List<String> tags = getTagsForNoteId(noteId);

                note = new Note(noteId, userId, noteName, noteText);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return note;
    }

    public List<Note> getAllNotes(long userId) {
        List<Note> notes = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM notes WHERE user_id = ?");
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                long id = rs.getLong("note_id");
                Date uploadDate = rs.getTimestamp("upload_date");
                Date lastUpdateDate = rs.getTimestamp("last_update");
                Date lastAccessDate = rs.getTimestamp("last_access");
                String noteName = rs.getString("note_name");
                String noteText = rs.getString("note_text");
//                List<String> tags = getTagsForNoteId(id);

                Note note = new Note(id, userId, noteName, noteText);
                notes.add(note);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return notes;
    }

    public boolean updateNote(Note note) {
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE notes SET user_id = ?, upload_date = ?, last_update = ?, last_access_date = ?, note_name = ?, note = ? WHERE note_id = ?");
            stmt.setLong(1, note.getUserId());
            stmt.setTimestamp(2, (Timestamp) note.getUploadDate());
            stmt.setTimestamp(3, new Timestamp( System.currentTimeMillis()));
            stmt.setTimestamp(4, (Timestamp) note.getLastAccessDate());
            stmt.setString(5, note.getNoteName());
            stmt.setString(6, note.getNoteText());
            stmt.setLong(7, note.getNoteId());
            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            return rowsAffected>0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteNote(Note note) {
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM notes WHERE note_id = ?");
            stmt.setLong(1, note.getNoteId());
            int rowsAffected = stmt.executeUpdate();

            stmt.close();
            return rowsAffected>0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

//    private List<String> getTagsForNoteId(long noteId) throws SQLException {
//        List<String> tags = new ArrayList<>();
//        try (Connection conn = dataSource.getConnection()){
//            PreparedStatement stmt = conn.prepareStatement("SELECT tag_name FROM tags WHERE note_id = ?");
//            stmt.setLong(1, noteId);
//            ResultSet rs = stmt.executeQuery();
//
//            while (rs.next()) {
//                String tagName = rs.getString("tag_name");
//                tags.add(tagName);
//            }
//
//            rs.close();
//            stmt.close();
//        }
//
//        return tags;
//    }
}