package com.marsclub.validator;

/**
 * 校验异常
 * Created by dujj on 2019/1/10.
 */
public class VerifyException extends RuntimeException{

    public VerifyException(String message){
        super(message);
    }

    public VerifyException(String message, Throwable throwable){
        super(message, throwable);
    }
}
