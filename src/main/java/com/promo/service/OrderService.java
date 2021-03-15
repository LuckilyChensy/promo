package com.promo.service;

import com.promo.error.BusinessException;
import com.promo.service.model.OrderModel;

public interface OrderService {
    // 提供指定用户与商品，活动，库存之间的关系
    OrderModel createOrder(Integer userId,Integer itemId,Integer promoId,Integer amount,String stockLogId) throws BusinessException;
}
