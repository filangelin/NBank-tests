package common.annotations;

public @interface AccountSpec {

    int user() default 1;

    int count() default 1;
}

