/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.forge.parser.java.tests;

import javax.inject.Singleton;

import junit.framework.Assert;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaBinaryFacet;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author jfraney
 */
@Singleton
@RunWith(Arquillian.class)
public class JavaBinaryFacetTest extends AbstractShellTest
{
   @Test
   public void testSearchArchives() throws Exception
   {
      Project project = initializeJavaProject();
      DependencyFacet dependencyFacet = project.getFacet(DependencyFacet.class);
      String [] gavs = new String[] {
    		  "org.slf4j:slf4j-api:1.6.0"
      };
      
      for(String gav: gavs) {
    	  Dependency dependency = DependencyBuilder.create(gav);
    	  dependencyFacet.addDirectDependency(dependency);
      }
      
      
      JavaBinaryFacet index = project.getFacet(JavaBinaryFacet.class);
      JavaSource<?> loggerClass = index.find("org.slf4j.Logger");
      Assert.assertNotNull(loggerClass);
      
      Assert.assertTrue(loggerClass.isClass());
      
      Method<JavaClass> method = ((JavaClass)loggerClass).getMethod("isTraceEnabled");
      Assert.assertNotNull(method);

   }
}
