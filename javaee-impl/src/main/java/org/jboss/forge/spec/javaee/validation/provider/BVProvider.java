/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.validation.provider;

import static org.jboss.forge.shell.util.BeanManagerUtils.getContextualInstance;

import javax.enterprise.inject.spi.BeanManager;

/**
 * @author Kevin Pollet
 */
public enum BVProvider
{
   JAVA_EE("Generic Java EE", JavaEEValidatorProvider.class),
   HIBERNATE_VALIDATOR("Hibernate Validator", HibernateValidatorProvider.class),
   APACHE_BEAN_VALIDATION("Apache Bean Validation", ApacheBeanValidationProvider.class);

   private final String name;
   private final Class<? extends ValidationProvider> validationProviderClass;

   BVProvider(String name, Class<? extends ValidationProvider> validationProviderClass)
   {
      this.name = name;
      this.validationProviderClass = validationProviderClass;
   }

   public String getName()
   {
      return name;
   }

   public ValidationProvider getValidationProvider(BeanManager manager)
   {
      return getContextualInstance(manager, validationProviderClass);
   }

   public Class<? extends ValidationProvider> getValidationProviderClass()
   {
      return validationProviderClass;
   }
}
