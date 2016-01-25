/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
