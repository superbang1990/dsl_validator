package com.marsclub.validator.inf;

/**
 * 可以接受自定义规则的接口
 * Created by dujj on 2018/1/24.
 */
public interface Acceptable<T> {

    /**
     * 接收用户自定义的校验规则
     */
    Acceptable<T> anotherRule(Rule<T> rule);

    /**
     * 自定义校验规则结束
     * @return
     */
    FieldUnit<?, T> end();
}
