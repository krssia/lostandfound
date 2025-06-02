package com.mongo.lostfound.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.model.Filters;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final GridFSBucket gridFSBucket;

    public String storeImage(MultipartFile file) throws IOException {
        return gridFSBucket.uploadFromStream(
                file.getOriginalFilename(),
                file.getInputStream()
        ).toString();
    }

    public List<String> storeImages(List<MultipartFile> files) throws IOException {
        return files.stream()
                .map(file -> {
                    try {
                        return storeImage(file);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    public void deleteImage(String imageId) {
        ObjectId objectId = new ObjectId(imageId);
        gridFSBucket.delete(objectId);
    }

    public void deleteImages(List<String> imageIds) {
        imageIds.forEach(this::deleteImage);
    }

    // ImageService.java 新增方法
    public GridFSFile getImage(String imageId) {
        ObjectId objectId = new ObjectId(imageId);
        return gridFSBucket.find(Filters.eq("_id", objectId)).first();
    }

    public InputStream getImageStream(String imageId) {
        GridFSFile file = getImage(imageId);
        if (file == null) return null;

        return gridFSBucket.openDownloadStream(file.getObjectId());
    }

    public String getFileExtension(GridFSFile file) {
        String contentType = "application/octet-stream";

        if (file.getMetadata() != null) {
            Object contentTypeObj = file.getMetadata().get("_contentType");
            if (contentTypeObj != null) {
                contentType = contentTypeObj.toString().toLowerCase();
            }
        }

        switch (contentType) {
            case "image/jpeg", "image/jpg" -> {
                return ".jpg";
            }
            case "image/png" -> {
                return ".png";
            }
            case "image/gif" -> {
                return ".gif";
            }
            default -> {
                String filename = file.getFilename().toLowerCase();
                if (filename.endsWith(".jpg")) {
                    return ".jpg";
                } else if (filename.endsWith(".png")) {
                    return ".png";
                }
                return ".dat";
            }
        }
    }
}

