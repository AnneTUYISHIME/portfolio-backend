package com.anne.portfolio_backend.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class UploadController {

    @Autowired
    private Cloudinary cloudinary;

    @Value("${admin.password}")
    private String adminPassword;

    // ⭐ THIS STORES CV URL IN MEMORY (NO DATABASE NEEDED)
    private static String latestCvUrl = "";

    // ================= PROFILE PICTURE =================
    @PostMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadProfile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("password") String password) {

        Map<String, String> response = new HashMap<>();

        if (!password.equals(adminPassword)) {
            response.put("message", "Unauthorized! Wrong password.");
            return ResponseEntity.status(401).body(response);
        }

        try {
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "portfolio/profile",
                            "public_id", "profile_picture",
                            "overwrite", true,
                            "resource_type", "auto"
                    )
            );

            response.put("url", uploadResult.get("secure_url").toString());
            response.put("message", "Profile picture uploaded successfully!");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("message", "Upload failed: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // ================= CV UPLOAD =================


@PostMapping(value = "/cv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<Map<String, String>> uploadCV(
        @RequestParam("file") MultipartFile file,
        @RequestParam("password") String password) {

    Map<String, String> response = new HashMap<>();

    if (!password.equals(adminPassword)) {
        response.put("message", "Unauthorized! Wrong password.");
        return ResponseEntity.status(401).body(response);
    }

    try {
        Map uploadResult = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "portfolio/cv",
                        "public_id", "my_cv",
                        "resource_type", "auto",   // ⭐ THE REAL FIX
                        "overwrite", true
                )
        );

        // ⭐ USE EXACT URL FROM CLOUDINARY
        String url = uploadResult.get("secure_url").toString();

        response.put("url", url);
        response.put("message", "CV uploaded successfully!");
        return ResponseEntity.ok(response);

    } catch (Exception e) {
        response.put("message", "Upload failed: " + e.getMessage());
        return ResponseEntity.status(500).body(response);
    }
}
    // ================= GET PROFILE URL =================
    @GetMapping("/profile-url")
    public ResponseEntity<Map<String, String>> getProfileUrl() {
        Map<String, String> response = new HashMap<>();
        response.put("url",
                "https://res.cloudinary.com/" +
                        cloudinary.config.cloudName +
                        "/image/upload/portfolio/profile/profile_picture");
        return ResponseEntity.ok(response);
    }

    // ================= GET CV URL =================
 @GetMapping("/cv-url")
public ResponseEntity<Map<String, String>> getCvUrl() throws Exception {

    Map resource = cloudinary.api().resource(
            "portfolio/cv/my_cv",
            ObjectUtils.asMap("resource_type", "image")
    );

    String secureUrl = resource.get("secure_url").toString();

    // insert fl_attachment to force download
    String downloadUrl = secureUrl.replace("/upload/", "/upload/fl_attachment/");

    Map<String, String> response = new HashMap<>();
    response.put("url", downloadUrl);

    return ResponseEntity.ok(response);
}

    @GetMapping("/test")
    public String test() {
        return "Upload controller is working!";
    }
}