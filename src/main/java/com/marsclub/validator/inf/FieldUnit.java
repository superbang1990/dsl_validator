package com.marsclub.validator.inf;

import com.alibaba.fastjson.JSONObject;
import com.marsclub.validator.ValidateTemplate;

import java.util.List;

/**
 * 域单元
 * Created by dujj on 2018/1/23.
 */
public interface FieldUnit<K,T> {

    /**
     * 非空判断
     */
    FieldUnit<K, T> notNull(String errorMessage);

    /**
     * 最大值比较
     */
    FieldUnit<K, T> in(List<T> list, String errorMessage);

    /**
     * 最大值比较
     */
    FieldUnit<K, T> greaterThan(T another, String errorMessage);

    /**
     * 最小值比较
     */
    FieldUnit<K, T> lessThan(T another, String errorMessage);

    /**
     * 值必须符合正则表达式
     */
    FieldUnit<K, T> pattern(String pattern, String errorMessage);

    /**
     * 区间比较
     * @param min 最小值
     * @param max 最大值
     */
    FieldUnit<K, T> rangeIn(T min, T max, String errorMessage);

    /**
     * 如果该字段为空，则赋予默认值
     */
    FieldUnit<K, T> defaultable(T defaultValue);

    /**
     * 若有值，则赋值，否则跳过
     */
    FieldUnit<K, T> probable();

    /**
     * 结束字段定义
     */
    ValidateTemplate<K> endUnit();

    /**
     * 允许用户提交自定义校验规则
     */
    Acceptable<T> customize();

    /**
     * 单节字段校验
     */
    void validate(K entity, JSONObject context, boolean probable);

    /**
     * 返回本字段单元的名称
     */
    String getFieldName();
}
