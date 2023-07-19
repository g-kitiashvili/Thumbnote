package com.example.Thumbnote.service;

import com.example.Thumbnote.dao.NotebookDAO;
import com.example.Thumbnote.objects.Notebook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class NotebookServiceTest {

    @Mock
    private NotebookDAO notebookDAO;

    private NotebookService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new NotebookService(notebookDAO);
    }

    @Test
    void addNotebook_withNullParameter_returnsFalse() {
        boolean result = service.addNotebook(null);
        assertEquals(false, result);
    }

    @Test
    void addNotebook_withEmptyNotebookName_returnsFalse() {
        Notebook notebook = new Notebook(1L, "", "This is a test notebook.", 1L);
        boolean result = service.addNotebook(notebook);
        assertEquals(false, result);
    }

    @Test
    void deleteNotebook_withInvalidId_returnsFalse() {
        long notebookId = -1L;
        boolean result = service.deleteNotebook(notebookId);
        assertEquals(false, result);
    }

    @Test
    void updateNotebook_withNullParameter_returnsFalse() {
        boolean result = service.updateNotebook(null);
        assertEquals(false, result);
    }

    @Test
    void addNotebook_returnsTrue() {
        Notebook notebook = new Notebook(1L, "Test Notebook", "This is a test notebook.", 1L);

        when(notebookDAO.addNotebook(notebook)).thenReturn(true);

        boolean result = service.addNotebook(notebook);

        assertEquals(true, result);
    }

    @Test
    void getAllNotebooks_withInvalidUserId_returnsEmptyList() {
        long userId = -1L;
        List<Notebook> result = service.getAllNotebooks(userId);
        assertEquals(0, result.size());
    }

    @Test
    void deleteNotebook_returnsTrue() {
        long notebookId = 1L;

        when(notebookDAO.deleteNotebook(notebookId)).thenReturn(true);

        boolean result = service.deleteNotebook(notebookId);

        assertEquals(true, result);
    }

    @Test
    void doesExist_withEmptyNotebookName_returnsFalse() {
        long userId = 1L;
        String notebookName = "";
        boolean result = service.doesExist(userId, notebookName);
        assertEquals(false, result);
    }

    @Test
    void getById_withInvalidId_returnsNull() {
        long notebookId = -1L;
        Notebook result = service.getById(notebookId);
        assertEquals(null, result);
    }

    @Test
    void updateNotebook_returnsTrue() {
        Notebook notebook = new Notebook(1L, "Test Notebook", "This is a test notebook.", 1L);

        when(notebookDAO.updateNotebook(notebook)).thenReturn(true);

        boolean result = service.updateNotebook(notebook);

        assertEquals(true, result);
    }

    @Test
    void getAllNotebooks_returnsListOfNotebooks() {
        long userId = 1L;

        when(notebookDAO.getAllNotebooks(userId)).thenReturn(List.of(new Notebook(1L, "Notebook 1", "This is notebook 1.", userId), new Notebook(2L, "Notebook 2", "This is notebook 2.", userId)));

        List<Notebook> result = service.getAllNotebooks(userId);

        assertEquals(2, result.size());
    }

    @Test
    void doesExist_returnsTrue() {
        long userId = 1L;
        String notebookName = "Test Notebook";

        when(notebookDAO.doesExist(userId, notebookName)).thenReturn(true);

        boolean result = service.doesExist(userId, notebookName);

        assertEquals(true, result);
    }

    @Test
    void getById_returnsNotebook() {
        long notebookId = 1L;

        when(notebookDAO.getById(notebookId)).thenReturn(new Notebook(notebookId, "Test Notebook", "This is a test notebook.", 1L));

        Notebook result = service.getById(notebookId);

        assertEquals(notebookId, result.getNotebookId());
    }
}