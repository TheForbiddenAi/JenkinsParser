package me.theforbiddenai.jenkinsparser.unit;

import me.theforbiddenai.jenkinsparser.Information;
import me.theforbiddenai.jenkinsparser.impl.JenkinsImpl;
import me.theforbiddenai.jenkinsparser.impl.entities.ClassInformation;
import me.theforbiddenai.jenkinsparser.impl.entities.EnumInformation;
import me.theforbiddenai.jenkinsparser.impl.entities.FieldInformation;
import me.theforbiddenai.jenkinsparser.impl.entities.MethodInformation;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MiscGetUnitTest {

    private static JenkinsImpl jenkins;

    @BeforeAll
    public static void setUp() {
        jenkins = mock(JenkinsImpl.class);
    }

    @Test
    public void testMethodGetExtraInformation() {
        ClassInformation mockClass = new ClassInformation("", null);
        MethodInformation mockMethod = new MethodInformation(mockClass, null, "");

        infoTest(mockMethod);
    }

    @Test
    public void testMethodGetMethods() {
        ClassInformation mockClass = new ClassInformation("", null);
        MethodInformation mockMethod = new MethodInformation(mockClass, null, "");
        mockMethod.setName("testMethod");

        mockMethod.setMethodList(new ArrayList<>(Arrays.asList(mockMethod, mockMethod)));

        when(jenkins.getMethod(anyString(), anyString())).thenReturn(mockMethod);

        MethodInformation methodInfo = jenkins.getMethod("testClass", "testMethod");

        assertThat(methodInfo).isNotNull();
        assertThat(methodInfo.getName()).isEqualTo("testMethod");

        assertThat(methodInfo.getAllMethods()).isNotNull();
        assertThat(methodInfo.getAllMethods()).hasSize(2);
        assertThat(methodInfo.getAllMethods().get(0).getName()).isEqualTo("testMethod");
    }

    @Test
    public void testEnumGetExtraInformation() {
        ClassInformation mockClass = new ClassInformation("", null);
        EnumInformation mockEnum = new EnumInformation(mockClass, (Element) null);

        infoTest(mockEnum);
    }

    @Test
    public void testFieldGetExtraInformation() {
        ClassInformation mockClass = new ClassInformation("", null);
        FieldInformation mockField = new FieldInformation(mockClass, (Element) null);

        infoTest(mockField);
    }

    /**
     * This method tests both the getExtraInformation and getRawExtraInformation methods
     *
     * @param mockInfo The information object being tested
     */
    private void infoTest(Information mockInfo) {
        mockInfo.setName("test");

        HashMap<String, String> rawExtraInfo = new HashMap<>();
        rawExtraInfo.put("rawHeader1", "rawInfo1");
        rawExtraInfo.put("rawHeader2", "rawInfo2");

        mockInfo.setRawExtraInformation(rawExtraInfo);
        mockInfo.setExtraInformation(rawExtraInfo);

        List<Information> mockInfoList = new ArrayList<>();
        mockInfoList.add(mockInfo);

        when(jenkins.search(anyString())).thenReturn(mockInfoList);

        Information info = jenkins.search("test").get(0);

        assertThat(info).isNotNull();
        assertThat(info.getName()).isEqualTo("test");

        assertThat(info.getRawExtraInformation()).isNotNull();
        assertThat(info.getRawExtraInformation()).hasSize(2);
        assertThat(info.getRawExtraInformation().get("rawHeader1")).isEqualTo("rawInfo1");

        assertThat(info.getExtraInformation()).isNotNull();
        assertThat(info.getExtraInformation()).hasSize(2);
        assertThat(info.getExtraInformation().get("rawHeader2")).isEqualTo("rawInfo2");
    }

}