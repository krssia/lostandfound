package com.mongo.lostfound.mapper;

import com.mongo.lostfound.dto.ItemBaseDTO;
import com.mongo.lostfound.dto.ItemPostDTO;
import com.mongo.lostfound.vo.ItemVO;
import com.mongo.lostfound.entity.Item;
import org.springframework.stereotype.Component;

@Component
public class ItemMapper {

    private Item convert(ItemBaseDTO dto) {
        if (dto == null) return null;
        Item item = new Item();
        item.setContactInfo(dto.getContactInfo());
        item.setDescription(dto.getDescription());
        item.setTitle(dto.getTitle());
        item.setType(dto.getType());
        item.setUserId(dto.getUserId());
        item.setStatus(dto.getStatus());
        item.setLostOrFoundDate(dto.getLostOrFoundDate());
        return item;
    }

    public Item toEntity(ItemPostDTO dto) {
        return convert(dto);
    }

    public ItemVO toVO(Item item) {
        if (item == null) return null;
        ItemVO vo = new ItemVO();
        vo.setId(item.getId());
        vo.setContactInfo(item.getContactInfo());
        vo.setCreatedAt(item.getCreatedAt());
        vo.setDescription(item.getDescription());
        vo.setLocation(item.getLocation());
        vo.setLostOrFoundDate(item.getLostOrFoundDate());
        vo.setStatus(item.getStatus());
        vo.setTitle(item.getTitle());
        vo.setType(item.getType());
        vo.setUserId(item.getUserId());
        vo.setImageIds(item.getImageIds());
        return vo;
    }
}
