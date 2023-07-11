package com.example.Thumbnote.service;

import com.example.Thumbnote.dao.AccDAO;
import com.example.Thumbnote.dao.NoteDAO;
import com.example.Thumbnote.objects.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.sql.SQLException;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class NoteServiceTest {

    private NoteDAO mockNoteDAO;
    private AccDAO mockAccDAO;
    private NoteService noteService;

    @BeforeEach
    public void setUp() {
        mockNoteDAO = mock(NoteDAO.class);
        mockAccDAO = mock(AccDAO.class);
        noteService = new NoteService(mockNoteDAO, mockAccDAO);
    }
    @Test
    public void testAttachPicture() {
        // Arrange
        String username = "testuser";
        Long id = 123L;
        String picturePath = "/path/to/picture.png";
        Note note = new Note(123L, 1L, 2L, new Date(), "Note1", "This is a note", Collections.emptyList(), null);
        when(mockAccDAO.getUserID(username)).thenReturn(1L);
        when(mockNoteDAO.getById(1L, id)).thenReturn(note);
        when(mockNoteDAO.attachPictureToNote(note, picturePath)).thenReturn(true);

        // Act
        boolean result = noteService.attachPicture(username, id, picturePath);

        // Assert
        assertTrue(result);
        assertEquals(picturePath, note.getPicturePath());
        verify(mockAccDAO).getUserID(username);
        verify(mockNoteDAO).getById(1L, id);
        verify(mockNoteDAO).attachPictureToNote(note, picturePath);
    }

    @Test
    public void testAttachPictureWithNullNote() {
        // Arrange
        String username = "testuser";
        Long id = 123L;
        String picturePath = "/path/to/picture.png";
        when(mockAccDAO.getUserID(username)).thenReturn(1L);
        when(mockNoteDAO.getById(1L, id)).thenReturn(null);

        // Act
        boolean result = noteService.attachPicture(username, id, picturePath);

        // Assert
        assertFalse(result);
        verify(mockAccDAO).getUserID(username);
        verify(mockNoteDAO).getById(1L, id);
        verifyNoMoreInteractions(mockNoteDAO);
    }




    @Test
    public void testAddNoteToNotebook() {
        long userId = 1L;
        long notebookId = 2L;
        Note noteToAdd = new Note(3L, userId, 0L, new Date(), "Note to Add", "This note will be added to the notebook.", null, null);

        when(mockAccDAO.getUserID(anyString())).thenReturn(userId);
        when(mockNoteDAO.addNoteToNotebook(noteToAdd, notebookId)).thenReturn(true);

        boolean success = noteService.addNoteToNotebook( noteToAdd, notebookId);

        verify(mockNoteDAO).addNoteToNotebook(noteToAdd, notebookId);

        assertTrue(success);
    }



    @Test
    public void testGetAllNotes() {
        String username = "testuser";
        long userId = 1L;
        List<Note> expectedNotes = Arrays.asList(
                new Note(1L, userId, 1L, new Date(), "Note 1", "This is the first note.", Arrays.asList("tag1", "tag2"), null),
                new Note(2L, userId, 1L, new Date(), "Note 2", "This is the second note.", Arrays.asList("tag3", "tag4"), null)
        );

        when(mockAccDAO.getUserID(username)).thenReturn(userId);
        when(mockNoteDAO.getAllNotes(userId)).thenReturn(expectedNotes);

        List<Note> actualNotes = noteService.getAllNotes(username);

        verify(mockAccDAO).getUserID(username);
        verify(mockNoteDAO).getAllNotes(userId);

        assertEquals(expectedNotes, actualNotes);
    }
    @Test
    void testSearchNotesWithNameAndTags() {
        Date uploadDate = new Date();
        List<Note> notes = new ArrayList<>();
        Note note1 = new Note(1L, 1L, 1L, uploadDate, "Test Note 1", "This is a test note.", List.of("test", "note"), null);
        Note note2 = new Note(2L, 1L, 1L, uploadDate, "Test Note 2", "This is another test note.", List.of("test"), null);
        notes.add(note1);
        notes.add(note2);

        when(mockNoteDAO.searchNotes(eq("test"), eq(List.of("test", "note")), eq("name"), eq("asc"), eq(1L))).thenReturn(notes);

        List<Note> result = noteService.searchNotes("test", List.of("test", "note"), "name", "asc", 1L);

        assertEquals(notes, result);
    }

    @Test
    void testSearchNotesWithNoNameAndTags() {
        Date uploadDate = new Date();
        List<Note> notes = new ArrayList<>();
        Note note1 = new Note(1L, 1L, 1L, uploadDate, "Test Note 1", "This is a test note.", List.of("test", "note"), null);
        Note note2 = new Note(2L, 1L, 1L, uploadDate, "Test Note 2", "This is another test note.", List.of("test"), null);
        notes.add(note1);
        notes.add(note2);

        when(mockNoteDAO.searchNotes(isNull(), isNull(), eq("upload_Date"), eq("desc"), eq(1L))).thenReturn(notes);

        List<Note> result = noteService.searchNotes(null, null, "upload_Date", "desc", 1L);

        assertEquals(notes, result);
    }

    @Test
    void testSearchNotesWithInvalidSortBy() {
        List<Note> notes = new ArrayList<>();

        when(mockNoteDAO.searchNotes(anyString(), anyList(), eq("invalid"), anyString(), anyLong())).thenReturn(notes);

        List<Note> result = noteService.searchNotes("test", List.of("test", "note"), "invalid", "asc", 1L);

        assertEquals(notes, result);
    }

    @Test
    void testSearchNotesWithInvalidSortOrder() {
        List<Note> notes = new ArrayList<>();

        when(mockNoteDAO.searchNotes(anyString(), anyList(), eq("name"), eq("invalid"), anyLong())).thenReturn(notes);

        List<Note> result = noteService.searchNotes("test", List.of("test", "note"), "name", "invalid", 1L);

        assertEquals(notes, result);
    }


    @Test
    public void testGetNoteById() {
        String username = "testuser";
        long userId = 1L;
        Long id = 1L;
        Note expectedNote = new Note(id, userId, 0L, new Date(), "", "", null, null);

        when(mockAccDAO.getUserID(username)).thenReturn(userId);
        when(mockNoteDAO.getById(userId, id)).thenReturn(expectedNote);

        Note actualNote = noteService.getNoteById(username, id);

        verify(mockAccDAO).getUserID(username);
        verify(mockNoteDAO).getById(userId, id);

        assertEquals(expectedNote, actualNote);

        assertNotNull(actualNote.getLastAccessDate());
    }


    @Test
    public void testUpdateNoteTags() {
        long noteId = 1L;
        String username = "testuser";

        long userId = 1L;
        Long id = 1L;
        List<String> tags = Arrays.asList("tag1", "tag2");

        when(mockAccDAO.getUserID(username)).thenReturn(1L);
        when(mockNoteDAO.updateNoteTags(noteId, 1L, tags)).thenReturn(new Note(id, userId, 0L, new Date(), "", "", null, null));

        Note actualNote = noteService.updateNoteTags(noteId, username, tags);

        verify(mockAccDAO).getUserID(username);
        verify(mockNoteDAO).updateNoteTags(noteId, 1L, tags);

        assertNotNull(actualNote);
    }

    @Test
    public void testGetNoteTags() throws SQLException {
        long noteId = 1L;
        String username = "testuser";

        when(mockAccDAO.getUserID(username)).thenReturn(1L);
        when(mockNoteDAO.getTagsForNoteId(1L, noteId)).thenReturn(Arrays.asList("tag1", "tag2"));

        List<String> actualTags = noteService.getNoteTags(username, noteId);

        verify(mockAccDAO).getUserID(username);
        verify(mockNoteDAO).getTagsForNoteId(1L, noteId);

        assertEquals(Arrays.asList("tag1", "tag2"), actualTags);
    }

    @Test
    public void testGetNoteByIdWithNotNullNote() {
        long noteId = 1L;
        String username = "testuser";

        long userId = 1L;
        Long id = 1L;
        Note expectedNote = new Note(id, userId, 0L, new Date(), "", "", null, null);
        when(mockAccDAO.getUserID(username)).thenReturn(1L);
        when(mockNoteDAO.getById(1L, noteId)).thenReturn(expectedNote);

        Note actualNote = noteService.getNoteById(username, noteId);

        verify(mockAccDAO).getUserID(username);
        verify(mockNoteDAO).getById(1L, noteId);

        assertSame(expectedNote, actualNote);
    }

    @Test
    public void testGetNoteByIdWithNullNote() {
        long noteId = 1L;
        String username = "testuser";

        when(mockAccDAO.getUserID(username)).thenReturn(1L);
        when(mockNoteDAO.getById(1L, noteId)).thenReturn(null);

        Note actualNote = noteService.getNoteById(username, noteId);

        verify(mockAccDAO).getUserID(username);
        verify(mockNoteDAO).getById(1L, noteId);

        assertNull(actualNote);
    }

    @Test
    public void testCreateNote() {
        String username = "testuser";

        long userId = 1L;
        Long id = 1L;
        Note note = new Note(id, userId, 0L, new Date(), "", "", null, null);

        when(mockAccDAO.getUserID(username)).thenReturn(1L);
        when(mockNoteDAO.AddNote(note)).thenReturn(true);

        boolean success = noteService.createNote(username, note);

        verify(mockAccDAO).getUserID(username);
        verify(mockNoteDAO).AddNote(note);

        assertTrue(success);
    }

    @Test
    public void testUpdateNote() throws SQLException {
        long noteId = 1L;
        String username = "testuser";

        long userId = 1L;
        Long id = 1L;
        Note existingNote = new Note(id, userId, 0L, new Date(), "Note Name", "Note Text", null, null);
        Note newNote = new Note(id, userId, 0L, new Date(), "Updated Note Name", "Updated Note Text", null, null);

        when(mockAccDAO.getUserID(username)).thenReturn(userId);
        when(mockNoteDAO.getById(userId, noteId)).thenReturn(existingNote);
        when(mockNoteDAO.updateNote(existingNote)).thenReturn(true);

        boolean success = noteService.updateNote(username, noteId, newNote);

        verify(mockAccDAO).getUserID(username);
        verify(mockNoteDAO).getById(userId, noteId);
        verify(mockNoteDAO).updateNote(existingNote);

        assertTrue(success);

        assertEquals(existingNote.getNoteName(), newNote.getNoteName());
        assertEquals(existingNote.getNoteText(), newNote.getNoteText());
    }

    @Test
    public void testDeleteNoteById() {
        long noteId = 1L;
        String username = "testuser";

        long userId = 1L;
        Long id = 1L;
        Note note = new Note(id, userId, 0L, new Date(), "", "", null, null);
        when(mockAccDAO.getUserID(username)).thenReturn(1L);
        when(mockNoteDAO.getById(1L, noteId)).thenReturn(note);
        when(mockNoteDAO.deleteNote(note, 1L)).thenReturn(true);
        boolean success = noteService.deleteNoteById(username, noteId);
        verify(mockAccDAO).getUserID(username);
        verify(mockNoteDAO).getById(1L, noteId);
        verify(mockNoteDAO).deleteNote(note, 1L);
        assertTrue(success);
    }

    @Test
    public void testGetAllNotebookNotes() {
        long userId = 1L;
        long notebookId = 1L;
        when(mockNoteDAO.getAllNotebookNotes(userId, notebookId)).thenReturn(Arrays.asList(
                new Note(1L, userId, notebookId, new Date(), "Note 1", "Note Text 1", null, null),
                new Note(2L, userId, notebookId, new Date(), "Note 2", "Note Text 2", null, null)
        ));
        List<Note> actualNotes = noteService.getAllNotebookNotes(userId, notebookId);
        verify(mockNoteDAO).getAllNotebookNotes(userId, notebookId);
        assertNotNull(actualNotes);
        assertEquals(2, actualNotes.size());
    }

    @Test
    public void testDeleteNoteFromNotebook() {
        long notebookId = 1L;

        long userId = 1L;
        Long id = 1L;
        Note note = new Note(id, userId, 0L, new Date(), "", "", null, null);
        when(mockNoteDAO.deleteNoteFromNotebook(note, notebookId)).thenReturn(true);
        boolean success = noteService.deleteNoteFromNotebook(note, notebookId);
        verify(mockNoteDAO).deleteNoteFromNotebook(note, notebookId);
        assertTrue(success);
    }
}