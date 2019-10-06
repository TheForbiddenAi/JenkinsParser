package me.theforbiddenai.jenkinsparser.integration;

import me.theforbiddenai.jenkinsparser.Information;
import me.theforbiddenai.jenkinsparser.impl.JenkinsImpl;
import me.theforbiddenai.jenkinsparser.impl.entities.EnumInformation;
import me.theforbiddenai.jenkinsparser.impl.entities.FieldInformation;
import me.theforbiddenai.jenkinsparser.impl.entities.MethodInformation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class MiscGetIntegrationTest {

    private static final String JENKINS_URL = "https://docs.oracle.com/javase/10/docs/api/allclasses-noframe.html";

    private static JenkinsImpl jenkins;

    @BeforeAll
    public static void setUp() throws Exception {
        jenkins = new JenkinsImpl(JENKINS_URL);
    }

    @Test
    public void testMethodGetExtraInformation() {
        MethodInformation methodInfo = jenkins.getMethod("String", "length");

        infoTest(methodInfo, "length", "Specified by:");
    }

    @Test
    public void testMethodGetMethods() {
        MethodInformation methodInfo = jenkins.getMethod("String", "valueOf");

        assertThat(methodInfo).isNotNull();
        assertThat(methodInfo.getName()).isEqualTo("valueOf");

        assertThat(methodInfo.getAllMethods()).isNotNull();
        assertThat(methodInfo.getAllMethods()).hasSize(9);
        assertThat(methodInfo.getAllMethods().get(0)).isNotNull();
    }

    @Test
    public void testEnumGetExtraInformation() {
        EnumInformation enumInfo = jenkins.getEnum("Component.BaselineResizeBehavior", "center_offset");

        infoTest(enumInfo, "CENTER_OFFSET", null);
    }

    @Test
    public void testFieldGetExtraInformation() {
        FieldInformation field = jenkins.getField("String", "CASE_INSENSITIVE_ORDER");

        infoTest(field, "CASE_INSENSITIVE_ORDER", "Since:");
    }

    /**
     * This method tests both the getExtraInformation and getRawExtraInformation methods
     *
     * @param info   The information object being tested
     * @param name   The object name to make sure it is correct
     * @param header The header being checked to see if it's value is null
     */
    private void infoTest(Information info, String name, String header) {
        assertThat(info).isNotNull();
        assertThat(info.getName()).isEqualTo(name);

        if (header != null) {
            assertThat(info.getRawExtraInformation()).isNotNull();
            assertThat(info.getRawExtraInformation()).hasSize(2);
            assertThat(info.getRawExtraInformation().get(header)).isNotNull();

            assertThat(info.getExtraInformation()).isNotNull();
            assertThat(info.getExtraInformation()).hasSize(2);
            assertThat(info.getExtraInformation().get(header)).isNotNull();
        }
    }

}
