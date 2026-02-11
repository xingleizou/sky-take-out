package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.CacheConstant;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     * @param dishDTO
     */
    @Override
    @Transactional
    public void saveWithFlavors(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        // 设置创建时间和更新时间
        //dish.setCreateTime(LocalDateTime.now());
        //dish.setUpdateTime(LocalDateTime.now());

        // 设置创建人和更新人
        // 这里可以获取当前登录用户信息，暂时使用默认值
        //dish.setCreateUser("admin");
        //dish.setUpdateUser("admin");

        // 保存菜品基本信息
        dishMapper.insert(dish);

        // 获取保存的菜品id
        Long dishId = dish.getId();

        // 保存菜品口味信息
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dishId);
                dishFlavorMapper.insert(flavor);
            }
        }
        
        // 清理相关分类的缓存
        cleanDishCache(dish.getCategoryId());
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }
    /**
     * 批量删除菜品
     * @param ids
     */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        // 判断当前菜品是否被套餐关联
        for (Long id : ids) {
            // 查询套餐关联表
            int count = setmealDishMapper.countByDishId(id);
            if (count > 0) {
                // 当前菜品被套餐关联，不能删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        // 删除菜品数据
        dishMapper.deleteByIds(ids);

        // 删除菜品关联的口味数据
        dishFlavorMapper.deleteByDishIds(ids);
        
        // 清理所有菜品缓存（因为不确定删除了哪些分类的菜品）
        cleanDishCache(null);
    }

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        // 查询菜品基本信息
        Dish dish = dishMapper.getById(id);

        // 查询菜品口味信息
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);

        // 封装返回结果
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavors);

        return dishVO;
    }

    /**
     * 修改菜品
     * @param dishDTO
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        // 设置更新时间和更新人
        //dish.setUpdateTime(LocalDateTime.now());
        //dish.setUpdateUser("admin");

        // 修改菜品基本信息
        dishMapper.update(dish);

        // 删除原有的口味数据
        dishFlavorMapper.deleteByDishId(dishDTO.getId());

        // 重新插入口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dishDTO.getId());
                dishFlavorMapper.insert(flavor);
            }
        }
        
        // 清理相关分类的缓存
        cleanDishCache(dish.getCategoryId());
    }

    /**
     * 起售停售菜品
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        // 先查询原菜品信息获取分类ID
        Dish originalDish = dishMapper.getById(id);
        
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();
        dishMapper.update(dish);
        
        // 清理相关分类的缓存
        if (originalDish != null) {
            cleanDishCache(originalDish.getCategoryId());
        }
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @Override
    public List<Dish> list(Long categoryId) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return dishMapper.list(dish);
    }

    /**
     * 条件查询菜品和口味（带Redis缓存）
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        // 构造缓存键，遵循命名规范：dish_分类ID_状态
        String cacheKey = CacheConstant.DISH_CACHE_PREFIX + dish.getCategoryId() + "_" + dish.getStatus();
        
        // 先尝试从Redis缓存中获取数据
        List<DishVO> cachedList = (List<DishVO>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedList != null && !cachedList.isEmpty()) {
            log.info("从Redis缓存中获取菜品数据，缓存键: {}", cacheKey);
            return cachedList;
        }
        
        // 缓存未命中，查询数据库
        log.info("Redis缓存未命中，从数据库查询菜品数据，缓存键: {}", cacheKey);
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }
        
        // 将查询结果写入Redis缓存，设置30分钟过期时间
        if (!dishVOList.isEmpty()) {
            redisTemplate.opsForValue().set(cacheKey, dishVOList, CacheConstant.DISH_CACHE_EXPIRE, TimeUnit.MINUTES);
            log.info("将菜品数据存入Redis缓存，缓存键: {}，过期时间: {}分钟", cacheKey, CacheConstant.DISH_CACHE_EXPIRE);
        }

        return dishVOList;
    }

    /**
     * 清理菜品缓存
     * @param categoryId 分类ID，null表示清理所有菜品缓存
     */
    @Override
    public void cleanDishCache(Long categoryId) {
        if (categoryId != null) {
            // 清理指定分类的缓存
            String pattern = CacheConstant.DISH_CACHE_PREFIX + categoryId + ":*";
            redisTemplate.delete(redisTemplate.keys(pattern));
            log.info("清理分类ID为{}的菜品缓存", categoryId);
        } else {
            // 清理所有菜品缓存
            String pattern = CacheConstant.DISH_CACHE_PREFIX + "*";
            redisTemplate.delete(redisTemplate.keys(pattern));
            log.info("清理所有菜品缓存");
        }
    }
}