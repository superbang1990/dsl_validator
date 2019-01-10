# dsl_validator
just a simple validator framework by DSL.

Let's see some examples. We can use this simple framework for simple testing because it is just a demo at this monent.
We can first define a validate template like this:

ValidateTemplate<User> user = Validators.defineValidateTemplate(User.class);
user.createFieldUnit("login_name", String.class).notNull("登录名不可为空").endUnit()
    .createFieldUnit("phone_number", String.class).notNull("密码不可为空").endUnit()
    .createFieldUnit("age", String.class).defaultable("").endUnit()
    .createFieldUnit("user_note", String.class).defaultable("").endUnit();
    
And, then go with:
user.validateAndGet();
