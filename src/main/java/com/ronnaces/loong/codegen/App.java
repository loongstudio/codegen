package com.ronnaces.loong.codegen;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * App
 *
 * @author Galen Luo
 * @version 1.0.0
 * @since 2022/6/26 8:30
 */
@ComponentScan("com.ronnaces")
public class App {

    public static ApplicationContext applicationContext;

    public static void main(String[] args) {
        applicationContext = new AnnotationConfigApplicationContext(App.class);
        CodegenApplication.main(args);
    }

}
