package com.mongo.lostfound.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ItemSearchVO extends ItemVO {
    private Double distance;
}
