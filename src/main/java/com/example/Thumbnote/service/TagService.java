package com.example.Thumbnote.service;

import com.example.Thumbnote.dao.AccDAO;
import com.example.Thumbnote.dao.NoteDAO;
import com.example.Thumbnote.dao.TagDAO;
import com.example.Thumbnote.objects.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class TagService {

    private final NoteDAO noteDAO;
    private final AccDAO accDAO;
    private final TagDAO tagDAO;

    @Autowired
    public TagService(NoteDAO noteDAO, AccDAO accDAO, TagDAO tagDAO) {
        this.noteDAO = noteDAO;
        this.accDAO = accDAO;
        this.tagDAO = tagDAO;
    }

    public Note updateNoteTags(Long id, long userId, List<String> tags) {

        return tagDAO.updateNoteTags(id, userId, tags);
    }

    public List<String> getNoteTags(long userId, Long id) throws SQLException {
        return tagDAO.getTagsForNoteId(userId, id);
    }
}