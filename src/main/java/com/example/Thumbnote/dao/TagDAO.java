package com.example.Thumbnote.dao;

import com.example.Thumbnote.objects.Note;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class TagDAO {
    private final DataSource dataSource;

    public TagDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }






    public List<String> getTagsForNoteId(long userId, Long noteId) throws SQLException {
        List<String> tags = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()){

            PreparedStatement stmt = conn.prepareStatement("SELECT tag_name FROM tags WHERE note_id = ? and user_id= ? ");
            stmt.setLong(1, noteId);
            stmt.setLong(2,userId);
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


    public Note updateNoteTags(Long noteId, long userId, List<String> tags) {
        try (Connection conn = dataSource.getConnection()) {
            // Check if the note with the given ID belongs to the user with the given ID
            PreparedStatement checkNoteStmt = conn.prepareStatement("SELECT * FROM notes WHERE note_id = ? AND user_id = ?");
            checkNoteStmt.setLong(1, noteId);
            checkNoteStmt.setLong(2, userId);
            ResultSet rs = checkNoteStmt.executeQuery();

            if (rs.next()) {
                // Delete all existing tags for the note
                PreparedStatement deleteTagsStmt = conn.prepareStatement("DELETE FROM tags WHERE note_id = ?");
                deleteTagsStmt.setLong(1, noteId);
                deleteTagsStmt.executeUpdate();

                // Insert new tags for the note
                for (String tag : tags) {
                    PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO tags (note_id, tag_name, user_id) VALUES (?,?,?)");
                    insertStmt.setLong(1, noteId);
                    insertStmt.setString(2, tag);
                    insertStmt.setLong(3, userId);
                    insertStmt.executeUpdate();
                    insertStmt.close();
                }

                // Retrieve the updated note object from the notes table
                PreparedStatement noteStmt = conn.prepareStatement("SELECT * FROM notes WHERE note_id = ?");
                noteStmt.setLong(1, noteId);
                ResultSet noteRs = noteStmt.executeQuery();

                if (noteRs.next()) {
                    long ownerId = noteRs.getLong("user_id");
                    String noteName = noteRs.getString("note_name");
                    String noteText = noteRs.getString("note");
                    Date uploadDate = noteRs.getTimestamp("upload_date");
                    long nbId = noteRs.getLong("notebook_id");

                    List<String> newTags = getTagsForNoteId(userId, noteId);
                    String picturePath=rs.getString("picturePath");

                    Note note = new Note(noteId, ownerId, nbId, uploadDate, noteName, noteText, newTags, picturePath);

                    noteRs.close();
                    noteStmt.close();
                    deleteTagsStmt.close();
                    checkNoteStmt.close();

                    return note;
                }

                noteRs.close();
                noteStmt.close();
            }

            rs.close();
            checkNoteStmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


}