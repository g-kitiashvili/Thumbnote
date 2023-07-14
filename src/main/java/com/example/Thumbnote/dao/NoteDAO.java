package com.example.Thumbnote.dao;

import com.example.Thumbnote.objects.Note;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
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

    public long getNoteId(String noteName,long user_id){
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("select note_id from notes where note_name=? and user_id=?");
            stmt.setString(1, noteName);
            stmt.setLong(2, user_id);

            ResultSet rs = stmt.executeQuery();
            stmt.close();
            if(rs.next()){
                return rs.getLong(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
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
    public boolean addNoteToNotebook(Note note, long notebookId) {
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement notebookStmt = conn.prepareStatement("SELECT COUNT(*) FROM notebooks WHERE notebook_id = ? AND user_id = ?");
            notebookStmt.setLong(1, notebookId);
            notebookStmt.setLong(2, note.getUserId());
            ResultSet notebookRs = notebookStmt.executeQuery();
            notebookRs.next();
            int notebookCount = notebookRs.getInt(1);
            notebookRs.close();
            notebookStmt.close();

            if (notebookCount == 0) {
                return false;
            }

            PreparedStatement noteStmt = conn.prepareStatement("SELECT COUNT(*) FROM notes WHERE note_name = ? AND user_id = ? AND notebook_id = ?");
            noteStmt.setString(1, note.getNoteName());
            noteStmt.setLong(2, note.getUserId());
            noteStmt.setLong(3, notebookId);
            ResultSet noteRs = noteStmt.executeQuery();
            noteRs.next();
            int noteCount = noteRs.getInt(1);
            noteRs.close();
            noteStmt.close();

            if (noteCount > 0) {
                return false;
            }

            PreparedStatement addNoteStmt = conn.prepareStatement("UPDATE notes SET notebook_id = ?, last_access_date = ? WHERE user_id = ? AND note_name = ? AND notebook_id IS NULL");
            addNoteStmt.setLong(1, notebookId);
            addNoteStmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            addNoteStmt.setLong(3, note.getUserId());
            addNoteStmt.setString(4, note.getNoteName());
            int rowsAffected = addNoteStmt.executeUpdate();
            addNoteStmt.close();

            return rowsAffected == 1;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean attachPictureToNote(Note note, String picturePath) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            PreparedStatement selectStmt = conn.prepareStatement("SELECT picturePath FROM notes WHERE user_id = ? AND note_name = ?");
            selectStmt.setLong(1, note.getUserId());
            selectStmt.setString(2, note.getNoteName());
            ResultSet selectRs = selectStmt.executeQuery();
            String currentPicturePath = null;
            if (selectRs.next()) {
                currentPicturePath = selectRs.getString("picturePath");
            }
            selectRs.close();
            selectStmt.close();

            if (picturePath != null && picturePath!="" && !picturePath.equals(currentPicturePath)) {
                PreparedStatement updateStmt = conn.prepareStatement("UPDATE notes SET picturePath = ? WHERE user_id = ? AND note_name = ?");
                updateStmt.setString(1, picturePath);
                updateStmt.setLong(2, note.getUserId());
                updateStmt.setString(3, note.getNoteName());
                int rowsAffected = updateStmt.executeUpdate();
                updateStmt.close();
                conn.commit();
                return rowsAffected == 1;
            } else if ((picturePath == null || picturePath=="") && currentPicturePath != null) {
                PreparedStatement deleteStmt = conn.prepareStatement("UPDATE notes SET picturePath = NULL WHERE user_id = ? AND note_name = ?");
                deleteStmt.setLong(1, note.getUserId());
                deleteStmt.setString(2, note.getNoteName());
                int rowsAffected = deleteStmt.executeUpdate();
                deleteStmt.close();
                conn.commit();
                return rowsAffected == 1;
            } else {
                conn.rollback();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean deleteNoteFromNotebook(Note note, long notebookId) {
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement notebookStmt = conn.prepareStatement("SELECT COUNT(*) FROM notebooks WHERE notebook_id = ? AND user_id = ?");
            notebookStmt.setLong(1, notebookId);
            notebookStmt.setLong(2, note.getUserId());
            ResultSet notebookRs = notebookStmt.executeQuery();
            notebookRs.next();
            int notebookCount = notebookRs.getInt(1);
            notebookRs.close();
            notebookStmt.close();

            if (notebookCount == 0) {
                return false;
            }

            PreparedStatement deleteNoteStmt = conn.prepareStatement("UPDATE notes SET notebook_id = NULL WHERE user_id = ? AND note_name = ? AND notebook_id = ?");
            deleteNoteStmt.setLong(1, note.getUserId());
            deleteNoteStmt.setString(2, note.getNoteName());
            deleteNoteStmt.setLong(3, notebookId);
            int rowsAffected = deleteNoteStmt.executeUpdate();
            deleteNoteStmt.close();

            return rowsAffected == 1;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public Note getById(long userid, Long noteId) {
        Note note = null;
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM notes WHERE note_id = ? and user_id=?");
            stmt.setLong(1, noteId);
            stmt.setLong(2,userid);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                long userId = rs.getLong("user_id");
                String noteName = rs.getString("note_name");
                String noteText = rs.getString("note");
                Date uploadDate=rs.getDate("upload_date");
                long notebookId=rs.getLong("notebook_id");
                String picturePath=rs.getString("picturePath");

                List<String> tags = getTagsForNoteId(userId, noteId);

                note = new Note(noteId, userId,notebookId , uploadDate, noteName, noteText, tags, picturePath);
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
                List<String> tags = getTagsForNoteId(id, id);
                long nbId = rs.getLong("notebook_id");
                String picturePath=rs.getString("picturePath");

                Note note = new Note(id, userId, nbId, uploadDate, noteName, noteText, tags, picturePath);
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

    public boolean deleteNote(Note note,long userId) {
        try (Connection conn = dataSource.getConnection()) {
            if(note==null) return false;
            PreparedStatement stmt2 = conn.prepareStatement("delete from tags where note_id = ? and user_id=? ");
            stmt2.setLong(1, note.getNoteId());
            stmt2.setLong(2,userId);
            stmt2.executeUpdate();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM notes WHERE note_id = ? and user_id = ?");
            stmt.setLong(1, note.getNoteId());
            stmt.setLong(2,userId);
            int rowsAffected = stmt.executeUpdate();
            stmt.close();
            stmt2.close();
            return rowsAffected>0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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
    public List<Note> getAllNotebookNotes(long userId, long notebookId){
        List<Note> notes = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM notes WHERE user_id = ? and notebook_id=?");
            stmt.setLong(1, userId);
            stmt.setLong(2,notebookId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                long id = rs.getLong("note_id");
                String noteName = rs.getString("note_name");
                String noteText = rs.getString("note");
                Date uploadDate = rs.getDate("upload_date");
                List<String> tags = getTagsForNoteId(id, id);
                String picturePath=rs.getString("picturePath");

                Note note = new Note(id, userId,notebookId , uploadDate, noteName, noteText, tags, picturePath);
                notes.add(note);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notes;
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

    public List<Note> searchNotes(String name, List<String> tags, String sortBy, String sortOrder, long userId) {
        List<Note> notes = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT * FROM notes WHERE user_id = ? ";
            if (name != null) {
                sql += "AND note_name LIKE ? ";
            }
            if (tags != null && !tags.isEmpty()) {
                String tagQuery = String.join(",", Collections.nCopies(tags.size(), "?"));
                sql += "AND note_id IN (SELECT note_id FROM tags WHERE tag_name IN (" + tagQuery + ")) ";
            }
            sql += "ORDER BY " + sortBy + " " + sortOrder;

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, userId);
            int paramIndex = 2;
            if (name != null) {
                stmt.setString(paramIndex, "%" + name + "%");
                paramIndex++;
            }
            if (tags != null && !tags.isEmpty()) {
                for (int i = 0; i < tags.size(); i++) {
                    stmt.setString(paramIndex, tags.get(i));
                    paramIndex++;
                }
            }

            System.out.println("SQL query: " + stmt.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                long id = rs.getLong("note_id");
                String noteName = rs.getString("note_name");
                String noteText = rs.getString("note");
                Date uploadDate = rs.getTimestamp("upload_date");
                long nbId = rs.getLong("notebook_id");
                List<String> noteTags = getTagsForNoteId(id, id);
                String picturePath=rs.getString("picturePath");

                Note note = new Note(id, userId, nbId, uploadDate, noteName, noteText, noteTags, picturePath);
                notes.add(note);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notes;
    }
}