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
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO notes (user_id, last_access_date, note_name, note) VALUES (?,  ?, ?, ?)");
            stmt.setLong(1, note.getUserId());

            stmt.setTimestamp(2, (Timestamp) note.getLastAccessDate());
            stmt.setString(3, note.getNoteName());
            stmt.setString(4, note.getNoteText());
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
                String noteName = rs.getString("note_name");
                String noteText = rs.getString("note");
                Date uploadDate=rs.getDate("upload_date");

                List<String> tags = getTagsForNoteId(noteId);

                note = new Note(noteId, userId,uploadDate, noteName, noteText,tags);
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
                String noteName = rs.getString("note_name");
                String noteText = rs.getString("note");
                Date uploadDate = rs.getDate("upload_date");
                List<String> tags = getTagsForNoteId(id);

                Note note = new Note(id, userId,uploadDate, noteName, noteText,tags);
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
            PreparedStatement stmt = conn.prepareStatement("UPDATE notes SET user_id = ?, last_access_date = ?, note_name = ?, note = ? WHERE note_id = ?");
            stmt.setLong(1, note.getUserId());
            stmt.setTimestamp(2,new Timestamp(System.currentTimeMillis()));
            stmt.setString(3, note.getNoteName());
            stmt.setString(4, note.getNoteText());
            stmt.setLong(5, note.getNoteId());
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

    public List<String> getTagsForNoteId(long noteId) throws SQLException {
        List<String> tags = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()){
            PreparedStatement stmt = conn.prepareStatement("SELECT tag_name FROM tags WHERE note_id = ?");
            stmt.setLong(1, noteId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String tagName = rs.getString("tag_name");
                tags.add(tagName);
            }

            rs.close();
            stmt.close();
        }

        return tags;
    }

    public Note updateNoteTags(Long noteId, List<String> tags) {

            try (Connection conn = dataSource.getConnection()) {
                // Update the tags for the note in the tags table
                PreparedStatement tagStmt = conn.prepareStatement("DELETE FROM tags WHERE note_id = ?");
                tagStmt.setLong(1, noteId);
                tagStmt.executeUpdate();

                for (String tag : tags) {
                    PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO tags (note_id, tag_name) VALUES (?, ?)");
                    insertStmt.setLong(1, noteId);
                    insertStmt.setString(2, tag);
                    insertStmt.executeUpdate();
                    insertStmt.close();
                }

                // Retrieve the updated note object from the notes table
                PreparedStatement noteStmt = conn.prepareStatement("SELECT * FROM notes WHERE note_id = ?");
                noteStmt.setLong(1, noteId);
                ResultSet rs = noteStmt.executeQuery();

                if (rs.next()) {
                    long userId = rs.getLong("user_id");
                    String noteName = rs.getString("note_name");
                    String noteText = rs.getString("note");
                    Date uploadDate = rs.getTimestamp("upload_date");

                    List<String> newTags = getTagsForNoteId(noteId);

                    Note note = new Note(noteId, userId, uploadDate, noteName, noteText, newTags);

                    // Update the note object in the notes table
                    PreparedStatement updateStmt = conn.prepareStatement("UPDATE notes SET user_id = ?, last_access_date = ?, note_name = ?, note = ? WHERE note_id = ?");
                    updateStmt.setLong(1, note.getUserId());
                    updateStmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                    updateStmt.setString(3, note.getNoteName());
                    updateStmt.setString(4, note.getNoteText());
                    updateStmt.setLong(5, note.getNoteId());
                    updateStmt.executeUpdate();
                    updateStmt.close();

                    rs.close();
                    noteStmt.close();
                    tagStmt.close();

                    return note;
                }

                rs.close();
                noteStmt.close();
                tagStmt.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }

}