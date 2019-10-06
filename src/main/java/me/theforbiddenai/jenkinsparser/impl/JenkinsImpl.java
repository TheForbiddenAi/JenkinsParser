package me.theforbiddenai.jenkinsparser.impl;

import me.theforbiddenai.jenkinsparser.Information;
import me.theforbiddenai.jenkinsparser.Jenkins;
import me.theforbiddenai.jenkinsparser.impl.entities.ClassInformation;
import me.theforbiddenai.jenkinsparser.impl.entities.EnumInformation;
import me.theforbiddenai.jenkinsparser.impl.entities.FieldInformation;
import me.theforbiddenai.jenkinsparser.impl.entities.MethodInformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JenkinsImpl implements Jenkins {

    private String baseURL;
    private ArrayList<Element> classList;
    private Cache CACHE = new Cache();

    // URL must be a link to the class list
    public JenkinsImpl(@NotNull String url) {
        url = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;

        classList = getClassList(url);
        baseURL = url.substring(0, url.lastIndexOf("/") + 1);
    }

    /**
     * Gets the requested information based on the search query
     *
     * @param query The search query
     * @return A list of found information based on the search query or null
     */
    @Override
    public List<Information> search(String query) {

        if (query.length() == 0) throw new Error("Invalid query!");

        query = query.replace("#", ".");
        query = query.endsWith(".") ? query.substring(0, query.length() - 1) : query;

        List<Information> infoList = new ArrayList<>();
        try {
            infoList.addAll(Utilites.convertList(searchClasses(query)));
        } catch (NullPointerException ignored) {}

        String className = query.contains(".") ? query.substring(0, query.lastIndexOf(".")) : query;
        String objectName = query.contains(".") ? query.substring(query.lastIndexOf(".") + 1) : null;


            if (objectName != null) {

                ClassInformation classInfo = getClass(className);

                try {
                    infoList.addAll(Utilites.convertList(searchMethods(classInfo, objectName)));
                } catch (NullPointerException ignored) {}

                try {
                    infoList.add(getEnum(classInfo, objectName));
                } catch (NullPointerException ignored) {}

                try {
                    infoList.add(getField(classInfo, objectName));
                } catch (NullPointerException ignored) {}

            }

        if (infoList.size() == 0) {
            throw new NullPointerException("Couldn't find the specified query!");
        }

        return infoList;
    }

    /**
     * Gets the first class with the specified name
     *
     * @param className The name of the class being searched for
     * @return The class information object or null if none are found
     */
    @Override
    public ClassInformation getClass(String className) {
        try {
            ClassInformation classInfo = (ClassInformation) CACHE.getInformation(className);
            if (classInfo != null) {
                return classInfo;
            }

            classInfo = new ClassInformation(baseURL, classList, className);
            CACHE.insertInformation(className, classInfo);

            return classInfo;
        } catch (ClassCastException ex) {
            throw new NullPointerException("Class not found!");
        }
    }

    /**
     * Gets all classes with the specified name
     *
     * @param className The name of the classes being searched for
     * @return A list of class information for all found classes or null if none are found
     */
    @Override
    public List<ClassInformation> searchClasses(String className) {
        return getClass(className).getAllClasses();
    }

    /**
     * Gets the first method with the specified name from the class
     *
     * @param className  The class name for the class the method is being searched from in
     * @param methodName The method name being searched for
     * @return The first method with the specified name from the specified class or null if none are found
     */
    @Override
    public MethodInformation getMethod(String className, String methodName) {
        return getMethod(getClass(className), methodName);
    }

    /**
     * Gets the first method with the specified name from the class
     *
     * @param classInfo  The class information object for the class the method is being searched from in
     * @param methodName The method name being searched for
     * @return The first method with the specified name from the specified class or null if none are found
     */
    @Override
    public MethodInformation getMethod(ClassInformation classInfo, String methodName) {
        try {
            MethodInformation methodInfo = (MethodInformation) CACHE.getInformation(classInfo.getName() + "." + methodName);
            if (methodInfo != null) {
                return methodInfo;
            }

            methodInfo = new MethodInformation(classInfo, methodName);
            CACHE.insertInformation(classInfo.getName() + "." + methodName, methodInfo);

            return methodInfo;
        } catch (ClassCastException ex) {
            throw new NullPointerException("Couldn't find the specified method!");
        }
    }

    /**
     * Gets a list of all methods in the class which have the specified method name
     *
     * @param className  The class name for the class the method(s) are being searched for in
     * @param methodName The method(s) being searched for
     * @return A list of methods which have the specified name from the specified class
     */
    @Override
    public List<MethodInformation> searchMethods(String className, String methodName) {
        return searchMethods(getClass(className), methodName);
    }

    /**
     * Gets a list of all methods in the class which have the specified method name
     *
     * @param classInfo  The class information object for the class the method(s) are being searched for in
     * @param methodName The method(s) being searched for
     * @return A list of methods which have the specified name from the specified class
     */
    @Override
    public List<MethodInformation> searchMethods(ClassInformation classInfo, String methodName) {
        return getMethod(classInfo, methodName).getAllMethods();
    }

    /**
     * Gets the enum info with the specified name in the specified class
     *
     * @param className The class name for the enum is being searched for in
     * @param enumName  The enum being searched for
     * @return The enum info with the specified name or null if none are found
     */
    @Override
    public EnumInformation getEnum(String className, String enumName) {
        return getEnum(getClass(className), enumName);
    }

    /**
     * Returns the enum info with the specified name in the specified class
     *
     * @param classInfo The class the enum is being searched for in
     * @param enumName  The enum being searched for
     * @return The enum info with the specified name or null if none are found
     */
    @Override
    public EnumInformation getEnum(ClassInformation classInfo, String enumName) {
        try {
            EnumInformation enumInfo = (EnumInformation) CACHE.getInformation(classInfo.getName() + "." + enumName);
            if (enumInfo != null) {
                return enumInfo;
            }

            enumInfo = new EnumInformation(classInfo, enumName);
            CACHE.insertInformation(classInfo.getName() + "." + enumName, enumInfo);

            return enumInfo;
        } catch (ClassCastException ex) {
            throw new NullPointerException("Could not find the specified enum");
        }
    }

    /**
     * Gets the field info with the specified name in the specified class
     *
     * @param className The name of the class the field is being searched for it
     * @param fieldName The name of the field being searched for
     * @return
     */
    @Override
    public FieldInformation getField(String className, String fieldName) {
        return getField(getClass(className), fieldName);
    }

    /**
     * Gets the field info with the specified name in the specified class
     *
     * @param classInfo The class information for the class the field in
     * @param fieldName The name of the field being searched for
     * @return
     */
    @Override
    public FieldInformation getField(ClassInformation classInfo, String fieldName) {
        try {
            FieldInformation fieldInfo = (FieldInformation) CACHE.getInformation(classInfo.getName() + "." + fieldName);
            if (fieldInfo != null) {
                return fieldInfo;
            }

            fieldInfo = new FieldInformation(classInfo, fieldName);
            CACHE.insertInformation(classInfo.getName() + "." + fieldName, fieldInfo);

            return fieldInfo;
        } catch (ClassCastException ex) {
            throw new NullPointerException("Could not find the specified field");
        }
    }

    /**
     * Gets the class list from the specified jenkins url
     *
     * @param classListURL The jenkins url
     * @return The class list
     */
    @Nullable
    private ArrayList<Element> getClassList(@NotNull String classListURL) {
        ArrayList<Element> classList = new ArrayList<>();
        try {
            Document classListDoc = Jsoup.connect(classListURL).get();

            classListDoc.select("li").stream()
                    .filter(element -> element.selectFirst("a") != null)
                    .forEach(classList::add);
        } catch (IOException | NullPointerException | IllegalArgumentException ex) {
            ex.printStackTrace();
            throw new Error("Invalid jenkins url!");
        }

        if (classList.size() == 0) {
            throw new Error("Unable to from the given jenkins url!");
        }

        return classList;
    }

    /**
     * Updates the Jenkins JavaDoc URL
     *
     * @param url The new url
     */
    public void setJenkinsUrl(@NotNull String url) {
        url = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;

        classList = getClassList(url);
        baseURL = url.substring(0, url.lastIndexOf("/") + 1);
    }

    private static final class Cache {

        private final Map<String, Information> informationCache = new ConcurrentHashMap<>();

        @Nullable
        Information getInformation(@NotNull String name) {
            return informationCache.get(name.toLowerCase());
        }

        void insertInformation(@NotNull String name, @NotNull Information info) {
            informationCache.put(name.toLowerCase(), info);
        }

    }
}