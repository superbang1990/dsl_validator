package com.marsclub.validator;

import com.alibaba.fastjson.JSONObject;
import com.marsclub.validator.inf.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 校验模板，存储了一个类中定义的字段的校验规则
 * Created by dujj on 2018/1/23.
 */
public class ValidateTemplate<K> {

    private final Map<String, FieldUnit<K, ?>> FIELD_UNIT_MAP = new HashMap<>();

    private final Class<K> cla;

    //此处的cla指POJO
    public ValidateTemplate(Class<K> cla){
        this.cla = cla;
    }

    /**
     * 生成新的字段验证单元
     */
    public <T> FieldUnit<K, T> createFieldUnit(String field, Class<T> token){
        FieldUnit<K, T> fieldUnit = new DefaultFieldUnit<>(field, cla, token, this);
        FIELD_UNIT_MAP.put(field, fieldUnit);
        return fieldUnit;
    }

    /**
     * 根据用户JSON数据，生成校验单元
     */
    public ValidateUnit<K> generateValidateUnit(){
        //根据requestJSON创建上下文
        return new DefaultValidateUnit();
    }

    /**
     * 校验单元
     */
    public class DefaultValidateUnit implements ValidateUnit<K>{

        private final Logger logger = Logger.getLogger("validators", "validate_unit");

        private final Map<String, FieldUnit<K, ?>> fieldUnitMap;
        private K entity;
        private LinkedList<FieldStrategy> fields = new LinkedList<>(); //校验域

        private Excluded<K> excluded;
        private Included<K> included;
        private Probable<K> probable;

        DefaultValidateUnit(){
            this.fieldUnitMap = Collections.unmodifiableMap(ValidateTemplate.this.FIELD_UNIT_MAP);
            for(String key : fieldUnitMap.keySet()){
                fields.add(new FieldStrategy(key)); //默认将所有字段列入校验域
            }
        }

        /**
         * 执行校验过程，并生成对应的实例
         */
        public K validateAndGet(JSONObject context) throws VerifyException {
            FieldUnit<K, ?> fieldUnit;
            try {
                this.entity = cla.newInstance();
            } catch (Exception e) {
                logger.log(Level.WARNING, e.getMessage());
                throw new VerifyException("validator配置错误");
            }
            for(FieldStrategy fs : fields){
                fieldUnit = fieldUnitMap.get(fs.fieldName); //拿到fieldUnit
                fieldUnit.validate(entity, context, fs.probable);
            }
            return entity;
        }

        @Override
        public Excluded<K> withExcluding() {
            if(excluded == null){
                excluded = new Excluded<K>() {

                    @Override
                    public Excluded<K> exclude(String... fieldNames) {
                        if(fieldNames != null && fieldNames.length > 0){
                            for(String excludedField : fieldNames){
                                for(int i = 0; i < fields.size(); i ++){
                                    FieldStrategy fs = fields.get(i);
                                    if(fs.equals(excludedField)){
                                        fields.remove(i); break; //将该域从校验域中排除掉
                                    }
                                }
                            }
                        }
                        return this;
                    }

                    @Override
                    public ValidateUnit<K> endExcluding() {
                        return DefaultValidateUnit.this;
                    }
                };
            }
            return excluded;
        }

        @Override
        public Included<K> withIncluding() {
            fields.clear(); //排除掉所有的校验域
            if(included == null){
                included = new Included<K>() {

                    @Override
                    public Included<K> include(String... fieldNames) {
                        if(fieldNames != null && fieldNames.length > 0){
                            for(String includedField : fieldNames){
                                fields.add(new FieldStrategy(includedField)); //将待校验域加入到fields中
                            }
                        }
                        return this;
                    }

                    @Override
                    public ValidateUnit<K> endIncluding() {
                        return DefaultValidateUnit.this;
                    }
                };
            }
            return included;
        }

        @Override
        public Probable<K> withProbable() {
            if(probable == null){
                probable = new Probable<K>() {

                    @Override
                    public Probable<K> probable(String... fieldNames) {
                        if(fieldNames != null && fieldNames.length > 0){
                            for(String probableField : fieldNames){
                                for(FieldStrategy fs : fields){
                                    if(fs.equals(probableField)){
                                        fs.probable = true; break; //更改probable属性
                                    }
                                }
                                fields.add(new FieldStrategy(probableField, true));
                            }
                        }
                        return this;
                    }

                    @Override
                    public ValidateUnit<K> endProbable() {
                        return DefaultValidateUnit.this;
                    }
                };
            }
            return probable;
        }
    }

    private static class FieldStrategy{

        String fieldName;
        boolean probable;

        FieldStrategy(String fieldName){
            this(fieldName, false);
        }

        FieldStrategy(String fieldName, boolean probable){
            this.fieldName = fieldName;
            this.probable = probable;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == null || this.fieldName == null){
                return false;
            }
            String fieldName;
            if(obj instanceof FieldStrategy){
                FieldStrategy other = (FieldStrategy) obj;
                fieldName = other.fieldName;
            }else if(obj instanceof String){
                fieldName = (String) obj;
            }else{
                return false;
            }
            return this.fieldName.equals(fieldName);
        }
    }
}
