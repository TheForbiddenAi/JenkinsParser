# JenkinsParser
A java api to easily parse jenkins javadocs
## Usage
### Maven Dependency
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
  
<dependency>
    <groupId>com.github.TheForbiddenAi</groupId>
    <artifactId>JenkinsParser</artifactId>
    <version>v1.0.2</version>
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
List<Information> info = jenkins.search("String");

ClassInformation classInfo = jenkins.getClass("String");
MethodInformation methodInfo = jenkins.getMethod("String", "valueOf");
EnumInformation enumInfo = jenkins.getEnum("Component.BaselineResizeBehavior", "center_offset");
FieldInformation fieldInfo = jenkins.getField("String", "case_insensitive_order");
```

When searching for methods, or classes, that have the same name you can choose one of two ways:
```java
// Methods
MethodInformation methodInfo = jenkins.getMethod("String", "valueOf");
List<MethodInformation> methods = methodInfo.getAllMethods();

// Classes
ClassInformation classInfo = jenkins.getClass("Object");
List<ClassInformation> classes = classInfo.getAllClasses();
```
or
```java
// Methods
List<MethodInformation> methods = jenkins.searchMethods("String", "valueOf");

// Classes
List<ClassInformation> classes = jenkins.searchClasses("Object");
```
