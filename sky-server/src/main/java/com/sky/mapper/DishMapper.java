package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 根据条件动态查询菜品
     * @param dish
     * @return
     */
    List<Dish> list(Dish dish);

    /**
     * 插入菜品数据
     * @param dish
     */
    void insert(Dish dish);

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据主键查询菜品
     * @param id
     * @return
     */
    Dish getById(Long id);

    /**
     * 根据主键修改菜品基本信息
     * @param dish
     */
    void update(Dish dish);

    /**
     * 根据主键删除菜品
     * @param ids
     */
    @Delete("DELETE FROM dish WHERE id IN #{ids}")
    void deleteByIds(List<Long> ids);
}