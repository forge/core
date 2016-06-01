/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.ui.converters;

import javax.inject.Inject;

import org.jboss.forge.addon.parser.java.converters.PackageRootConverter;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.ui.facets.HintsFacet;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.inject.InputComponentInjectionEnricher;
import org.jboss.forge.addon.ui.input.inject.InputComponentInjectionPoint;
import org.jboss.forge.furnace.services.Imported;

/**
 * Adds the {@link PackageRootConverter} value converter to the {@link InputComponent} if type is a
 * {@link InputType#JAVA_PACKAGE_PICKER}
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class PackageRootEnricher implements InputComponentInjectionEnricher
{
   /**
    * Using {@link Imported} because {@link PackageRootConverter} depends on the {@link ProjectFactory} that is optional
    */
   @Inject
   private Imported<PackageRootConverter> imported;

   @SuppressWarnings("unchecked")
   @Override
   public void enrich(InputComponentInjectionPoint injectionPoint, InputComponent<?, ?> input)
   {
      String inputType = input.getFacet(HintsFacet.class).getInputType();
      if (InputType.JAVA_PACKAGE_PICKER.equals(inputType) && String.class == input.getValueType())
      {
         if (!imported.isUnsatisfied())
         {
            InputComponent<?, String> packageInput = (InputComponent<?, String>) input;
            packageInput.setValueConverter(imported.get());
         }
      }
   }
}
