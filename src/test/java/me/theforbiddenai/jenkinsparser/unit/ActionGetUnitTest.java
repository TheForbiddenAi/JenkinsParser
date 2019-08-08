package me.theforbiddenai.jenkinsparser.unit;

import me.theforbiddenai.jenkinsparser.impl.JenkinsImpl;
import me.theforbiddenai.jenkinsparser.impl.entities.ClassInformation;
import me.theforbiddenai.jenkinsparser.impl.entities.EnumInformation;
import me.theforbiddenai.jenkinsparser.impl.entities.FieldInformation;
import me.theforbiddenai.jenkinsparser.impl.entities.MethodInformation;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class ActionGetUnitTest {

    private static JenkinsImpl jenkins;

    @BeforeAll
    public static void setUp() {
        jenkins = mock(JenkinsImpl.class);
    }

    @Test
    public void testGetClass() {
        ClassInformation mockClass = new ClassInformation(null, null);
        mockClass.setName("testClass");

        when(jenkins.getClass(anyString())).thenReturn(mockClass);

        ClassInformation classInfo = jenkins.getClass("testClass");

        assertThat(classInfo).isNotNull();
        assertThat(classInfo.getName()).isEqualTo("testClass");

    }

    @Test
    public void testGetMethod() {
        ClassInformation classInfo = new ClassInformation(null, null);

        MethodInformation mockMethod = new MethodInformation(classInfo, null, "");
        mockMethod.setName("testMethod");

        when(jenkins.getMethod(any(ClassInformation.class), anyString())).thenReturn(mockMethod);
        when(jenkins.getMethod(anyString(), anyString())).thenReturn(mockMethod);

        MethodInformation methodInfoClassObject = jenkins.getMethod(classInfo, "testMethod");
        MethodInformation methodInfoClassName = jenkins.getMethod("testClass", "testMethod");

        assertThat(methodInfoClassObject).isNotNull();
        assertThat(methodInfoClassName).isNotNull();

        assertThat(methodInfoClassObject.getName()).isEqualTo("testMethod");
        assertThat(methodInfoClassName.getName()).isEqualTo("testMethod");

    }

    @Test
    public void testGetEnum() {
        ClassInformation classInfo = new ClassInformation(null, null);

        EnumInformation mockEnum = new EnumInformation(classInfo, (Element) null);
        mockEnum.setName("testEnum");

        when(jenkins.getEnum(any(ClassInformation.class), anyString())).thenReturn(mockEnum);
        when(jenkins.getEnum(anyString(), anyString())).thenReturn(mockEnum);

        EnumInformation enumInfoClassObject = jenkins.getEnum(classInfo, "testEnum");
        EnumInformation enumInfoClassName = jenkins.getEnum("testClass", "testEnum");

        assertThat(enumInfoClassObject).isNotNull();
        assertThat(enumInfoClassName).isNotNull();

        assertThat(enumInfoClassObject.getName()).isEqualTo("testEnum");
        assertThat(enumInfoClassName.getName()).isEqualTo("testEnum");
    }

    @Test
    public void testGetField() {
        ClassInformation classInfo = new ClassInformation(null, null);

        FieldInformation mockField = new FieldInformation(null, (Element) null);
        mockField.setName("testField");

        when(jenkins.getField(any(ClassInformation.class), anyString())).thenReturn(mockField);
        when(jenkins.getField(anyString(), anyString())).thenReturn(mockField);

        FieldInformation fieldInfoClassObject = jenkins.getField(classInfo, "testField");
        FieldInformation fieldInfoClassName = jenkins.getField("testClass", "testField");

        assertThat(fieldInfoClassObject).isNotNull();
        assertThat(fieldInfoClassName).isNotNull();

        assertThat(fieldInfoClassObject.getName()).isEqualTo("testField");
        assertThat(fieldInfoClassName.getName()).isEqualTo("testField");
    }


}
