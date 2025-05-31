package com.mongo.lostfound.service;

import com.mongo.lostfound.dto.MessageDTO;
import com.mongo.lostfound.entity.Messages;
import com.mongo.lostfound.mapper.MessageMapper;
import com.mongo.lostfound.vo.MessageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MongoTemplate mongoTemplate;
    private final MessageMapper messageMapper = new MessageMapper();

    public MessageVO sendMessage(MessageDTO dto) {
        Messages messages = messageMapper.toEntity(dto);
        mongoTemplate.save(messages);
        return messageMapper.toVO(messages);
    }

    public List<MessageVO> getConversation(String userId1, String userId2) {
        Query query = new Query(new Criteria().orOperator(
                Criteria.where("fromUserId").is(userId1), Criteria.where("toUserId").is(userId2),
                Criteria.where("fromUserId").is(userId2), Criteria.where("toUserId").is(userId1)
        )).with(Sort.by(Sort.Direction.ASC, "sentAt"));

        List<Messages> messages = mongoTemplate.find(query, Messages.class);
        return messages.stream().map(messageMapper::toVO).collect(Collectors.toList());
    }

    public void markMessagesRead(String fromUserId, String toUserId) {
        Query query = new Query(Criteria.where("fromUserId").is(fromUserId)
                .and("toUserId").is(toUserId)
                .and("read").is(false));

        Update update = new Update().set("read", true);
        mongoTemplate.updateMulti(query, update, Messages.class);
    }
}
