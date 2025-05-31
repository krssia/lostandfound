package com.mongo.lostfound.mapper;

import com.mongo.lostfound.dto.MessageDTO;
import com.mongo.lostfound.entity.Messages;
import com.mongo.lostfound.vo.MessageVO;

import java.util.Date;

public class MessageMapper {
    public MessageVO toVO(Messages entity) {
        if (entity == null) return null;
        MessageVO vo = new MessageVO();
        vo.setId(entity.getId());
        vo.setFromUserId(entity.getFromUserId());
        vo.setToUserId(entity.getToUserId());
        vo.setContent(entity.getContent());
        vo.setSentAt(entity.getSentAt());
        vo.setRead(entity.isRead());
        return vo;
    }

    public Messages toEntity(MessageDTO dto) {
        if (dto == null) return null;
        Messages messages = new Messages();
        messages.setFromUserId(dto.getFromUserId());
        messages.setToUserId(dto.getToUserId());
        messages.setContent(dto.getContent());
        messages.setSentAt( new Date());
        messages.setRead(false);
        return messages;
    }
}
