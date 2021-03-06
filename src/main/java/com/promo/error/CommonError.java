package com.promo.error;

/**
 * 错误提示的通用结构
 */
public interface CommonError {

    public int getErrCode();

    public String getErrMsg();
    // 提供自定义错误信息描述方式
    public CommonError setErrMsg(String errMsg);
}
