# JenkinsParser
A java api to easily parse jenkins javadocs
## Usage
### Maven Dependency
```
<repositories>
	<repository>
	    <id>jitpack.io</id>
	    <url>https://jitpack.io</url>
	</repository>
</repositories>
  
<dependency>
    <groupId>com.github.theforbiddenai</groupId>
    <artifactId>JenkinsParser</artifactId>
    <version>RELEASE</version>
</dependency>
  ```

### Init
First, you must create an instance of `JenkinsImpl`
```java
JenkinsImpl jenkins = new JenkinsImpl(url);
```

* url - The jenkins class list url (Example URL: https://docs.oracle.com/en/java/javase/11/docs/api/allclasses.html)

### Usage

Querying classes, methods, enums, and fields:
```java
Information info = jenkins.search("String");

ClassInformation classInfo = jenkins.getClass("String");
MethodInformation methodInfo = jenkins.getMethod("String", "valueOf");
EnumInformation enumInfo = jenkins.getEnum("Component.BaselineResizeBehavior", "center_offset");
FieldInformation fieldInfo = jenkins.getField("String", "case_insensitive_order");
```

When searching for methods that have the same name you can choose one of two ways:
```java
MethodInformation methodInfo = jenkins.getMethod("String", "valueOf");
List<MethodInformation> methods = methodInfo.getAllMethods();
```
or
```java
List<MethodInformation> methods = jenkins.searchMethods("String", "valueOf");
```
