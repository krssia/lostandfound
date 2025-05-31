package com.mongo.lostfound.config;

import com.mongo.lostfound.entity.Item;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;


@Configuration
public class MongoConfig {
    @Bean
    public MongoTemplate mongoTemplate(
            MongoDatabaseFactory factory,
            MongoMappingContext mappingContext
    ) {
        MongoTemplate template = new MongoTemplate(factory);

        MongoPersistentEntityIndexResolver resolver =
                new MongoPersistentEntityIndexResolver(mappingContext);

        resolver.resolveIndexFor(Item.class)
                .forEach(template.indexOps(Item.class)::createIndex);

        return template;
    }
    @Bean
    public GridFSBucket gridFSBucket(MongoDatabaseFactory factory) {
        return GridFSBuckets.create(factory.getMongoDatabase());
    }
}
