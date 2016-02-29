package com.tiza.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Description: SpringUtil
 * Author: DIYILIU
 * Update: 2015-09-22 15:26
 */
public class SpringUtil {

    private static ApplicationContext applicationContext;

    static {

        applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
    }

    public static Object getBean(String name){

        return applicationContext.getBean(name);
    }
}
