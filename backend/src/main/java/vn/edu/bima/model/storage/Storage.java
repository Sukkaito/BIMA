package vn.edu.bima.model.storage;

import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import vn.edu.bima.service.StorageService;
import vn.edu.bima.config.StorageProperties;

@Service
public class Storage implements StorageService {

    private final Path uploadLocation;

    @Autowired
    public Storage(StorageProperties properties) {
        this.uploadLocation = Paths.get(properties.getLocation());
    }

    @Override
    public void store(MultipartFile file) {
        try {
            Files.copy(file.getInputStream(),
                    this.uploadLocation.resolve(file.getOriginalFilename()),
                    StandardCopyOption.REPLACE_EXISTING
            );
        } catch (Exception e) {
            throw new RuntimeException("FAIL!");
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.uploadLocation, 1)
                    .filter(path -> !path.equals(this.uploadLocation))
                    .map(this.uploadLocation::relativize);
        } catch (IOException e) {
            throw new RuntimeException("FAIL!");
        }
    }

    @Override
    public Path load(String filename) {
        return uploadLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(Path file) {
        try {
            return new UrlResource(file.toUri());
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    public void init() {
        try {
            Files.createDirectory(uploadLocation);
        } catch (FileAlreadyExistsException e) {
            // Do nothing
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage!");
        }
    }
}