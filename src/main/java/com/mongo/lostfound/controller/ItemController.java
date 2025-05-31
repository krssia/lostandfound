package com.mongo.lostfound.controller;


import com.mongo.lostfound.Response.Response;
import com.mongo.lostfound.dto.ItemPostDTO;
import com.mongo.lostfound.dto.ItemPutDTO;
import com.mongo.lostfound.vo.ItemVO;
import com.mongo.lostfound.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    @GetMapping("/{id}")
    public ResponseEntity<Response<ItemVO>> getById(@PathVariable String id) {
        ItemVO itemVO = itemService.getById(id);
        if (itemVO != null) {
            return ResponseEntity.ok(new Response<>(true, itemVO, "查询成功"));
        } else {
            return ResponseEntity.ok(new Response<>(false, null, "未找到物品"));
        }
    }
    @PostMapping(value = "/item/upload", consumes = "multipart/form-data")
    public ResponseEntity<Response<ItemVO>> itemUpload(@RequestPart("data") @Valid ItemPostDTO data, @RequestPart("images") List<MultipartFile> files) {
        ItemVO itemVO = itemService.uploadItemWithImage(data, files);
        return ResponseEntity.ok(new Response<>(true, itemVO, "上传成功"));
    }

    @GetMapping("/stats/daily")
    public ResponseEntity<Response<List<Document>>> getDailyStats() {
        List<Document> stats = itemService.getDailyStats();
        return ResponseEntity.ok(new Response<>(true, stats, "统计成功"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Void>> deleteItem(@PathVariable String id) {
        boolean success = itemService.deleteItemById(id);
        return success ?
                ResponseEntity.ok(new Response<>(true, null, "删除成功")) :
                ResponseEntity.ok(new Response<>(false, null, "物品不存在"));
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<Response<ItemVO>> updateItem(
            @PathVariable String id,
            @RequestPart("data") @Valid ItemPutDTO data,
            @RequestPart(value = "images", required = false) List<MultipartFile> files) throws IOException {

        ItemVO itemVO = itemService.updateItemWithImages(id, data, files);
        return itemVO != null ?
                ResponseEntity.ok(new Response<>(true, itemVO, "更新成功")) :
                ResponseEntity.ok(new Response<>(false, null, "物品不存在"));
    }

    @GetMapping("/text-search")
    public ResponseEntity<Response<List<ItemVO>>> textSearch(
            @RequestParam String keyword) {

        List<ItemVO> results = itemService.textSearch(keyword);
        return ResponseEntity.ok(new Response<>(true, results, "纯文本查询成功"));
    }

    @GetMapping("/geo-search")
    public ResponseEntity<Response<List<Document>>> geoSearch(
            @RequestParam double lng,
            @RequestParam double lat,
            @RequestParam double maxDistanceMeters) {

        List<Document> results = itemService.geoOnlySearch(lng, lat, maxDistanceMeters);
        return ResponseEntity.ok(new Response<>(true, results, "地理位置查询成功"));
    }

    @GetMapping("/geo-text-search")
    public ResponseEntity<Response<List<Document>>> geoTextSearch(
            @RequestParam String keyword,
            @RequestParam double lng,
            @RequestParam double lat,
            @RequestParam double maxDistanceMeters) {

        List<Document> results = itemService.geoTextAggregationSearch(keyword, lng, lat, maxDistanceMeters);
        return ResponseEntity.ok(new Response<>(true, results, "文本 + 地理混合查询成功"));
    }

}
