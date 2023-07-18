package com.example.Thumbnote.controller;

import com.example.Thumbnote.annotation.Secure;
import com.example.Thumbnote.objects.Note;
import com.example.Thumbnote.service.AccountService;
import com.example.Thumbnote.service.NoteService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("api/search")
public class SearchController {

    private final NoteService noteService;
    private final AccountService accService;

    @Autowired
    public SearchController(NoteService noteService, AccountService accService) {
        this.noteService = noteService;
        this.accService = accService;
    }

    @GetMapping
    @Secure
    public ResponseEntity<List<Note>> searchNotes(HttpServletRequest request,
                                                  @RequestParam(required = false) String name,
                                                  @RequestParam(required = false) List<String> tags,
                                                  @RequestParam(required = false, defaultValue = "upload_Date") String sortBy,
                                                  @RequestParam(required = false, defaultValue = "desc") String sortOrder) {
        long userId = (long) request.getAttribute("userId");

        List<Note> matchedNotes = noteService.searchNotes(name, tags, sortBy, sortOrder, userId);

        if (matchedNotes.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(matchedNotes);
        }
    }
}