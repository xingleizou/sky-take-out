package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 批量插入套餐菜品关联数据
     * @param setmealDish
     */
    void insert(SetmealDish setmealDish);

    /**
     * 根据套餐id删除套餐菜品关联数据
     * @param setmealId
     */
    @Delete("DELETE FROM setmeal_dish WHERE setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);

    /**
     * 根据套餐ids删除套餐菜品关联数据
     * @param setmealIds
     */
    void deleteBySetmealIds(List<Long> setmealIds);

    /**
     * 根据菜品id查询套餐菜品关联数据数量
     * @param dishId
     * @return
     */
    int countByDishId(Long dishId);
}