package com.marsclub.validator.inf;

import com.alibaba.fastjson.JSONObject;
import com.marsclub.validator.VerifyException;

/**
 * 表示单条校验规则
 * Created by dujj on 2018/1/23.
 */
public interface Rule<T>{

    /**
     * 对单条规则进行校验，如果校验不通过，则直接抛出异常
     */
    T verify(T value, JSONObject context) throws VerifyException;
}
