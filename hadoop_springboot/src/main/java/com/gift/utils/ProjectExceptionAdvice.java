package com.gift.utils;

import com.gift.domain.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProjectExceptionAdvice {

    //拦截所有异常信息
    @ExceptionHandler
    public Result doException(Exception ex){
        ex.printStackTrace();
        return new Result(false,"Gift服务器故障!请重试");
    }
}
