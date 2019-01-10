package com.marsclub.validator;

import com.marsclub.validator.inf.ValidateUnit;

import java.util.HashMap;
import java.util.Map;

/**
 * 校验器总入口
 * Created by dujj on 2018/3/10.
 */
public final class Validators {

    private static final Map<Class<?>, ValidateTemplate> VALIDATE_UNIT_MAP = new HashMap<>();

    /**
     * 根据前端传入的JSON格式数据，和类型Class，创建校验单元
     */
    @SuppressWarnings("unchecked")
    public static <K> ValidateTemplate<K> defineValidateTemplate(Class<K> cla) {
        ValidateTemplate<K> template;
        if (VALIDATE_UNIT_MAP.containsKey(cla)) {
            template = (ValidateTemplate<K>) VALIDATE_UNIT_MAP.get(cla);
        } else {
            template = new ValidateTemplate(cla);
            VALIDATE_UNIT_MAP.put(cla, template);
        }
        return template;
    }

    /**
     * 通过校验模板信息生成校验单元
     */
    @SuppressWarnings("unchecked")
    public static <K> ValidateUnit<K> createValidateUnitFromTemplate(Class<K> cla) {
        if (!VALIDATE_UNIT_MAP.containsKey(cla)) {
            throw new VerifyException(String.format("The template fo class:[%s] hasn't been initialized.", cla.getName()));
        }
        ValidateTemplate<K> template = VALIDATE_UNIT_MAP.get(cla);
        return template.generateValidateUnit();
    }
}
