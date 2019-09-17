package com.littlecity.server.entity;

import lombok.Builder;
import lombok.Data;

/**
 * @author huangxiaocheng
 * @Date 2019/9/16
 **/
@Data
@Builder
public class RespResult {

    private String code;

    private String message;

    private Object data;

    public static RespResult ok(){
        RespResult respResult = RespResult.builder()
                .code("0")
                .message("success")
                .data(null)
                .build();

        return respResult;
    }

    public static RespResult of(Object data){
        RespResult success = RespResult.builder().code("0")
                .message("success")
                .data(data)
                .build();

        return success;
    }

    public static RespResult fail(String code, String message, Object data){
        RespResult fail = RespResult.builder().code(code)
                .message(message)
                .data(data)
                .build();
        return fail;
    }
}
