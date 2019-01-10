package com.marsclub.validator.inf;

import com.alibaba.fastjson.JSONObject;
import com.marsclub.validator.VerifyException;

/**
 * 校验单元执行接口
 * Created by dujj on 2018/1/24.
 */
public interface ValidateUnit<K> {

    /**
     * 执行校验过程，并生成对应的实例
     */
    K validateAndGet(JSONObject context) throws VerifyException;

    /**
     * 允许将无需校验的域剔除
     */
    Excluded<K> withExcluding();

    /**
     * 允许重新定义需要检验的域，该操作将会清空定义模板时的校验域
     */
    Included<K> withIncluding();

    /**
     * 允许定义域为简单的值注入，不进行任何校验，只要参数不为NULL，并且类型匹配，即注入到实体中
     */
    Probable<K> withProbable();

}