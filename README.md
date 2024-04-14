# ![C Thing Software](https://www.cthing.com/branding/CThingSoftware-57x60.png "C Thing Software") escapers

[![CI](https://github.com/cthing/escapers/actions/workflows/ci.yml/badge.svg)](https://github.com/cthing/escapers/actions/workflows/ci.yml)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.cthing/escapers/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.cthing/escapers)
[![javadoc](https://javadoc.io/badge2/org.cthing/escapers/javadoc.svg)](https://javadoc.io/doc/org.cthing/escapers)

A Java library that escapes strings for CSV, HTML, Java, JavaScript, JSON, XML and YAML.

## Usage
The library is available from [Maven Central](https://repo.maven.apache.org/maven2/org/cthing/escapers/) using the
following Maven dependency:
```xml
<dependency>
  <groupId>org.cthing</groupId>
  <artifactId>escapers</artifactId>
  <version>1.0.0</version>
</dependency>
```
or the following Gradle dependency:
```kotlin
implementation("org.cthing:escapers:1.0.0")
```

Escaping for each supported language follows the pattern:
1. Select an escaper based on the language you are targeting. For example, to escape a string for use in an
   HTML file, select the `HtmlEscaper`.
2. All escapers offer static methods to escape a string or character array and will either return the
   escaped string or write it to the specified `Writer`. For example, to escape a string for use in an HTML file:
   ```java
   final String escaped = HtmlEscaper.escape("This & that"); 
   ```
   which produces the string "This &amp;amp; that".
3. Certain escapers offer options to control the escaping behavior. For example, the HTML escaper offers
   options to control what character ranges are escaped, the named entity sets used, and whether numerical
   character entities are presented in hexadecimal or decimal. For example, the following escapes a string
   for HTML using entities for all non-ASCII characters, named character entities for all ISO Latin-1 characters,
   and decimal for numerical character entities:
   ```java
   final String escaped = HtmlEscaper.escape("This & that",
                                             HtmlEscaper.Option.ESCAPE_NON_ASCII,
                                             HtmlEscaper.Option.USE_ISO_LATIN_1_ENTITIES,
                                             HtmlEscaper.Option.USE_DECIMAL); 
   ```
   
The [Javadoc](https://javadoc.io/doc/org.cthing/escapers) for each escaper provides detailed information about
the character replacements performed and the options supported.

## Building
The library is compiled for Java 17. If a Java 17 toolchain is not available, one will be downloaded.

Gradle is used to build the library:
```bash
./gradlew build
```
The Javadoc for the library can be generated by running:
```bash
./gradlew javadoc
```

## Releasing
This project is released on the [Maven Central repository](https://central.sonatype.com/artifact/org.cthing/escapers).
Perform the following steps to create a release.

- Commit all changes for the release
- In the `build.gradle.kts` file
    - Ensure that `baseVersion` is set to the version for the release. The project follows [semantic versioning](https://semver.org/).
    - Set `isSnapshot` to `false`
- Commit the changes
- Wait until CI builds the release candidate
- Run the command `mkrelease escapers <version>`
- In a browser go to the [Maven Central Repository Manager](https://s01.oss.sonatype.org/)
- Log in
- Use the `Staging Upload` to upload the generated artifact bundle `escapers-bundle-<version>.jar`
- Click on `Staging Repositories`
- Once it is enabled, press `Release` to release the artifacts to Maven Central
- Log out
- Wait for the new release to be available on Maven Central
- In a browser, go to the project on GitHub
- Generate a release with the tag `<version>`
- In the build.gradle.kts file
    - Increment the `baseVersion` patch number
    - Set `isSnapshot` to `true`
- Update the `CHANGELOG.md` with the changes in the release and prepare for next release changes
- Update the `Usage` section in the `README.md` with the latest artifact release version
- Commit these changes
