package com.sky.service.impl;

import com.sky.constant.ShopConstant;
import com.sky.service.ShopService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ShopServiceImpl implements ShopService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 设置店铺营业状态
     * @param status 营业状态 1-营业中 0-打烊
     */
    @Override
    public void setShopStatus(Integer status) {
        // 验证状态值的有效性
        if (status != null && (status == 1 || status == 0)) {
            redisTemplate.opsForValue().set(ShopConstant.BUSINESS_STATUS_KEY, status);
            log.info("店铺营业状态已设置为: {}", status);
        } else {
            log.warn("无效的营业状态: {}, 仅支持 0(打烊) 或 1(营业中)", status);
            throw new IllegalArgumentException("营业状态值无效，仅支持 0(打烊) 或 1(营业中)");
        }
    }

    /**
     * 获取店铺营业状态
     * @return 营业状态 1-营业中 0-打烊
     */
    @Override
    public Integer getShopStatus() {
        Object status = redisTemplate.opsForValue().get(ShopConstant.BUSINESS_STATUS_KEY);
        if (status == null) {
            // 如果Redis中没有存储状态，默认为打烊状态
            log.info("Redis中未找到营业状态，默认为打烊状态");
            return 0; // 默认打烊
        }
        return Integer.valueOf(status.toString());
    }
}