package com.example.Thumbnote.service;

import com.example.Thumbnote.dao.AccDAO;
import com.example.Thumbnote.dao.NoteDAO;
import com.example.Thumbnote.dao.TagDAO;
import com.example.Thumbnote.objects.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

class TagServiceTest {
    @Mock
    private NoteDAO noteDAO;
    @Mock
    private AccDAO accDAO;
    @Mock
    private TagDAO tagDAO;

    private TagService tagService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        tagService = new TagService(noteDAO, accDAO, tagDAO);
    }

    @Test
    public void testUpdateNoteTags() {
        long noteId = 1L;
        long userId = 1L;
        List<String> tags = Arrays.asList("tag1", "tag2");

        when(tagDAO.updateNoteTags(noteId, userId, tags)).thenReturn(new Note(1L, userId, 0L, new Date(), "", "", null, null));

        Note actualNote = tagService.updateNoteTags(noteId, userId, tags);

        verify(tagDAO).updateNoteTags(noteId, userId, tags);

        assertNotNull(actualNote);
    }

    @Test
    public void testGetNoteTags() throws SQLException {
        long noteId = 1L;
        long userId = 1L;

        when(tagDAO.getTagsForNoteId(userId, noteId)).thenReturn(Arrays.asList("tag1", "tag2"));

        List<String> actualTags = tagService.getNoteTags(userId, noteId);

        verify(tagDAO).getTagsForNoteId(userId, noteId);

        assertEquals(Arrays.asList("tag1", "tag2"), actualTags);
    }
}