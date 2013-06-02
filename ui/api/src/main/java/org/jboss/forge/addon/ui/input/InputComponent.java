/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.input;

import java.util.concurrent.Callable;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.facets.Faceted;
import org.jboss.forge.addon.ui.facets.HintsFacet;
import org.jboss.forge.furnace.services.Exported;

/**
 * This is the parent interface of all inputs.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Exported
public interface InputComponent<IMPLTYPE, VALUETYPE> extends Faceted<HintsFacet>
{
   String getLabel();

   String getName();
   
   String getDescription();

   Class<VALUETYPE> getValueType();

   boolean isEnabled();

   boolean isRequired();

   String getRequiredMessage();

   IMPLTYPE setEnabled(boolean b);

   IMPLTYPE setEnabled(Callable<Boolean> callable);

   IMPLTYPE setLabel(String label);
   
   IMPLTYPE setDescription(String description);

   IMPLTYPE setRequired(boolean required);

   IMPLTYPE setRequired(Callable<Boolean> required);

   IMPLTYPE setRequiredMessage(String message);

   Converter<String, VALUETYPE> getValueConverter();

   IMPLTYPE setValueConverter(Converter<String, VALUETYPE> converter);

}
