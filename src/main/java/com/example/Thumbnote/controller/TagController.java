package com.example.Thumbnote.controller;

import com.example.Thumbnote.objects.Note;
import com.example.Thumbnote.service.AuthService;
import com.example.Thumbnote.service.NoteService;
import com.example.Thumbnote.service.TagService;
import com.example.Thumbnote.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("api/notes")
public class TagController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final TagService tagService;

    @Autowired
    private Environment env;

    @Autowired
    public TagController(TagService tagService, AuthService authService, JwtUtil jwtUtil) {
        this.tagService = tagService;
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }




    @PostMapping("/{id}/addtag")
    public ResponseEntity<?> updateNoteTags(@RequestHeader("Authorization") String authHeader, @PathVariable Long id, @RequestBody List<String> tags) {
        String token = authHeader.replace("Bearer ", "");
        String username = authService.getUsernameFromToken(token);
        if (jwtUtil.validateToken(token, username)) {

            Note updatedNote = tagService.updateNoteTags(id, username, tags);
            return ResponseEntity.ok(updatedNote);
        } else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/{id}/tags")
    public ResponseEntity<List<String>> getNoteTags(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {
        String token = authHeader.replace("Bearer ", "");
        String username = authService.getUsernameFromToken(token);
        if (jwtUtil.validateToken(token, username)) {
            try {
                List<String> tags = tagService.getNoteTags(username, id);
                return ResponseEntity.ok(tags);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}