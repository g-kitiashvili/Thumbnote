package com.example.Thumbnote.service;

import com.example.Thumbnote.dao.NotebookDAO;
import com.example.Thumbnote.objects.Notebook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotebookService {

    private final NotebookDAO notebookDao;

    @Autowired
    public NotebookService(NotebookDAO notebookDao) {
        this.notebookDao = notebookDao;
    }

    public boolean addNotebook(Notebook notebook) {
        return notebookDao.addNotebook(notebook);
    }

    public boolean deleteNotebook(long notebookId) {
        return notebookDao.deleteNotebook(notebookId);
    }

    public boolean updateNotebook(Notebook notebook) {
        return notebookDao.updateNotebook(notebook);
    }

    public List<Notebook> getAllNotebooks(long userId) {
        return notebookDao.getAllNotebooks(userId);
    }

    public boolean doesExist(long userId, String notebookName) {
        return notebookDao.doesExist(userId, notebookName);
    }

    public Notebook getById(long notebookId) {
        return notebookDao.getById(notebookId);
    }


}