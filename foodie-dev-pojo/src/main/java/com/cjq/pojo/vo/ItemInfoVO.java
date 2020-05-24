package com.cjq.pojo.vo;


import com.cjq.pojo.Items;
import com.cjq.pojo.ItemsImg;
import com.cjq.pojo.ItemsParam;
import com.cjq.pojo.ItemsSpec;
import lombok.Data;

import java.util.List;

/**
 * 商品详情VO
 */
@Data
public class ItemInfoVO {

    private Items item;
    private List<ItemsImg> itemImgList;
    private List<ItemsSpec> itemSpecList;
    private ItemsParam itemParams;

}
