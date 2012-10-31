# Forge Maven Extensions

This project contains a MOJO to create .FAR (Forge Archive) files.

## Usage
        <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
           <modelVersion>4.0.0</modelVersion>
           <groupId>com.example.app</groupId>
           <artifactId>example-far</artifactId>
           <version>0.0.1-SNAPSHOT</version>
           <packaging>far</packaging>

           <build>
              <plugins>
                 <plugin>
                    <groupId>org.jboss.forge</groupId>
                    <artifactId>maven-far-plugin</artifactId>
                    <version>0.0.1-SNAPSHOT</version>
                    <extensions>true</extensions>
                 </plugin>
              </plugins>

           </build>
        </project>
