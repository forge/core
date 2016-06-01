/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.addon.validation;

import java.lang.reflect.Type;

import javax.enterprise.inject.Vetoed;

import org.hibernate.validator.spi.valuehandling.ValidatedValueUnwrapper;
import org.jboss.forge.addon.ui.input.InputComponent;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.TypeResolver;

/**
 * Unwraps the value of a {@link InputComponent}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@Vetoed
class InputComponentValueUnwrapper extends ValidatedValueUnwrapper<InputComponent<?, ?>>
{
   private final TypeResolver typeResolver = new TypeResolver();

   @Override
   public Object handleValidatedValue(InputComponent<?, ?> value)
   {
      return value.getValue();
   }

   @Override
   public Type getValidatedValueType(Type valueType)
   {
      ResolvedType resolvedType = typeResolver.resolve(valueType);
      return resolvedType.typeParametersFor(InputComponent.class).get(1).getErasedType();
   }

}
