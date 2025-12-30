package com.sky.constant;

/**
 * 店铺营业状态常量类
 */
public class ShopConstant {

    // 营业状态 - 营业中
    public static final String BUSINESS_STATUS_OPEN = "1";

    // 营业状态 - 打烊
    public static final String BUSINESS_STATUS_CLOSE = "0";

    // Redis中存储营业状态的键
    public static final String BUSINESS_STATUS_KEY = "SHOP_BUSINESS_STATUS";

}