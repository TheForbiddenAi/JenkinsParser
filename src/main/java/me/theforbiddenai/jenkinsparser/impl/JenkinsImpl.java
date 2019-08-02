package me.theforbiddenai.jenkinsparser.impl;

import me.theforbiddenai.jenkinsparser.Information;
import me.theforbiddenai.jenkinsparser.Jenkins;
import me.theforbiddenai.jenkinsparser.impl.entities.ClassInformation;
import me.theforbiddenai.jenkinsparser.impl.entities.EnumInformation;
import me.theforbiddenai.jenkinsparser.impl.entities.FieldInformation;
import me.theforbiddenai.jenkinsparser.impl.entities.MethodInformation;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JenkinsImpl implements Jenkins {

    private String baseURL;
    private ArrayList<Element> classList;

    // URL must be a link to the class list
    public JenkinsImpl(@NotNull String url) {
        url = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;

        classList = getClassList(url);
        baseURL = url.substring(0, url.lastIndexOf("/") + 1);
    }

    /**
     * Gets the requested information based on the search query
     * @param query The search query
     * @return The found information based on the search query or null
     */
    public Information search(String query) {

        if(query.length() == 0) throw new Error("Invalid query!");

        query = query.replace("#", ".");
        query = query.endsWith(".") ? query.substring(0, query.length() - 1) : query;

        try {
            return getClass(query);
        } catch (NullPointerException ignored) {}

        String className = query.contains(".") ? query.substring(0, query.lastIndexOf(".")) : query;
        String objectName = query.contains(".") ? query.substring(query.lastIndexOf(".") + 1) : null;

        ClassInformation classInfo = getClass(className);
        if(objectName != null) {
            try {
                return getMethod(classInfo, objectName);
            } catch (NullPointerException ignored) {}

            try {
                return getEnum(classInfo, objectName);
            } catch (NullPointerException ignored) {}

            try {
                return getField(classInfo, objectName);
            } catch (NullPointerException ignored) {}

            throw new NullPointerException("Couldn't find the specified query!");
        }

        return null;
    }

    /**
     * Gets the class with the specified name
     *
     * @param className The name of the class being searched for
     * @return The class information object or null if none are found
     */
    @Override
    public ClassInformation getClass(String className) {
        return new ClassInformation(baseURL, classList, className);
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
        return new MethodInformation(classInfo, methodName);
    }

    /**
     * Returns a list of all methods in the class which have the specified method name
     *
     * @param className  The class name for the class the method(s) are being searched for in
     * @param methodName The method(s) being searched for
     * @return A list of methods which have the specified name from the specified class
     */
    @Override
    public List<MethodInformation> searchMethods(String className, String methodName) {
        return new MethodInformation(getClass(className), methodName).getAllMethods();
    }

    /**
     * Returns a list of all methods in the class which have the specified method name
     *
     * @param classInfo  The class information object for the class the method(s) are being searched for in
     * @param methodName The method(s) being searched for
     * @return A list of methods which have the specified name from the specified class
     */
    @Override
    public List<MethodInformation> searchMethods(ClassInformation classInfo, String methodName) {
        return new MethodInformation(classInfo, methodName).getAllMethods();
    }

    /**
     * Returns the enum info with the specified name in the specified class
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
        return new EnumInformation(classInfo, enumName);
    }

    @Override
    public FieldInformation getField(String className, String fieldName) {
        return getField(getClass(className), fieldName);
    }

    @Override
    public FieldInformation getField(ClassInformation classInfo, String fieldName) {
        return new FieldInformation(classInfo, fieldName);
    }

    /**
     * Gets the class list from the specified jenkins url
     *
     * @param classListURL The jenkins url
     * @return The class list
     */
    @NotNull
    private ArrayList<Element> getClassList(@NotNull String classListURL) {
        ArrayList<Element> classList = new ArrayList<>();
        try {
            Document classListDoc = Jsoup.connect(classListURL).get();

            classListDoc.select("li").stream()
                    .filter(element -> element.selectFirst("a") != null)
                    .forEach(classList::add);
        } catch (IOException | NullPointerException ex) {
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


}
