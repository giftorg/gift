package org.giftorg.backed.entity;

import lombok.Data;

/**
 * 后端返回结果
 */
@Data
public class Response {
    private Integer code;
    private String msg;
    private Object data;

    public Response() {
    }

    public Response(Object data) {
        this.code = 0;
        this.msg = "success";
        this.data = data;
    }

    public Response(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Response(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
}
