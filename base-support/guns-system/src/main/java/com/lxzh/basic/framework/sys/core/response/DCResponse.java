package com.lxzh.basic.framework.sys.core.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

/**
 * restful 接口返回信息的实体类
 * @param <T>
 */
@ApiModel(description = "服务器端返回结果")
public class DCResponse<T> {
    public static final String DEFAULT_SUCCESS_MESSAGE = "请求成功";

    public static final String DEFAULT_ERROR_MESSAGE = "网络异常";

    public static final Integer DEFAULT_SUCCESS_CODE = 200;

    public static final Integer DEFAULT_ERROR_CODE = 500;

    private T data;
    @ApiModelProperty(value = "额外信息")
    private Map<String,String> metaData ;
    @ApiModelProperty(value = "代码",name = "code",example = "200")
    private int code;

    @ApiModelProperty(value = "描述",name = "message",example = "成功")
    private String message;

    private DCResponse() {
    }

    private DCResponse(ResponseBuilder<T> builder) {
        this.data = builder.result;
        this.code = builder.code;
        this.message = builder.msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public Map<String, String> getMetaData() {
        return metaData;
    }

    public void setMetaData(Map<String, String> metaData) {
        this.metaData = metaData;
    }

    /**
     *
     * @param result
     * @param <T>
     * @return
     */
    public static <T> DCResponse<T> success(T result){
        return new ResponseBuilder(result)
                .code(DEFAULT_SUCCESS_CODE)
                .build();
    }

    public static DCResponse error(int code, String message) {
        return new ResponseBuilder("")
                .code(code)
                .msg(message)
                .build();
    }

    public static class ResponseBuilder<T> {
        private T result;
        private int  code;

        private String msg;

        public ResponseBuilder() {
            this.result = null;
        }

        public ResponseBuilder(T result) {
            this.result = result;
        }

        public ResponseBuilder code(int code) {
            this.code = code;
            if(DEFAULT_SUCCESS_CODE == code){
                this.msg = "操作成功";
            }else{
                this.msg = "操作失败";
            }
            return this;
        }

        public ResponseBuilder result(T result) {
            this.result = result;
            return this;
        }

        public ResponseBuilder msg(String msg) {
            this.msg = msg;
            return this;
        }

        public DCResponse<T> build() {
            return new DCResponse(this);
        }
    }
}
