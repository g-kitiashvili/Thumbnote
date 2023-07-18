package com.example.Thumbnote.controller;

import com.example.Thumbnote.annotation.Secure;
import com.example.Thumbnote.objects.Note;
import com.example.Thumbnote.service.TagService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("api/notes")
@Secure

public class TagController {


    private final TagService tagService;


    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;

    }

    @PostMapping("/{id}/addtag")
    public ResponseEntity<?> updateNoteTags(HttpServletRequest request, @PathVariable Long id, @RequestBody List<String> tags) {
        long userId = (long) request.getAttribute("userId");
        Note updatedNote = tagService.updateNoteTags(id, userId, tags);
        return ResponseEntity.ok(updatedNote);
    }

    @GetMapping("/{id}/tags")
    public ResponseEntity<List<String>> getNoteTags(HttpServletRequest request, @PathVariable Long id) {
        long userId = (long) request.getAttribute("userId");
        try {
            List<String> tags = tagService.getNoteTags(userId, id);
            return ResponseEntity.ok(tags);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}