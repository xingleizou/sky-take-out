package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /**
     * 插入套餐数据
     * @param setmeal
     */
    void insert(Setmeal setmeal);

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 根据套餐id删除套餐
     * @param ids
     */
    @Delete("DELETE FROM setmeal WHERE id IN #{ids}")
    void deleteBatch(List<Long> ids);

    /**
     * 更新套餐信息
     * @param setmeal
     */
    void update(Setmeal setmeal);



    /**
     * 根据分类id查询套餐
     * @param categoryId
     * @return
     */
    List<Setmeal> getByCategoryId(Long categoryId);

    /**
     * 根据id查询套餐和菜品关联信息
     * @param id
     * @return
     */
    SetmealVO getSetmealWithDishes(Long id);

    /**
     * 根据id查询套餐和套餐菜品关联信息
     * @param id
     * @return
     */
    SetmealVO getSetmealWithDishesAndSetmealDish(Long id);
}