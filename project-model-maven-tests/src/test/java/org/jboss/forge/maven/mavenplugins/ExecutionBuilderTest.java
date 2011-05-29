/*
 *
 *  * JBoss, Home of Professional Open Source
 *  * Copyright 2011, Red Hat, Inc., and individual contributors
 *  * by the @authors tag. See the copyright.txt in the distribution for a
 *  * full listing of individual contributors.
 *  *
 *  * This is free software; you can redistribute it and/or modify it
 *  * under the terms of the GNU Lesser General Public License as
 *  * published by the Free Software Foundation; either version 2.1 of
 *  * the License, or (at your option) any later version.
 *  *
 *  * This software is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  * Lesser General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU Lesser General Public
 *  * License along with this software; if not, write to the Free
 *  * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 */

package org.jboss.forge.maven.mavenplugins;

import org.jboss.forge.maven.plugins.ConfigurationBuilder;
import org.jboss.forge.maven.plugins.ExecutionBuilder;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

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
