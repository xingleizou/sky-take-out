package com.sky.controller.user;

import com.sky.constant.ShopConstant;
import com.sky.result.Result;
import com.sky.service.ShopService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户端-店铺营业状态管理
 */
@RestController("userShopController")
@RequestMapping("/user/shop")
@Api(tags = "用户端-店铺营业状态管理")
@Slf4j
public class ShopController {

    @Autowired
    private ShopService shopService;

    /**
     * 获取店铺营业状态
     * @return
     */
    @GetMapping("/{status}")
    @ApiOperation("获取店铺营业状态")
    public Result<Integer> getShopStatus() {
        log.info("用户端获取店铺营业状态");
        Integer status = shopService.getShopStatus();
        return Result.success(status);
    }
}