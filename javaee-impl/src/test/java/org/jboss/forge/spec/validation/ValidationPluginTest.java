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
package org.jboss.forge.spec.validation;

import java.io.IOException;
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

import static org.jboss.forge.shell.util.ConstraintInspector.getName;
import static org.jboss.forge.spec.javaee.validation.provider.BVProvider.APACHE_BEAN_VALIDATION;
import static org.jboss.forge.spec.javaee.validation.provider.BVProvider.HIBERNATE_VALIDATOR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Kevin Pollet
 */
@RunWith(Arquillian.class)
public class ValidationPluginTest extends SingletonAbstractShellTest
{
    private static final String PLUGIN_NAME = getName(ValidationPlugin.class);

    @Before
    @Override
    public void beforeTest() throws IOException
    {
        super.beforeTest();
        initializeJavaProject();
    }

    @Test
    public void testFacetInstalledWhenSetUp()
    {
        queueInputLines("");
        getShell().execute(PLUGIN_NAME + " setup --provider " + HIBERNATE_VALIDATOR);

        assertTrue(getProject().hasFacet(ValidationFacet.class));
    }

    @Test
    public void testValidatorProviderConfigIsGeneratedWhenSetUp()
    {
        //Hibernate Validator
        queueInputLines("");
        getShell().execute(PLUGIN_NAME + " setup --provider " + HIBERNATE_VALIDATOR);

        assertTrue(getProject().hasFacet(ValidationFacet.class));

        ValidationFacet facet = getProject().getFacet(ValidationFacet.class);
        assertNotNull(facet);

        ValidationDescriptor generatedDescriptor = facet.getConfig();
        ValidationDescriptor providerDescriptor = HIBERNATE_VALIDATOR.getValidationProvider(getBeanManager()).getDefaultDescriptor();

        assertValidationDescriptorValuesAreEquals(providerDescriptor, generatedDescriptor);

        //Apache Bean Validation
        queueInputLines("");
        getShell().execute(PLUGIN_NAME + " setup --provider " + APACHE_BEAN_VALIDATION);

        assertTrue(getProject().hasFacet(ValidationFacet.class));

        facet = getProject().getFacet(ValidationFacet.class);
        assertNotNull(facet);

        generatedDescriptor = facet.getConfig();
        providerDescriptor = APACHE_BEAN_VALIDATION.getValidationProvider(getBeanManager()).getDefaultDescriptor();

        assertValidationDescriptorValuesAreEquals(providerDescriptor, generatedDescriptor);
    }

    @Test
    public void testUserConfigIsAddedToGeneratedValidationDescriptorWhenSetUp()
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

        queueInputLines("");
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
    public void testValidationProviderDependenciesAreInstalledWhenSetUp()
    {
        //Hibernate Validator
        queueInputLines("");
        getShell().execute(PLUGIN_NAME + " setup --provider " + HIBERNATE_VALIDATOR);

        assertTrue(getProject().hasFacet(ValidationFacet.class));

        ValidationFacet facet = getProject().getFacet(ValidationFacet.class);
        ValidationProvider provider = HIBERNATE_VALIDATOR.getValidationProvider(getBeanManager());

        assertNotNull(facet);
        assertProjectHasDependencies(provider.getDependencies(), getProject());

        //Apache Bean Validation
        queueInputLines("");
        getShell().execute(PLUGIN_NAME + " setup --provider " + APACHE_BEAN_VALIDATION);

        assertTrue(getProject().hasFacet(ValidationFacet.class));

        facet = getProject().getFacet(ValidationFacet.class);
        provider = APACHE_BEAN_VALIDATION.getValidationProvider(getBeanManager());

        assertNotNull(facet);
        assertProjectHasDependencies(provider.getDependencies(), getProject());
    }

    private void assertProjectHasDependencies(Set<Dependency> expectedDependencies, Project project)
    {
        assertNotNull(expectedDependencies);
        assertTrue(project.hasFacet(DependencyFacet.class));

        final DependencyFacet facet = project.getFacet(DependencyFacet.class);
        assertNotNull(facet);

        for (Dependency oneDependency : expectedDependencies)
        {
            assertTrue(facet.hasDependency(oneDependency));
        }
    }

    private void assertValidationDescriptorValuesAreEquals(ValidationDescriptor expected, ValidationDescriptor actual)
    {
        assertNotNull(expected);
        assertNotNull(actual);
        assertEquals(expected.getDefaultProvider(), actual.getDefaultProvider());
        assertEquals(expected.getMessageInterpolator(), actual.getMessageInterpolator());
        assertEquals(expected.getTraversableResolver(), actual.getTraversableResolver());
        assertEquals(expected.getConstraintValidatorFactory(), actual.getConstraintValidatorFactory());
        assertEquals(expected.getConstraintMappings(), actual.getConstraintMappings());
    }
}
