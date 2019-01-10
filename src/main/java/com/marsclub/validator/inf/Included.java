package com.marsclub.validator.inf;

/**
 * Included模式
 * Created by dujj on 2018/1/25.
 */
public interface Included<K> {

    /**
     * 调用此接口，将会只校验参数列中传入的域
     */
    Included<K> include(String... fieldNames);

    /**
     * 离开Included模式
     */
    ValidateUnit<K> endIncluding();
}
