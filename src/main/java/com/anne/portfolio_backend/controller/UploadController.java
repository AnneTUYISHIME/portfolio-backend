package com.anne.portfolio_backend.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class UploadController {

    @Autowired
    private Cloudinary cloudinary;

    @Operation(summary = "Upload profile picture")
    @RequestBody(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
    @PostMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadProfile(
            @RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();
        try {
            System.out.println("File received: " + file.getOriginalFilename());
            System.out.println("File size: " + file.getSize());
            System.out.println("Cloudinary object: " + cloudinary);

            Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                    "folder", "portfolio/profile",
                    "public_id", "profile_picture",
                    "overwrite", true
                )
            );
            System.out.println("Upload result: " + uploadResult);
            response.put("url", uploadResult.get("secure_url").toString());
            response.put("message", "Profile picture uploaded successfully!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            response.put("message", "Upload failed: " + e.getMessage());
            response.put("error", e.getClass().getName());
            return ResponseEntity.status(500).body(response);
        }
    }

    @Operation(summary = "Upload CV")
    @RequestBody(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
    @PostMapping(value = "/cv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadCV(
            @RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();
        try {
            System.out.println("CV received: " + file.getOriginalFilename());
            Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                    "folder", "portfolio/cv",
                    "public_id", "my_cv",
                    "resource_type", "raw",
                    "overwrite", true
                )
            );
            response.put("url", uploadResult.get("secure_url").toString());
            response.put("message", "CV uploaded successfully!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            response.put("message", "Upload failed: " + e.getMessage());
            response.put("error", e.getClass().getName());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/test")
    public String test() {
        return "Upload controller is working!";
    }
}