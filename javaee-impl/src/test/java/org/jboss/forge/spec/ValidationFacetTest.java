/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec;

import static org.jboss.forge.project.dependencies.DependencyBuilder.create;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
         queueInputLines("");
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
      assertTrue(facet.hasDirectManagedDependency(JAVAEE_6_SPEC));
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
