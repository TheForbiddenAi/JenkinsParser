package me.theforbiddenai.jenkinsparser.unit;

import me.theforbiddenai.jenkinsparser.impl.JenkinsImpl;
import me.theforbiddenai.jenkinsparser.impl.entities.ClassInformation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ClassGetUnitTest {

    private static JenkinsImpl jenkins;

    @BeforeAll
    public static void setUp() {
        jenkins = mock(JenkinsImpl.class);
    }

    @Test
    public void testGetExtraInformation() {
        ClassInformation mockClass = new ClassInformation("", null);
        mockClass.setName("testClass");

        HashMap<String, String> extraInfo = new HashMap<>();
        extraInfo.put("header1", "info1");
        extraInfo.put("header2", "info2");

        mockClass.setExtraInformation(extraInfo);

        when(jenkins.getClass(anyString())).thenReturn(mockClass);

        ClassInformation classInfo = jenkins.getClass("testClass");

        assertThat(classInfo).isNotNull();
        assertThat(classInfo.getName()).isEqualTo("testClass");
        assertThat(classInfo.getExtraInformation()).isNotNull();
        assertThat(classInfo.getExtraInformation()).hasSize(2);
        assertThat(classInfo.getExtraInformation().get("header1")).isEqualTo("info1");
    }

    @Test
    public void testGetRawExtraInformation() {
        ClassInformation mockClass = new ClassInformation("", null);
        mockClass.setName("testClass");

        HashMap<String, String> rawExtraInfo = new HashMap<>();
        rawExtraInfo.put("rawHeader1", "rawInfo1");
        rawExtraInfo.put("rawHeader2", "rawInfo2");

        mockClass.setRawExtraInformation(rawExtraInfo);

        when(jenkins.getClass(anyString())).thenReturn(mockClass);

        ClassInformation classInfo = jenkins.getClass("testClass");

        assertThat(classInfo).isNotNull();
        assertThat(classInfo.getName()).isEqualTo("testClass");
        assertThat(classInfo.getRawExtraInformation()).isNotNull();
        assertThat(classInfo.getRawExtraInformation()).hasSize(2);
        assertThat(classInfo.getRawExtraInformation().get("rawHeader1")).isEqualTo("rawInfo1");
    }

    @Test
    public void testGetNestedClasses() {
        ClassInformation mockClass = new ClassInformation("", null);
        mockClass.setName("testClass");

        ArrayList<String> nestedClassList = new ArrayList<>(Arrays.asList("nestedClass1", "nestedClass2"));
        mockClass.setNestedClassList(nestedClassList);

        when(jenkins.getClass(anyString())).thenReturn(mockClass);

        ClassInformation classInfo = jenkins.getClass("testClass");

        assertThat(classInfo).isNotNull();
        assertThat(classInfo.getName()).isEqualTo("testClass");
        assertThat(classInfo.getNestedClassList()).isNotNull();
        assertThat(classInfo.getNestedClassList()).hasSize(2);
    }

    @Test
    public void testGetMethods() {
        ClassInformation mockClass = new ClassInformation("", null);
        mockClass.setName("testClass");

        ArrayList<String> methodList = new ArrayList<>(Arrays.asList("method1", "method2"));
        mockClass.setMethodList(methodList);

        when(jenkins.getClass(anyString())).thenReturn(mockClass);

        ClassInformation classInfo = jenkins.getClass("testClass");

        assertThat(classInfo).isNotNull();
        assertThat(classInfo.getName()).isEqualTo("testClass");
        assertThat(classInfo.getMethodList()).isNotNull();
        assertThat(classInfo.getMethodList()).hasSize(2);
    }

    @Test
    public void testGetMethodsWithParameters() {
        ClassInformation mockClass = new ClassInformation("", null);
        mockClass.setName("testClass");

        ArrayList<String> methodListWithParams = new ArrayList<>(Arrays.asList("method(String s)", "method(int i)"));
        mockClass.setMethodListWithParameters(methodListWithParams);

        when(jenkins.getClass(anyString())).thenReturn(mockClass);

        ClassInformation classInfo = jenkins.getClass("testClass");

        assertThat(classInfo).isNotNull();
        assertThat(classInfo.getName()).isEqualTo("testClass");
        assertThat(classInfo.getMethodListWithParameters()).isNotNull();
        assertThat(classInfo.getMethodListWithParameters()).hasSize(2);
    }

    @Test
    public void testGetEnums() {
        ClassInformation mockClass = new ClassInformation("", null);
        mockClass.setName("testClass");

        ArrayList<String> enumList = new ArrayList<>(Arrays.asList("enum1", "enum2"));
        mockClass.setEnumList(enumList);

        when(jenkins.getClass(anyString())).thenReturn(mockClass);

        ClassInformation classInfo = jenkins.getClass("testClass");

        assertThat(classInfo).isNotNull();
        assertThat(classInfo.getName()).isEqualTo("testClass");
        assertThat(classInfo.getEnumList()).isNotNull();
        assertThat(classInfo.getEnumList()).hasSize(2);
    }

    @Test
    public void testGetFields() {
        ClassInformation mockClass = new ClassInformation("", null);
        mockClass.setName("testClass");

        ArrayList<String> fieldList = new ArrayList<>(Arrays.asList("field1", "field2"));
        mockClass.setFieldList(fieldList);

        when(jenkins.getClass(anyString())).thenReturn(mockClass);

        ClassInformation classInfo = jenkins.getClass("testClass");

        assertThat(classInfo).isNotNull();
        assertThat(classInfo.getName()).isEqualTo("testClass");
        assertThat(classInfo.getFieldList()).isNotNull();
        assertThat(classInfo.getFieldList()).hasSize(2);
    }

}
