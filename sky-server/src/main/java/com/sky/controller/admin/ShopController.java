package com.sky.controller.admin;

import com.sky.constant.ShopConstant;
import com.sky.result.Result;
import com.sky.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 店铺营业状态管理
 */
@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Api(tags = "店铺营业状态管理")
@Slf4j
public class ShopController {

    @Autowired
    private ShopService shopService;

    /**
     * 设置店铺营业状态
     * @param status 营业状态 1-营业中 0-打烊
     * @return
     */
    @PutMapping("/{status}")
    @ApiOperation("设置店铺营业状态")
    public Result setShopStatus(@PathVariable Integer status) {
        log.info("设置店铺营业状态: {}", status);
        shopService.setShopStatus(status);
        return Result.success();
    }

    /**
     * 获取店铺营业状态
     * @return
     */
    @GetMapping("/status")
    @ApiOperation("获取店铺营业状态")
    public Result<Integer> getShopStatus() {
        log.info("获取店铺营业状态");
        Integer status = shopService.getShopStatus();
        return Result.success(status);
    }
}