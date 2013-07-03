/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.validation;

import static org.jboss.forge.shell.util.ConstraintInspector.getName;
import static org.jboss.forge.spec.javaee.validation.provider.BVProvider.APACHE_BEAN_VALIDATION;
import static org.jboss.forge.spec.javaee.validation.provider.BVProvider.HIBERNATE_VALIDATOR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.spec.javaee.ValidationFacet;
import org.jboss.forge.spec.javaee.descriptor.ValidationDescriptor;
import org.jboss.forge.spec.javaee.validation.ValidationPlugin;
import org.jboss.forge.spec.javaee.validation.provider.ValidationProvider;
import org.jboss.forge.test.SingletonAbstractShellTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Kevin Pollet
 */
@RunWith(Arquillian.class)
public class ValidationPluginTest extends SingletonAbstractShellTest
{
   private static final String PLUGIN_NAME = getName(ValidationPlugin.class);

   @Before
   @Override
   public void beforeTest() throws Exception
   {
      super.beforeTest();
      initializeJavaProject();
   }

   @Test
   public void testFacetInstalledWhenSetUp() throws Exception
   {
      queueInputLines("", "", "");
      getShell().execute(PLUGIN_NAME + " setup --provider " + HIBERNATE_VALIDATOR);

      assertTrue(getProject().hasFacet(ValidationFacet.class));
   }

   @Test
   public void testValidatorProviderConfigIsGeneratedWhenSetUp() throws Exception
   {
      // Hibernate Validator
      queueInputLines("", "", "");
      getShell().execute(PLUGIN_NAME + " setup --provider " + HIBERNATE_VALIDATOR);

      assertTrue(getProject().hasFacet(ValidationFacet.class));

      ValidationFacet facet = getProject().getFacet(ValidationFacet.class);
      assertNotNull(facet);

      ValidationDescriptor generatedDescriptor = facet.getConfig();
      ValidationDescriptor providerDescriptor = HIBERNATE_VALIDATOR.getValidationProvider(getBeanManager())
               .getDefaultDescriptor();

      assertDefaultProviderValidationDescriptorValuesAreEquals(providerDescriptor, generatedDescriptor);

      // Apache Bean Validation
      queueInputLines("", "");
      getShell().execute(PLUGIN_NAME + " setup --provider " + APACHE_BEAN_VALIDATION);

      assertTrue(getProject().hasFacet(ValidationFacet.class));

      facet = getProject().getFacet(ValidationFacet.class);
      assertNotNull(facet);

      generatedDescriptor = facet.getConfig();
      providerDescriptor = APACHE_BEAN_VALIDATION.getValidationProvider(getBeanManager()).getDefaultDescriptor();

      assertDefaultProviderValidationDescriptorValuesAreEquals(providerDescriptor, generatedDescriptor);
   }

   @Test
   public void testUserConfigIsAddedToGeneratedValidationDescriptorWhenSetUp() throws Exception
   {
      final String providedMessageInterpolator = "org.jboss.forge.spec.validation.MockMessageInterpolator";
      final String providedTraversableResolver = "org.jboss.forge.spec.validation.MockuserTraversableResolver";
      final String providedConstraintValidatorFactory = "org.jboss.forge.spec.validation.MockuserTraversableResolver";

      final StringBuilder shellCommand = new StringBuilder();
      shellCommand.append(PLUGIN_NAME);
      shellCommand.append(" setup");
      shellCommand.append(" --messageInterpolator ").append(providedMessageInterpolator);
      shellCommand.append(" --traversableResolver ").append(providedTraversableResolver);
      shellCommand.append(" --constraintValidatorFactory ").append(providedConstraintValidatorFactory);

      queueInputLines("", "", "");
      getShell().execute(shellCommand.toString());

      assertTrue(getProject().hasFacet(ValidationFacet.class));

      final ValidationFacet facet = getProject().getFacet(ValidationFacet.class);
      assertNotNull(facet);

      final ValidationDescriptor projectDescriptor = facet.getConfig();

      assertNotNull(projectDescriptor);
      assertEquals(providedMessageInterpolator, projectDescriptor.getMessageInterpolator());
      assertEquals(providedTraversableResolver, projectDescriptor.getTraversableResolver());
      assertEquals(providedConstraintValidatorFactory, projectDescriptor.getConstraintValidatorFactory());
   }

   @Test
   public void testValidationProviderDependenciesAreInstalledWhenSetUp() throws Exception
   {
      // Hibernate Validator
      queueInputLines("", "", "");
      getShell().execute(PLUGIN_NAME + " setup --provider " + HIBERNATE_VALIDATOR);

      assertTrue(getProject().hasFacet(ValidationFacet.class));

      ValidationFacet facet = getProject().getFacet(ValidationFacet.class);
      ValidationProvider provider = HIBERNATE_VALIDATOR.getValidationProvider(getBeanManager());

      assertNotNull(facet);
      assertProjectHasDependencies(provider.getDependencies(), getProject());

      // Apache Bean Validation
      queueInputLines("", "");
      getShell().execute(PLUGIN_NAME + " setup --provider " + APACHE_BEAN_VALIDATION);

      assertTrue(getProject().hasFacet(ValidationFacet.class));

      facet = getProject().getFacet(ValidationFacet.class);
      provider = APACHE_BEAN_VALIDATION.getValidationProvider(getBeanManager());

      assertNotNull(facet);
      assertProjectHasDependencies(provider.getDependencies(), getProject());
   }

   private void assertProjectHasDependencies(final Set<Dependency> expectedDependencies, final Project project)
   {
      assertNotNull(expectedDependencies);
      assertTrue(project.hasFacet(DependencyFacet.class));

      final DependencyFacet facet = project.getFacet(DependencyFacet.class);
      assertNotNull(facet);

      for (Dependency oneDependency : expectedDependencies)
      {
         assertTrue(facet.hasEffectiveDependency(oneDependency));
      }
   }

   private void assertDefaultProviderValidationDescriptorValuesAreEquals(final ValidationDescriptor expected,
            final ValidationDescriptor actual)
   {
      assertNotNull(expected);
      assertNotNull(actual);
      assertEquals(expected.getDefaultProvider(), actual.getDefaultProvider());
   }
}
