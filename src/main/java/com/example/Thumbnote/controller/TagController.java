package com.example.Thumbnote.controller;

import com.example.Thumbnote.annotation.Secure;
import com.example.Thumbnote.objects.Note;
import com.example.Thumbnote.service.AuthService;
import com.example.Thumbnote.service.TagService;
import com.example.Thumbnote.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/notes")
@Secure
@Slf4j
public class TagController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final TagService tagService;

    @Autowired
    public TagController(TagService tagService, AuthService authService, JwtUtil jwtUtil) {
        this.tagService = tagService;
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/{id}/addtag")
    public ResponseEntity<?> updateNoteTags(HttpServletRequest request, @PathVariable Long id, @RequestBody List<String> tags) {
        long userId = (long) request.getAttribute("userID");
        Note updatedNote = tagService.updateNoteTags(id, userId, tags);
        return ResponseEntity.ok(updatedNote);
    }

    @GetMapping("/{id}/tags")
    public ResponseEntity<List<String>> getNoteTags(HttpServletRequest request, @PathVariable Long id) {
        long userId = (long) request.getAttribute("userID");
        try {
            List<String> tags = tagService.getNoteTags(userId, id);
            return ResponseEntity.ok(tags);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}