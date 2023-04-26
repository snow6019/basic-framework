package com.lxzh.basic.framework.modular.enums;


import com.lxzh.basic.framework.core.exception.enums.abs.AbstractBaseExceptionEnum;

public enum ServiceExceptionEnum implements AbstractBaseExceptionEnum {

    /**
     * 员工
     */
    EMPLOYEE_ACCOUNT_EXISTS(1000, "员工账号已存在！"),
    ;


    private String message;
    private Integer code;

    ServiceExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
