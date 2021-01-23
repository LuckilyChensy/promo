package com.promo.service;

import com.promo.error.BusinessException;
import com.promo.service.model.OrderModel;

public interface OrderService {
    OrderModel createOrder(Integer userId,Integer itemId,Integer promoId,Integer amount,String stockLogId) throws BusinessException;
}
