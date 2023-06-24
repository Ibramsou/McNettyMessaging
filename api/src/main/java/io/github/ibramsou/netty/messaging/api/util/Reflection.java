package io.github.ibramsou.netty.messaging.api.util;

import java.lang.reflect.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Reflection {

    private static final Field modifiers;

    static {
        String version = System.getProperty("java.version");
        final String[] parts = version.split("\\.");
        int javaVersion = Integer.parseInt(parts[0].equals("1") ? parts[1] : parts[0]);
        if (javaVersion > 8) {
            modifiers = null;
        } else {
            try {
                modifiers = Field.class.getDeclaredField("modifiers");
                modifiers.setAccessible(true);
            } catch (NoSuchFieldException e) {
                throw new IllegalArgumentException("Cannot access modifiers field", e);
            }
        }
    }

    public static Class<?> forName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Cannot find class named '" + className + "'", e);
        }
    }

    public static Field access(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            if (modifiers != null && Modifier.isFinal(field.getModifiers())) {
                modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            }
            return field;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException("Cannot access field named '" + fieldName + "'", e);
        }
    }

    public static void setAccessible(Field field) {
        try {
            field.setAccessible(true);
            if (modifiers != null && Modifier.isFinal(field.getModifiers())) {
                modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            }
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Cannot access field named '" + field.getName() + "'", e);
        }
    }

    public static Method accessMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Cannot access method named '" + methodName + "'", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T invoke(Method method, Object instance, Object... objects) {
        try {
            return (T) method.invoke(instance, objects);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("Cannot invoke method named '" + method.getName() + "'", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(Field field, Object instance) {
        try {
            return (T) field.get(instance);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Cannot get instance of field '" + field.getName() + "'", e);
        }
    }

    public static <T> T set(Field field, Object instance, T value) {
        try {
            field.set(instance, value);
            return value;
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Cannot set instance of field '" + field.getName() + "'", e);
        }
    }

    public static <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>... parameterTypes) {
        try {
            return clazz.getConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Cannot get constructor for class named '" + clazz.getName() + "'");
        }
    }

    public static <T> T newInstance(Class<T> clazz, Constructor<T> constructor, Object... objects) {
        try {
            return constructor.newInstance(objects);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("Cannot create new instance of class '" + clazz.getName() + "'", e);
        }
    }

    public static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException("Cannot create new instance of class '" + clazz.getName() + "'", e);
        }
    }

    public static void listFields(Class<?> clazz, Predicate<Field> predicate, Consumer<Field> consumer) {
        for (Field declaredField : clazz.getDeclaredFields()) {
            if (predicate.test(declaredField)) consumer.accept(declaredField);
        }
    }

    public static void listMethods(Class<?> clazz, Predicate<Method> predicate, Consumer<Method> consumer) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (predicate.test(method)) consumer.accept(method);
        }
    }
}
