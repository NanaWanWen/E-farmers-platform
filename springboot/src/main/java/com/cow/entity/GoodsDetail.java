package com.cow.entity;

import lombok.Data;

@Data
public class GoodsDetail {
    private String goods_id;//商品唯一编号（可以用你的 orderNo 或者数据库中的商品主键）
    private String goods_name;//商品名称
    private Long price;//单价，单位为“分”（注意要把原来元为单位的价格 ×100）
    private Long quantity;//购买数量
}

