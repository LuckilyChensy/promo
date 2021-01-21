package com.promo.error;

public class BusinessException extends Exception implements CommonError{

    private CommonError commonError;

    //直接接受  EmBusinessError 的传参用于构造业务异常
    public BusinessException(CommonError commonError) {
        super();
        this.commonError = commonError;
    }

    //提供构建自定义异常的方式
    public BusinessException(CommonError commonError, String errMsg) {
        super();
        this.commonError = commonError;
        this.commonError.setErrMsg(errMsg);
    }

    @Override
    public int getErrCode() {
        return 0;
    }

    @Override
    public String getErrMsg() {
        return null;
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        return null;
    }
}
