package com.sky.service;

/**
 * 店铺营业状态管理接口
 */
public interface ShopService {

    /**
     * 设置店铺营业状态
     * @param status 营业状态 1-营业中 0-打烊
     */
    void setShopStatus(Integer status);

    /**
     * 获取店铺营业状态
     * @return 营业状态 1-营业中 0-打烊
     */
    Integer getShopStatus();
}