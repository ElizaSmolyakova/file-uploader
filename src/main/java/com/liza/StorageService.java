package com.liza;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class StorageService {

    @Value("${content.root}")
    private String contentRoot;

    Set<String> files;

    @PostConstruct
    public void init() {
        files = Stream.of(new File(contentRoot).listFiles())
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toSet());
    }

    public void store(MultipartFile file) {

        if (file.isEmpty()) {
            //throw an exception
        }

        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(contentRoot + file.getOriginalFilename());
            Files.write(path, bytes);
            files.add(file.getOriginalFilename());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<String> loadAll() {
        return files;
    }

    public Resource loadAsResource(String filename) throws FileNotFoundException {
        try {
            Path file = Paths.get(contentRoot).resolve(filename);

            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new FileNotFoundException(
                        "Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new FileNotFoundException(
                    "Could not read file: " + filename);
        }
    }
}


