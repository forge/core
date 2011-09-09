/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.spec;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.jboss.forge.project.dependencies.DependencyBuilder.create;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.spec.javaee.ValidationFacet;
import org.jboss.forge.spec.javaee.descriptor.ValidationDescriptor;
import org.jboss.forge.spec.jpa.AbstractJPATest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Kevin Pollet
 */
@RunWith(Arquillian.class)
public class ValidationFacetTest extends AbstractJPATest
{
   private static final Dependency JAVAEE_6_SPEC = create("org.jboss.spec:jboss-javaee-6.0:1.0.0.Final:provided:basic");

   @Before
   @Override
   public void beforeTest() throws Exception
   {
      super.beforeTest();
      initializeJavaProject();
      if ((getProject() != null) && !getProject().hasFacet(ValidationFacet.class))
      {
         getShell().execute("project install-facet forge.spec.validation");
      }
   }

   @Test
   public void testFacetAddedWhenInstalled()
   {
      final ValidationFacet facet = getProject().getFacet(ValidationFacet.class);

      assertNotNull(facet);
      assertTrue(facet.isInstalled());
   }

   @Test
   public void testSpecDependenciesAddedWhenInstalled()
   {
      final DependencyFacet facet = getProject().getFacet(DependencyFacet.class);

      assertNotNull(facet);
      assertTrue(facet.hasDependency(JAVAEE_6_SPEC));
   }

   @Test
   public void testEmptyConfigFileCreatedWhenInstalled()
   {
      final ValidationFacet facet = getProject().getFacet(ValidationFacet.class);
      assertNotNull(facet);

      final ValidationDescriptor descriptor = facet.getConfig();

      assertNotNull(descriptor);
      assertNull(descriptor.getDefaultProvider());
      assertNull(descriptor.getMessageInterpolator());
      assertNull(descriptor.getConstraintValidatorFactory());
      assertNull(descriptor.getTraversableResolver());
      assertTrue(descriptor.getConstraintMappings().isEmpty());
   }
}
