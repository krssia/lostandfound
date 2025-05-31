package com.mongo.lostfound.service;

import com.mongo.lostfound.dto.ItemPostDTO;
import com.mongo.lostfound.dto.ItemPutDTO;
import com.mongo.lostfound.mapper.ItemMapper;
import com.mongo.lostfound.util.BeanCopyUtils;
import com.mongo.lostfound.vo.ItemVO;
import com.mongo.lostfound.entity.Item;
import com.mongo.lostfound.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final ImageService imageService;
    private final MongoTemplate mongoTemplate;

    public ItemVO getById(String id) {
        Optional<Item> optionalItem = itemRepository.findById(id);
        if (optionalItem.isPresent()) {

            Item item = optionalItem.get();

            return itemMapper.toVO(item);
        } else {
            return null;
        }
    }

    public ItemVO uploadItemWithImage(ItemPostDTO dto, List<MultipartFile> files) {
        try {
            List<String> imageIds = imageService.storeImages(files);
            dto.setImageIds(imageIds);

            Item item = itemMapper.toEntity(dto);

            item.setLostOrFoundDate(dto.getLostOrFoundDate());

            if (dto.getLongitude() != null && dto.getLatitude() != null) {
                item.setLocation(new GeoJsonPoint(dto.getLongitude(), dto.getLatitude()));
            }

            return itemMapper.toVO(itemRepository.save(item));
        } catch (IOException e) {
            throw new RuntimeException("图片上传失败", e);
        }
    }


    public List<Document> getDailyStats() {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.group("status")
                        .count().as("count")
                        .push("location").as("locations"),
                Aggregation.project("count", "locations")
                        .and("_id").as("status")
                        .andExclude("_id")
        );
        return mongoTemplate.aggregate(agg, Item.class, Document.class)
                .getMappedResults();
    }


    public boolean deleteItemById(String id) {
        Optional<Item> optionalItem = itemRepository.findById(id);
        if (optionalItem.isPresent()) {
            Item item = optionalItem.get();
            if (item.getImageIds() != null && !item.getImageIds().isEmpty()) {
                imageService.deleteImages(item.getImageIds());
            }
            itemRepository.deleteById(id);
            return true;
        }
        return false;
    }


    public ItemVO updateItemWithImages(String id, ItemPutDTO dto, List<MultipartFile> files) throws IOException {
        Optional<Item> optionalItem = itemRepository.findById(id);
        if (optionalItem.isEmpty()) return null;

        Item existingItem = optionalItem.get();

        if (files != null && !files.isEmpty()) {
            // 删除旧图片
            if (existingItem.getImageIds() != null && !existingItem.getImageIds().isEmpty()) {
                imageService.deleteImages(existingItem.getImageIds());
            }
            List<String> newImageIds = imageService.storeImages(files);
            dto.setImageIds(newImageIds);
        } else {
            dto.setImageIds(existingItem.getImageIds());
        }

        BeanCopyUtils.copyNonNullProperties(dto, existingItem);

        if (dto.getLongitude() != null && dto.getLatitude() != null) {
            existingItem.setLocation(new GeoJsonPoint(dto.getLongitude(), dto.getLatitude()));
        }

        return itemMapper.toVO(itemRepository.save(existingItem));
    }


    public List<ItemVO> textSearch(String keyword) {
        String regex = Pattern.quote(keyword);
        Criteria criteria = new Criteria().orOperator(
                Criteria.where("title").regex(regex, "i"),
                Criteria.where("description").regex(regex, "i")
        );

        return mongoTemplate.find(Query.query(criteria), Item.class).stream()
                .map(itemMapper::toVO)
                .collect(Collectors.toList());
    }

    public List<Document> geoOnlySearch(Double lng, Double lat, Double maxDistanceKm) {
        NearQuery nearQuery = NearQuery.near(new GeoJsonPoint(lng, lat))
                .maxDistance(new Distance(maxDistanceKm, Metrics.KILOMETERS))
                .spherical(true)
                .inKilometers();

        Aggregation agg = Aggregation.newAggregation(
                Aggregation.geoNear(nearQuery, "distance"),
                Aggregation.project()
                        .andInclude("id", "title", "location")
                        .and(ArithmeticOperators.Divide.valueOf("distance").divideBy(1000)).as("distanceKm")  // distance默认米，转千米
                        .andExclude("_id")
        );

        return mongoTemplate.aggregate(agg, Item.class, Document.class).getMappedResults();
    }

    public List<Document> geoTextAggregationSearch(String keyword, Double lng, Double lat, Double maxDistanceKm) {
        String regex = Pattern.quote(keyword);
        Criteria criteria = new Criteria().orOperator(
                Criteria.where("title").regex(regex, "i"),
                Criteria.where("description").regex(regex, "i")
        );

        NearQuery nearQuery = NearQuery.near(new GeoJsonPoint(lng, lat))
                .maxDistance(new Distance(maxDistanceKm, Metrics.KILOMETERS))
                .spherical(true)
                .inKilometers()
                .query(Query.query(criteria));

        Aggregation agg = Aggregation.newAggregation(
                Aggregation.geoNear(nearQuery, "distance"),
                Aggregation.project()
                        .andInclude("id", "title", "location", "description")
                        .and(ArithmeticOperators.Divide.valueOf("distance").divideBy(1000)).as("distanceKm")
                        .andExclude("_id")
        );

        return mongoTemplate.aggregate(agg, Item.class, Document.class).getMappedResults();
    }



}
