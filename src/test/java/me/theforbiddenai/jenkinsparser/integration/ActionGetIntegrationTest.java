package me.theforbiddenai.jenkinsparser.integration;

import me.theforbiddenai.jenkinsparser.impl.JenkinsImpl;
import me.theforbiddenai.jenkinsparser.impl.entities.ClassInformation;
import me.theforbiddenai.jenkinsparser.impl.entities.EnumInformation;
import me.theforbiddenai.jenkinsparser.impl.entities.FieldInformation;
import me.theforbiddenai.jenkinsparser.impl.entities.MethodInformation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class ActionGetIntegrationTest {

    private static final String JENKINS_URL = "https://docs.oracle.com/javase/10/docs/api/allclasses-noframe.html";

    private static JenkinsImpl jenkins;

    @BeforeAll
    public static void setUp() throws Exception {
        jenkins = new JenkinsImpl(JENKINS_URL);
    }

    @Test
    public void testGetClass() {
        ClassInformation classInfo = jenkins.getClass("Object");

        assertThat(classInfo).isNotNull();
        assertThat(classInfo.getName()).isEqualTo("Class Object");
        assertThat(classInfo.getMethodList()).hasSize(11);
    }

    @Test
    public void testGetMethod() {
        ClassInformation classInfo = jenkins.getClass("Object");

        MethodInformation methodInfoClassName = jenkins.getMethod("Object", "clone");
        MethodInformation methodInfoClassObject = jenkins.getMethod(classInfo, "clone");

        assertThat(methodInfoClassName).isNotNull();
        assertThat(methodInfoClassObject).isNotNull();

        assertThat(methodInfoClassName.getDescription()).isEqualTo(methodInfoClassObject.getDescription());
        assertThat(methodInfoClassName.getName()).isEqualTo("clone");
    }

    @Test
    public void testGetEnum() {
        ClassInformation classInfo = jenkins.getClass("Component.BaselineResizeBehavior");

        EnumInformation enumInfoClassName = jenkins.getEnum("Component.BaselineResizeBehavior", "center_offset");
        EnumInformation enumInfoClassObject = jenkins.getEnum(classInfo, "center_offset");

        assertThat(enumInfoClassName).isNotNull();
        assertThat(enumInfoClassObject).isNotNull();

        assertThat(enumInfoClassName.getDescription()).isEqualTo(enumInfoClassObject.getDescription());
        assertThat(enumInfoClassName.getName()).isEqualTo("CENTER_OFFSET");
    }

    @Test
    public void testGetField() {
        ClassInformation classInfo = jenkins.getClass("String");

        FieldInformation fieldInfoClassName = jenkins.getField("String", "case_insensitive_order");
        FieldInformation fieldInfoClassObject = jenkins.getField(classInfo, "case_insensitive_order");

        assertThat(fieldInfoClassName).isNotNull();
        assertThat(fieldInfoClassObject).isNotNull();

        assertThat(fieldInfoClassName.getDescription()).isEqualTo(fieldInfoClassObject.getDescription());
        assertThat(fieldInfoClassName.getName()).isEqualTo("CASE_INSENSITIVE_ORDER");
    }

}
