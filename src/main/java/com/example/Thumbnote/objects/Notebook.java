package com.example.Thumbnote.objects;

import java.util.List;

public class Notebook {
    private long notebookId;
    private String notebookName;
    private String description;
    private long userId;
    private List<Note> notes;

    public Notebook(long notebookId, String notebookName, String description, long userId) {
        this.notebookId = notebookId;
        this.notebookName = notebookName;
        this.description = description;
        this.userId = userId;
    }

    public long getNotebookId() {
        return notebookId;
    }

    public void setNotebookId(long notebookId) {
        this.notebookId = notebookId;
    }

    public String getNotebookName() {
        return notebookName;
    }

    public void setNotebookName(String notebookName) {
        this.notebookName = notebookName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }
}