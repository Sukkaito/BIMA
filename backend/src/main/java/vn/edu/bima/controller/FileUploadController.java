package vn.edu.bima.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import vn.edu.bima.model.storage.Storage;

@RestController
class FileUploadController {
    private final Storage storage;

    @Autowired
    public FileUploadController(Storage storage) {
        this.storage = storage;
    }

    @GetMapping("/files")
    public ResponseEntity<List<String>> listUploadedFiles() {
        List<String> files = storage.loadAll()
                .map(Path::toString)
                .toList();

        return ResponseEntity.ok(files);
    }

    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Path file = storage.load(filename);
        Resource resource = storage.loadAsResource(file);
        String contentType;
        try {
            contentType = Files.probeContentType(file);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
        } catch (Exception e) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) {
        storage.store(file);

        return "File uploaded successfully!";
    }
}