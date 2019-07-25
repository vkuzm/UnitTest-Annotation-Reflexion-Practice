package com.example.TestUnitAnnomationReflaction;

import com.example.TestUnitAnnomationReflaction.annotations.*;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;


public class TestRunner {

    /**
     * Run Tests
     * @param packageName Test folder package name
     */
    public static void runTests(String packageName) {
        try {
            Class[] classes = getClasses(packageName);

            for (Class<?> c : classes) {

                if (c.isAnnotationPresent(TestEnabled.class)) {
                    Object o = null;

                    try {
                        o = c.getConstructor().newInstance();
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                            | NoSuchMethodException e) {

                        e.printStackTrace();
                    }

                    if (o != null) {
                        invokeOnceInClass(c, o, BeforeClass.class);
                        invokeMethods(o, c.getMethods(), Test.class);
                        invokeOnceInClass(c, o, AfterClass.class);
                    }
                }
            }

        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void invokeOnceInClass(Class<?> c, Object o, Class<? extends Annotation> annotationClass) {
        Method method = getMethodByAnnotation(c, annotationClass);
        if (method != null) {
            invokeMethod(o, method);
        }
    }

    private static Method getMethodByAnnotation(Class<?> c, Class<? extends Annotation> annotationClass) {
        Method[] methods = c.getMethods();

        for (Method method : methods) {
            if (method.isAnnotationPresent(annotationClass)) {
                return method;
            }
        }

        return null;
    }

    private static void invokeMethods(Object o, Method[] methods, Class<? extends Annotation> annotationClass) {
        for (Method method : methods) {
            if (annotationClass.equals(Test.class) && method.isAnnotationPresent(Test.class)) {
                invokeMethods(o, methods, Before.class);
                invokeMethod(o, method);
                invokeMethods(o, methods, After.class);

            } else if (method.isAnnotationPresent(annotationClass)) {
                invokeMethod(o, method);
            }
        }
    }

    private static void invokeMethod(Object o, Method method) {
        try {
            method.invoke(o);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private static Class[] getClasses(String packageName)
            throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

}