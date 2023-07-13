package com.example.Thumbnote.service;

import com.example.Thumbnote.dao.AccDAO;
import com.example.Thumbnote.dao.NoteDAO;
import com.example.Thumbnote.dao.TagDAO;
import com.example.Thumbnote.objects.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

class TagServiceTest {
    private NoteDAO mockNoteDAO;
    private AccDAO mockAccDAO;
    private TagDAO mockTagDAO;
    private TagService tagService;

    @BeforeEach
    public void setUp() {
        mockNoteDAO = mock(NoteDAO.class);
        mockAccDAO = mock(AccDAO.class);
        mockTagDAO = mock(TagDAO.class);
        tagService = new TagService(mockNoteDAO, mockAccDAO, mockTagDAO);
    }

    @Test
    public void testUpdateNoteTags() {
        long noteId = 1L;
        String username = "testuser";

        long userId = 1L;
        Long id = 1L;
        List<String> tags = Arrays.asList("tag1", "tag2");

        when(mockAccDAO.getUserID(username)).thenReturn(1L);
        when(mockTagDAO.updateNoteTags(noteId, 1L, tags)).thenReturn(new Note(id, userId, 0L, new Date(), "", "", null, null));

        Note actualNote = tagService.updateNoteTags(noteId, username, tags);

        verify(mockAccDAO).getUserID(username);
        verify(mockTagDAO).updateNoteTags(noteId, 1L, tags);

        assertNotNull(actualNote);
    }

    @Test
    public void testGetNoteTags() throws SQLException {
        long noteId = 1L;
        String username = "testuser";

        when(mockAccDAO.getUserID(username)).thenReturn(1L);
        when(mockTagDAO.getTagsForNoteId(1L, noteId)).thenReturn(Arrays.asList("tag1", "tag2"));

        List<String> actualTags = tagService.getNoteTags(username, noteId);

        verify(mockAccDAO).getUserID(username);
        verify(mockTagDAO).getTagsForNoteId(1L, noteId);

        assertEquals(Arrays.asList("tag1", "tag2"), actualTags);
    }

}