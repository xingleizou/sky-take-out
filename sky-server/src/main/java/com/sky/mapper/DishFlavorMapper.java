package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 批量插入口味数据
     * @param flavor
     */
    void insert(DishFlavor flavor);

    /**
     * 根据菜品id删除口味数据
     * @param dishId
     */
    @Delete("DELETE FROM dish_flavor WHERE dish_id = #{dishId}")
    void deleteByDishId(Long dishId);

    /**
     * 根据菜品id删除口味数据
     * @param dishIds
     */
    void deleteByDishIds(List<Long> dishIds);

    /**
     * 根据菜品id查询口味数据
     * @param dishId
     * @return
     */
    List<DishFlavor> getByDishId(Long dishId);
}