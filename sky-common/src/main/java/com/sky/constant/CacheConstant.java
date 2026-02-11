package com.sky.constant;

/**
 * 缓存相关常量
 */
public class CacheConstant {

    /**
     * 菜品缓存键前缀
     */
    public static final String DISH_CACHE_PREFIX = "dish_";

    /**
     * 菜品分页查询缓存键前缀
     */
    public static final String DISH_PAGE_CACHE_PREFIX = "dish_page_";

    /**
     * 菜品缓存过期时间（分钟）
     */
    public static final long DISH_CACHE_EXPIRE = 30;

    /**
     * 分类缓存键前缀
     */
    public static final String CATEGORY_CACHE_PREFIX = "category:";

    /**
     * 分类缓存过期时间（分钟）
     */
    public static final long CATEGORY_CACHE_EXPIRE = 60;
}