/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.parser.java.ui.validators;

import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.forge.addon.ui.facets.HintsFacet;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.InputComponentInjectionEnricher;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class JLSValidatorEnricher implements InputComponentInjectionEnricher
{

   @Override
   public void enrich(InjectionPoint injectionPoint, InputComponent<?, ?> input)
   {
      String inputType = input.getFacet(HintsFacet.class).getInputType();
      if (inputType != null)
      {
         switch (inputType)
         {
         case InputType.JAVA_PACKAGE_PICKER:
            input.addValidator(new PackageUIValidator());
            break;
         // FIXME: Using primitives validates as "int is a keyword"
         // case InputType.JAVA_CLASS_PICKER:
         // input.addValidator(new ClassNameUIValidator());
         // break;
         }
      }
   }
}
