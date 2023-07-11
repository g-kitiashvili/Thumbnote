package com.example.Thumbnote.objects;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Note {
    private long noteId;
    private long userId;
    private Date uploadDate;
    private Date lastAccessDate;
    private String noteName;
    private String noteText;
    private List<String> tags;
    private long notebookId;
    private String picturePath;



    public Note(long noteId, long userId, long notebookId, Date uploadDate, String noteName, String noteText, List<String> tags, String picturePath) {
        this.noteId = noteId;
        this.userId = userId;
        this.uploadDate=uploadDate;
        this.picturePath = picturePath;
        this.lastAccessDate = new Timestamp( System.currentTimeMillis());
        this.noteName = noteName;
        this.noteText = noteText;
        this.notebookId=notebookId;
        this.tags = tags;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Note note = (Note) obj;
        return noteId == note.noteId &&
                userId == note.userId &&
                notebookId == note.notebookId &&
                Objects.equals(uploadDate, note.uploadDate) &&
                Objects.equals(lastAccessDate, note.lastAccessDate) &&
                Objects.equals(noteName, note.noteName) &&
                Objects.equals(noteText, note.noteText) &&
                Objects.equals(tags, note.tags);
    }
    public long getNoteId() {
        return noteId;
    }

    public void setNoteId(long noteId) {
        this.noteId = noteId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public void setNotebookId(long nbId){
        notebookId=nbId;
    }
    public long getNotebookId(){
        return notebookId;
    }

    public Date getLastAccessDate() {
        return lastAccessDate;
    }

    public void setLastAccessDate(Date lastAccessDate) {
        this.lastAccessDate = lastAccessDate;
    }

    public String getNoteName() {
        return noteName;
    }

    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }


    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }
}