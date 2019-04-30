package com.lx;

/**
 * @author: luoXiong
 * @date: 2019/4/29 20:17
 * @version: 1.0
 * @desc:
 */
public class Student {
    public String name;
    public int age;

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public Student(String name) {
        this.name = name;
    }

    public Student(int age) {
        this.age = age;
    }
}
