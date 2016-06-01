/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.validation.providers;

import java.util.Collections;
import java.util.Set;

import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.javaee.Descriptors;
import org.jboss.forge.addon.javaee.validation.provider.ValidationProvider;
import org.jboss.shrinkwrap.descriptor.api.validationConfiguration11.ValidationConfigurationDescriptor;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class JavaEEValidatorProvider implements ValidationProvider
{
   private static final String PROVIDER_NAME = "Generic Java EE";
   private ValidationConfigurationDescriptor descriptor;

   public JavaEEValidatorProvider()
   {
      this.descriptor = Descriptors.create(ValidationConfigurationDescriptor.class);
   }

   @Override
   public String getName()
   {
      return PROVIDER_NAME;
   }

   @Override
   public ValidationConfigurationDescriptor getDefaultDescriptor()
   {
      return descriptor;
   }

   @Override
   public Set<Dependency> getDependencies()
   {
      return Collections.emptySet();
   }

   @Override
   public Set<Dependency> getAdditionalDependencies()
   {
      return Collections.emptySet();
   }
}
