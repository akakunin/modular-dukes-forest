package com.forest;

import org.wso2.msf4j.spring.MSF4JSpringApplication;

/** This Application Class placed into com.forest 
 * since msf4j performs autoscan only for package (and subpackages) from application class
 * 
 * @author akakunin
 *
 */
public class Application {
    public static void main(String[] args) {
        MSF4JSpringApplication.run(Application.class, args);
    }
}