package me.theforbiddenai.jenkinsparser.integration;

import me.theforbiddenai.jenkinsparser.impl.JenkinsImpl;
import me.theforbiddenai.jenkinsparser.impl.entities.ClassInformation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class ClassGetIntegrationTest {

    private static final String JENKINS_URL = "https://docs.oracle.com/javase/10/docs/api/allclasses-noframe.html";

    private static JenkinsImpl jenkins;

    @BeforeAll
    public static void setUp() {
        jenkins = new JenkinsImpl(JENKINS_URL);
    }

    @Test
    public void testGetExtraInformation() {
        ClassInformation classInfo = jenkins.getClass("String");

        assertThat(classInfo).isNotNull();
        assertThat(classInfo.getName()).isEqualTo("Class String");
        assertThat(classInfo.getExtraInformation()).isNotNull();
        assertThat(classInfo.getExtraInformation()).hasSize(5);
        assertThat(classInfo.getExtraInformation().get("Since:")).isNotNull();
    }

    @Test
    public void testGetRawExtraInformation() {
        ClassInformation classInfo = jenkins.getClass("String");

        assertThat(classInfo).isNotNull();
        assertThat(classInfo.getName()).isEqualTo("Class String");
        assertThat(classInfo.getRawExtraInformation()).isNotNull();
        assertThat(classInfo.getRawExtraInformation()).hasSize(5);
        assertThat(classInfo.getRawExtraInformation().get("See Also:")).isNotNull();
    }

    @Test
    public void testGetNestedClasses() {
        ClassInformation classInfo = jenkins.getClass("Component");

        assertThat(classInfo).isNotNull();
        assertThat(classInfo.getName()).isEqualTo("Class Component");
        assertThat(classInfo.getNestedClassList()).isNotNull();
        assertThat(classInfo.getNestedClassList()).hasSize(4);
        assertThat(classInfo.getNestedClassList().get(0)).isEqualTo("Component.AccessibleAWTComponent");
    }

    @Test
    public void testGetMethods() {
        ClassInformation classInfo = jenkins.getClass("String");

        assertThat(classInfo).isNotNull();
        assertThat(classInfo.getName()).isEqualTo("Class String");
        assertThat(classInfo.getMethodList()).isNotNull();
        assertThat(classInfo.getMethodList()).hasSize(69);
        assertThat(classInfo.getMethodList().get(0)).isEqualTo("charAt");
    }

    @Test
    public void testGetMethodsWithParameters() {
        ClassInformation classInfo = jenkins.getClass("String");

        assertThat(classInfo).isNotNull();
        assertThat(classInfo.getName()).isEqualTo("Class String");
        assertThat(classInfo.getMethodListWithParameters()).isNotNull();
        assertThat(classInfo.getMethodListWithParameters()).hasSize(69);
        assertThat(classInfo.getMethodListWithParameters().get(0)).isEqualTo("charAt(int index)");
    }

    @Test
    public void testGetEnums() {
        ClassInformation classInfo = jenkins.getClass("Component.BaselineResizeBehavior");

        assertThat(classInfo).isNotNull();
        assertThat(classInfo.getName()).isEqualTo("Enum Component.BaselineResizeBehavior");
        assertThat(classInfo.getEnumList()).isNotNull();
        assertThat(classInfo.getEnumList()).hasSize(4);
        assertThat(classInfo.getEnumList().get(0)).isEqualTo("CENTER_OFFSET");
    }

    @Test
    public void testGetFields() {
        ClassInformation classInfo = jenkins.getClass("String");

        assertThat(classInfo).isNotNull();
        assertThat(classInfo.getName()).isEqualTo("Class String");
        assertThat(classInfo.getFieldList()).isNotNull();
        assertThat(classInfo.getFieldList()).hasSize(1);
        assertThat(classInfo.getFieldList().get(0)).isEqualTo("CASE_INSENSITIVE_ORDER");
    }

    @Test
    public void testGetClasses() {
        ClassInformation classInfo = jenkins.getClass("Object");

        assertThat(classInfo).isNotNull();
        assertThat(classInfo.getName()).isEqualTo("Class Object");
        assertThat(classInfo.getAllClasses()).isNotNull();
        assertThat(classInfo.getAllClasses()).hasSize(2);
        assertThat(classInfo.getAllClasses().get(1).getName()).isEqualTo("Interface Object");
    }

}
