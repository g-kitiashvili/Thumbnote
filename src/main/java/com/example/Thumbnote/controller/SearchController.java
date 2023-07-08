package com.example.Thumbnote.controller;

import com.example.Thumbnote.objects.Note;
import com.example.Thumbnote.service.AccountService;
import com.example.Thumbnote.service.NoteService;
import com.example.Thumbnote.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/search")
public class SearchController {

    private final NoteService noteService;
    private final AccountService accService;
    private final JwtUtil jwtUtil;

    @Autowired
    public SearchController(NoteService noteService, AccountService accService, JwtUtil jwtUtil) {
        this.noteService = noteService;
        this.accService = accService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public ResponseEntity<List<Note>> searchNotes(@RequestHeader("Authorization") String authHeader,
                                                  @RequestParam(required = false) String name,
                                                  @RequestParam(required = false) List<String> tags,
                                                  @RequestParam(required = false, defaultValue = "upload_Date") String sortBy,
                                                  @RequestParam(required = false, defaultValue = "desc") String sortOrder) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtUtil.getUsernameFromToken(token);
        long userId = accService.getUserID(username);

        List<Note> matchedNotes = noteService.searchNotes(name, tags, sortBy, sortOrder, userId);

        if (matchedNotes.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(matchedNotes);
        }
    }
}