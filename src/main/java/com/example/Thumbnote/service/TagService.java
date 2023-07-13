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

    public Note updateNoteTags(Long id,String  username, List<String> tags) {
        long userId = accDAO.getUserID(username);

        return tagDAO.updateNoteTags(id, userId,tags);
    }

    public List<String> getNoteTags(String username,Long id) throws SQLException {
        long userId = accDAO.getUserID(username);
        return tagDAO.getTagsForNoteId(userId,id);
    }
}