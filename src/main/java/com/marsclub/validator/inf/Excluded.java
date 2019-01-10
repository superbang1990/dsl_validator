package com.marsclub.validator.inf;

/**
 * 值校验Excluded模式
 * Created by dujj on 2018/1/25.
 */
public interface Excluded<K> {

    /**
     * 调用此接口，将会把参数列表中定义的域名称排除在本次校验过程中
     */
    Excluded<K> exclude(String... fieldNames);

    /**
     * 离开Exclude模式
     */
    ValidateUnit<K> endExcluding();
}