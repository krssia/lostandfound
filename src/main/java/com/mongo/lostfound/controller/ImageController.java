package com.mongo.lostfound.controller;

import com.mongo.lostfound.service.ImageService;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;
    @GetMapping("/{imageId}")
    public ResponseEntity<InputStreamResource> getImage(@PathVariable String imageId) {
        GridFSFile file = imageService.getImage(imageId);
        if (file == null) {
            return ResponseEntity.notFound().build();
        }

        InputStream inputStream = imageService.getImageStream(imageId);

        String filename = Optional.ofNullable(file.getFilename())
                .orElse(imageId + imageService.getFileExtension(file));

        MediaType contentType = MediaType.APPLICATION_OCTET_STREAM;
        if (file.getMetadata() != null) {
            Object typeObj = file.getMetadata().get("contentType");
            if (typeObj != null) {
                contentType = MediaType.parseMediaType(typeObj.toString());
            }
        }

        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=\"" + filename + "\"")
                .contentType(contentType)
                .body(new InputStreamResource(inputStream));
    }


    @GetMapping("/zip")
    public ResponseEntity<ByteArrayResource> getImagesAsZip(@RequestParam("imageIds") List<String> imageIds) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (String imageId : imageIds) {
                GridFSFile file = imageService.getImage(imageId);
                if (file != null) {
                    try (InputStream stream = imageService.getImageStream(imageId)) {
                        ZipEntry entry = new ZipEntry(imageId + imageService.getFileExtension(file));
                        zos.putNextEntry(entry);
                        stream.transferTo(zos);
                        zos.closeEntry();
                    }
                }
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=images.zip")
                .contentType(MediaType.parseMediaType("application/zip"))
                .body(new ByteArrayResource(baos.toByteArray()));
    }

}
