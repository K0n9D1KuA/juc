package com.qyh.juc.demo.模拟实现原子类;

import lombok.Data;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UnsafeDemo {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        //首先获得Unsafe对象
        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Unsafe unsafe = (Unsafe) theUnsafe.get(null);
        System.out.println(unsafe);
        //获取域的偏移地址
        long idOffset = unsafe.objectFieldOffset(Teacher.class.getDeclaredField("id"));
        long nameOffset = unsafe.objectFieldOffset(Teacher.class.getDeclaredField("name"));
        Teacher teacher = new Teacher();
        unsafe.compareAndSwapInt(teacher, idOffset, 0, 1);
        unsafe.compareAndSwapObject(teacher, nameOffset, null, "张三");
        //打印对象
        System.out.println(teacher);
    }
}

@Data
class Teacher {
    volatile int id;
    volatile String name;
}


