package com.gift.domain;

import lombok.Data;

/**
 * 后端返回结果
 */
@Data
public class R {
    private Boolean flag;
    private Object object;
    private String message;

    public R() {
    }

    public R(Boolean flag) {
        this.flag = flag;
    }

    public R(Boolean flag, Object object) {
        this.flag = flag;
        this.object = object;
    }

    public R(Boolean flag, String message) {
        this.flag = flag;
        this.message = message;
    }

    public R(Boolean flag, Object object, String message) {
        this.flag = flag;
        this.object = object;
        this.message = message;
    }
}
