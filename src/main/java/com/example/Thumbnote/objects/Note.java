package com.example.Thumbnote.objects;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public class Note {
    private long noteId;
    private long userId;
    private Date uploadDate;
    private Date lastUpdateDate;
    private Date lastAccessDate;
    private String noteName;
    private String noteText;
    private List<String> tags;


    public Note(long noteId, long userId, String noteName, String noteText) {
        this.noteId = noteId;
        this.userId = userId;
        this.uploadDate =new Timestamp( System.currentTimeMillis());  ;
        this.lastUpdateDate = null;
        this.lastAccessDate = new Timestamp( System.currentTimeMillis());
        this.noteName = noteName;
        this.noteText = noteText;
        this.tags = tags;
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

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
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
}