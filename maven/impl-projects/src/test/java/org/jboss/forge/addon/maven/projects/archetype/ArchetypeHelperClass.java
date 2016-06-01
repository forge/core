/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects.archetype;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 */
public class ArchetypeHelperClass {
    protected static final File basedir = new File(System.getProperty("basedir", "."));
    protected static final File archetypeGenerateDir = new File(basedir, "target/test-archetype-helper");
    protected static final File archetypeJarDir = new File(basedir, "target/test-archetypes");

    @BeforeClass
    public static void init() {
        Files.recursiveDelete(archetypeGenerateDir);
        archetypeGenerateDir.getParentFile().mkdirs();
    }

    @Test
    public void testMavenArchetype() throws Exception {
        File outputDir = assertCreateArchetype("cdi-camel-archetype.jar");

        assertThat(new File(outputDir, "pom.xml")).exists().isFile();
        assertThat(new File(outputDir, "src")).exists().isDirectory();
    }

    @Test
    public void testNonMavenArchetype() throws Exception {
        File outputDir = assertCreateArchetype("golang-example-archetype.jar");

        assertThat(new File(outputDir, "pom.xml")).doesNotExist();
        assertThat(new File(outputDir, "src")).doesNotExist();
    }

    protected File assertCreateArchetype(String archetypeJarName) throws IOException {
        String groupId = "com.acme";
        String artifactId = "myproject";
        String version = "1.0-SNAPSHOT";
        File archetypeJar = new File(archetypeJarDir, archetypeJarName);
        File outputDir = new File(archetypeGenerateDir, archetypeJarName);
        System.out.println("Executing archetype jar: " + archetypeJar + " in folder: " + outputDir);
        InputStream archetypeInput = assertOpenFile(archetypeJar);
        ArchetypeHelper archetypeHelper = new ArchetypeHelper(archetypeInput, outputDir, groupId, artifactId, version);
        archetypeHelper.setPackageName("com.acme");
        archetypeHelper.execute();
        return outputDir;
    }

    public static InputStream assertOpenFile(File file) throws FileNotFoundException {
        assertThat(file).isFile().exists();
        return new FileInputStream(file);
    }

}
