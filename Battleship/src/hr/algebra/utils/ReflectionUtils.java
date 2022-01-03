/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hr.algebra.utils;

import hr.algebra.controller.GameViewController;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author Nina
 */
public class ReflectionUtils {
    
    private static final String FILE_SEPARATOR = "\\";
    
    public static void readAllFromSourcePackage(String packageLocation,String subPackageLocation, StringBuilder classInfo) {
        String[] packages = new File(packageLocation).list();

        for (String packageName : packages) {

            String[] packageChildren = new File(packageLocation + FILE_SEPARATOR + packageName).list();
            if (!packageName.contains(".")) {
                classInfo
                    .append("<!DOCTYPE html>\n")
                    .append("<html>\n")
                    .append("<head>\n")
                    .append("<style type = \"text/css\"> h1,h2,h3 {display: inline;}</style>")
                    .append("<title>Documentation"
                            + "</title>\n")
                    .append("<br/>")
                    .append("</head>\n")
                    .append("<body>\n")
                    .append("<br/>")
                    .append("<h1 style=\"color: PaleVioletRed;\">")
                    .append(subPackageLocation.equals("") ? "package " + packageName : "package " + subPackageLocation + "." + packageName)
                    .append("</h1>")
                    .append("<br/>");
            }
            readAllFromClass(packageLocation, packageName, subPackageLocation, classInfo, packageChildren);
        }
    }

    private static void readAllFromClass(String packageLocation, String packageName, String subPackageLocation, StringBuilder classInfo, String[] packageChildren) {
        String[] classes = new File(packageLocation + FILE_SEPARATOR
                + packageName).list();
        if (classes != null) {
            
            for (String className : classes) {
                
                if (className.endsWith(".java") == false) {
                    continue;
                }
                try {
                    Class c = Class.forName(
                            subPackageLocation + "." + packageName + "."
                                    + className.substring(0, className.indexOf(".")));
                    classInfo
                        .append("<br/>")
                        .append("<br/>")
                        .append("<h2 style=\"color: SteelBlue;\">")
                        .append(className)
                        .append("</h2>")
                        .append("<br/>");
                    readClassAndMembersInfo(c, classInfo);
                    
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(GameViewController.class.getName()).log(
                            Level.SEVERE, null, ex);
                }
            }
        }
        if (packageChildren != null) {
            readAllFromSourcePackage(packageLocation + FILE_SEPARATOR + packageName, "".equals(subPackageLocation) ? packageName : subPackageLocation + "." + packageName, classInfo);
        }
    }
    
    public static void readClassInfo(Class<?> clazz, StringBuilder classInfo) {
        //appendPackage(clazz, classInfo);
        // we can't find imports because the compiler doesn't put them into the object file
        // import is just a shorthand to the compiler    appendModifiers(clazz, classInfo);
        appendModifiers(clazz, classInfo);
        classInfo
                .append(" ")
                .append("<h2>")
                .append(clazz.getSimpleName())
                .append("</h2>")
                .append(" ");
        appendParent(clazz, classInfo, true);
        appendInterfaces(clazz, classInfo);
    }

    private static void appendPackage(Class<?> clazz, StringBuilder classInfo) {
        classInfo
                .append("<h1>")
                .append(clazz.getPackage())
                .append("</h1>")
                .append("<br/>");
    }

    private static void appendModifiers(Class<?> clazz, StringBuilder classInfo) {
        classInfo
                .append("<h2>")
                .append(Modifier.toString(clazz.getModifiers()))
                .append("</h2>");
    }

    private static void appendParent(Class<?> clazz, StringBuilder classInfo, boolean first) {
        Class<?> parent = clazz.getSuperclass();
        if (parent == null) {
            return;
        }
        if (first) {
            classInfo
                    .append("<h2>")
                    .append(" extends")
                    .append("</h2>");
        }
        classInfo
                .append("<h2>")
                .append(" ")
                .append(parent.getSimpleName())
                .append("</h2>")
                .append(" ");
        appendParent(parent, classInfo, false);
    }

    private static void appendInterfaces(Class<?> clazz, StringBuilder classInfo) {
        if (clazz.getInterfaces().length > 0) {
            classInfo
                    .append("<h2>")
                    .append(" implements ")
                    .append("</h2>");
            classInfo
                    .append("<h2>")
                    .append(
                    Arrays.stream(clazz.getInterfaces())
                            .map(Class::getSimpleName)
                            .collect(Collectors.joining(" "))
            ).append("</h2>").append("<br/>"). append("<br/>");
        }
    }

    public static void readClassAndMembersInfo(Class<?> clazz, StringBuilder classAndMembersInfo) {
        readClassInfo(clazz, classAndMembersInfo);
        appendFields(clazz, classAndMembersInfo);
        appendMethods(clazz, classAndMembersInfo);
        appendConstructors(clazz, classAndMembersInfo);
    }

    private static void appendFields(Class<?> clazz, StringBuilder classAndMembersInfo) {
        //Field[] fields = clazz.getFields();
        Field[] fields = clazz.getDeclaredFields();
        classAndMembersInfo
                .append(
                Arrays.stream(fields)
                        .map(Objects::toString)
                        .collect(Collectors.joining("<br/>"))
        );
    }

    private static void appendMethods(Class<?> clazz, StringBuilder classAndMembersInfo) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            appendAnnotations(method, classAndMembersInfo);
            classAndMembersInfo
                    .append("</br>")
                    .append("<h3>")
                    .append(" ")
                    .append(Modifier.toString(method.getModifiers()))
                    .append(" ")
                    .append(method.getReturnType().getSimpleName())
                    .append(" ")
                    .append(method.getName())
                    .append("</h3>")
                    .append(" ");
            appendParameters(method, classAndMembersInfo);
            appendExceptions(method, classAndMembersInfo);
        }
    }

    private static void appendAnnotations(Executable executable, StringBuilder classAndMembersInfo) {
        classAndMembersInfo
                .append("<br/>")
                .append(
                Arrays.stream(executable.getAnnotations())
                        .map(Objects::toString)
                        .collect(Collectors.joining("<br/>")));
    }

    private static void appendParameters(Executable executable, StringBuilder classAndMembersInfo) {
        classAndMembersInfo
                .append("<h3>")
                .append(
                Arrays.stream(executable.getParameters())
                        .map(Objects::toString)
                        .collect(Collectors.joining(", ", "(", ")"))
        ).append("</h3>").append(" ");
    }

    private static void appendExceptions(Executable executable, StringBuilder classAndMembersInfo) {
        if (executable.getExceptionTypes().length > 0) {
            classAndMembersInfo
                    .append("<h3>")
                    .append(" throws ")
                    .append("</h3>");
            classAndMembersInfo
                    .append("<h3>")
                    .append(
                    Arrays.stream(executable.getExceptionTypes())
                            .map(Class::getName)
                            .collect(Collectors.joining(" "))
            ).append("</h3>");
        }
    }

    private static void appendConstructors(Class<?> clazz, StringBuilder classAndMembersInfo) {
        Constructor[] constructors = clazz.getDeclaredConstructors();
        for (Constructor constructor : constructors) {
            appendAnnotations(constructor, classAndMembersInfo);
            classAndMembersInfo
                    .append("<br/>")
                    .append("<h3>")
                    .append(Modifier.toString(constructor.getModifiers()))
                    .append(" ")
                    .append(constructor.getName())
                    .append("</h3>")
                    .append("</body>\n")
                    .append("</html>\n");
            appendParameters(constructor, classAndMembersInfo);
            appendExceptions(constructor, classAndMembersInfo);
        }
    }
}
