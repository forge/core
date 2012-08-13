/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.test.web;

import java.util.Collection;

import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.project.Project;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface WebTest
{

   void setup(Project project);

   JavaClass from(Project project, Class<?> clazz);

   void addAsTestClass(Project project, JavaClass clazz);

   Method<JavaClass> buildDefaultDeploymentMethod(Project project, JavaClass clazz, Collection<String> deploymentItems);

}
