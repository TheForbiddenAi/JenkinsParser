package me.theforbiddenai.jenkinsparser;

import me.theforbiddenai.jenkinsparser.impl.entities.ClassInformation;
import me.theforbiddenai.jenkinsparser.impl.entities.EnumInformation;
import me.theforbiddenai.jenkinsparser.impl.entities.FieldInformation;
import me.theforbiddenai.jenkinsparser.impl.entities.MethodInformation;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Jenkins {

    List<Information> search(String query);

    ClassInformation getClass(String className);

    List<ClassInformation> searchClasses(String className);

    MethodInformation getMethod(String className, String methodName);

    MethodInformation getMethod(ClassInformation classInfo, String methodName);

    List<MethodInformation> searchMethods(String className, String methodName);

    List<MethodInformation> searchMethods(ClassInformation classInfo, String methodName);

    EnumInformation getEnum(String className, String enumName);

    EnumInformation getEnum(ClassInformation classInfo, String enumName);

    FieldInformation getField(String className, String fieldName);

    FieldInformation getField(ClassInformation classInfo, String fieldName);

    void setJenkinsUrl(@NotNull String url);

}
