/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.maven.mavenplugins;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.jboss.forge.maven.plugins.ConfigurationBuilder;
import org.jboss.forge.maven.plugins.ExecutionBuilder;
import org.junit.Test;

public class ExecutionBuilderTest {
    @Test
    public void testBuildExecution() {
        ExecutionBuilder executionBuilder = ExecutionBuilder.create()
                .setId("myid")
                .setPhase("compile")
                .addGoal("mygoal")
                .addGoal("othergoal");

        assertThat(executionBuilder.toString(), is("<execution><id>myid</id><phase>compile</phase><goal>mygoal</goal><goal>othergoal</goal></execution>"));
    }

    @Test
    public void testBuildExecutionWithConfig() {
        ExecutionBuilder executionBuilder = ExecutionBuilder.create()
                .setId("myid")
                .setPhase("compile")
                .addGoal("mygoal")
                .addGoal("othergoal")
                .setConfig(
                        ConfigurationBuilder.create()
                                .createConfigurationElement("myconfigelements")
                                .addChild("myelement").setText("myval").getParentElement()
                                .addChild("myotherelement").setText("otherval").getParentElement()
                                .getParentPluginConfig()
                );

        assertThat(executionBuilder.toString(), is("<execution><id>myid</id><phase>compile</phase><goal>mygoal</goal><goal>othergoal</goal><configuration><myconfigelements><myelement>myval</myelement><myotherelement>otherval</myotherelement></myconfigelements></configuration></execution>"));
    }
}
