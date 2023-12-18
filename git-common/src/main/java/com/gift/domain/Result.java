package com.gift.domain;

import lombok.Data;

/**
 * 后端返回结果
 */
@Data
public class Result {
    private Boolean flag;
    private Object object;
    private String message;

    public Result() {
    }

    public Result(Boolean flag) {
        this.flag = flag;
    }

    public Result(Boolean flag, Object object) {
        this.flag = flag;
        this.object = object;
    }

    public Result(Boolean flag, String message) {
        this.flag = flag;
        this.message = message;
    }

    public Result(Boolean flag, Object object, String message) {
        this.flag = flag;
        this.object = object;
        this.message = message;
    }
}
