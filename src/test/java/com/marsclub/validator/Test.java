package com.marsclub.validator;

/**
 * 测试用例
 * Created by dujj on 2019/1/10.
 */
public class Test {

    @org.junit.Test
    public void test() throws Exception{
        ValidateTemplate<User> user = Validators.defineValidateTemplate(User.class);
        user.createFieldUnit("login_name", String.class).notNull("登录名不可为空").endUnit()
                .createFieldUnit("phone_number", String.class).notNull("密码不可为空").endUnit()
                .createFieldUnit("age", String.class).defaultable("").endUnit()
                .createFieldUnit("user_note", String.class).defaultable("").endUnit();
    }
}
