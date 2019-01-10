package com.marsclub.validator;

import com.alibaba.fastjson.JSONObject;
import com.marsclub.validator.inf.Acceptable;
import com.marsclub.validator.inf.FieldUnit;
import com.marsclub.validator.inf.Rule;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 值单元默认抽象实现
 * Created by dujj on 2018/1/23.
 */
public class DefaultFieldUnit<K,T> implements FieldUnit<K, T> {

    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private List<Rule<T>> rules = new ArrayList<>();

    private final String fieldName;
    private final ValidateTemplate<K> validateTemplate;
    private final Class<T> token;
    private final Method setter;

    private Acceptable<T> acceptable = new InnerAcceptable();

    DefaultFieldUnit(String fieldName, Class<K> toCla, Class<T> token, ValidateTemplate<K> validateTemplate){
        this.fieldName = fieldName;
        this.validateTemplate = validateTemplate;
        this.token = token;
        StringBuilder setterName = new StringBuilder("set");
        //获取对应token的setter方法
        boolean nextUpper = true;
        for(char c : fieldName.toCharArray()){
            if(nextUpper){
                int upper = (int)c;
                if(upper >= 97 && upper <= 122){
                    upper -= 32;
                }
                setterName.append((char) upper);
                nextUpper = false;
            }else{
                if(c == '_'){
                    nextUpper = true;
                }else{
                    setterName.append(c);
                }
            }
        }
        try {
            setter = toCla.getMethod(setterName.toString(), token);
        } catch (NoSuchMethodException e) {
            throw new VerifyException("参数[" + fieldName + "]缺少setter");
        }
    }

    @Override
    public FieldUnit<K, T> notNull(String errorMessage){
        rules.add((T value, JSONObject context) -> {
            if(value == null
                    || (value.getClass().equals(String.class) && value.toString().trim().equals(""))){
                throw new VerifyException(errorMessage);
            }
            return value;
        });
        return this;
    }

    @Override
    public FieldUnit<K, T> in(final List<T> list, String errorMessage){
        rules.add((value, context) -> {
            for(T exist : list){
                if(exist.equals(value)){
                    return value;
                }
            }
            throw new VerifyException(errorMessage);
        });
        return this;
    }

    @Override
    public FieldUnit<K, T> greaterThan(T another, String errorMessage) {
        rules.add((T value, JSONObject context) -> {
            @SuppressWarnings("unchecked")
            Comparable<T> ca = (Comparable<T>) value;
            if(ca.compareTo(another) <= 0){
                throw new VerifyException(errorMessage);
            }
            return value;
        });
        return this;
    }

    @Override
    public FieldUnit<K, T> lessThan(T another, String errorMessage) {
        rules.add((T value, JSONObject context) -> {
            @SuppressWarnings("unchecked")
            Comparable<T> ca = (Comparable<T>) value;
            if(ca.compareTo(another) > 0){
                throw new VerifyException(errorMessage);
            }
            return value;
        });
        return this;
    }

    @Override
    public FieldUnit<K, T> pattern(String pattern, String errorMessage) {
        final Pattern patternRex = Pattern.compile(pattern);
        rules.add((T value, JSONObject context) -> {
            if(value == null || !(value instanceof String)){
                throw new VerifyException("值为null或者并非字符串格式，无法经过正则校验");
            }
            String strValue = value.toString();
            if(!patternRex.matcher(strValue).find()){
                throw new VerifyException(errorMessage);
            }
            return value;
        });
        return this;
    }

    @Override
    public FieldUnit<K, T> rangeIn(T min, T max, String errorMessage) {
        rules.add((T value, JSONObject context) -> {
            @SuppressWarnings("unchecked")
            Comparable<T> ca = (Comparable<T>) value;
            if(ca.compareTo(min) <0 || ca.compareTo(max) > 0){
                throw new VerifyException(errorMessage);
            }
            return value;
        });
        return this;
    }

    @Override
    public FieldUnit<K, T> defaultable(T defaultValue) {
        rules.add((T value, JSONObject context) ->
            value == null ? defaultValue : value
        );
        return this;
    }

    @Override
    public FieldUnit<K, T> probable() {
        rules.add((T value, JSONObject context) -> value);
        return this;
    }

    @Override
    public ValidateTemplate<K> endUnit() {
        return validateTemplate;
    }

    @Override
    public Acceptable<T> customize() {
        return acceptable;
    }

    @Override
    public void validate(K entity, JSONObject context, boolean probable) {
        T value = getValue(context, probable);
        //校验通过，则填充值
        try {
            setter.invoke(entity, value);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    private T getValue(JSONObject context, boolean probable){
        T value = getValueFromContext(context);
        if(!probable){ //非Probable模式，进行参数校验，否则直接值填充
            for(Rule<T> rule : rules){
                value = rule.verify(value, context);
            }
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    private T getValueFromContext(JSONObject context){
        Object value;
        try{
            if(token.equals(Long.class)){
                value = context.getLong(fieldName);
            }else if(token.equals(Integer.class) || token.equals(int.class)){
                value = context.getInteger(fieldName);
            }else if(token.equals(String.class)){
                value = context.getString(fieldName);
            }else if(token.equals(JSONObject.class)){
                value = context.getJSONObject(fieldName);
            }else if(token.equals(Date.class)){
                value = context.getString(fieldName);
                value = DATE_TIME_FORMAT.parse(value.toString());
            }else if(token.equals(Boolean.class)){
                value = context.getBoolean(fieldName);
            }else{
                throw new VerifyException("转换未定义");
            }
        } catch(NumberFormatException e){
            throw new VerifyException("参数[" + fieldName + "]的类型不合法，无法转换");
        } catch(ParseException e){
            value = null;
        }
        return (T) value;
    }

    class InnerAcceptable implements Acceptable<T> {

        @Override
        public Acceptable<T> anotherRule(Rule<T> rule) {
            if(rule != null){
                rules.add(rule);
            }
            return this;
        }

        @Override
        public FieldUnit<?, T> end() {
            return DefaultFieldUnit.this;
        }
    }
}
