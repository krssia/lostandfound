package com.mongo.lostfound.service;

import com.mongodb.client.gridfs.GridFSBucket;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

}

