package com.marsclub.validator.inf;

/**
 * Probable模式
 * Created by dujj on 2018/1/25.
 */
public interface Probable<K> {

    /**
     * 调用此接口，将会只做值的提取，不再进行强制校验
     */
    Probable<K> probable(String... fieldNames);

    /**
     * 离开Probable模式
     */
    ValidateUnit<K> endProbable();
}
