/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.descriptor;

import java.util.List;

import org.jboss.shrinkwrap.descriptor.api.Descriptor;

/**
 * Bean Validation configuration descriptor
 * 
 * @author Kevin Pollet
 */
public interface ValidationDescriptor extends Descriptor
{
   ValidationDescriptor setDefaultProvider(String defaultProvider);

   ValidationDescriptor setMessageInterpolator(String messageInterpolator);

   ValidationDescriptor setTraversableResolver(String traversableResolver);

   ValidationDescriptor setConstraintValidatorFactory(String constraintValidatorFactory);

   ValidationDescriptor setConstraintMapping(String constraintMapping);

   String getDefaultProvider();

   String getMessageInterpolator();

   String getTraversableResolver();

   String getConstraintValidatorFactory();

   List<String> getConstraintMappings();
}
