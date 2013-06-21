/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.impl.facets;

import org.jboss.forge.addon.environment.Environment;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.ui.facets.HintsFacet;
import org.jboss.forge.addon.ui.hints.HintsLookup;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class HintsFacetImpl extends AbstractFacet<InputComponent<?, ?>> implements HintsFacet
{
   private HintsLookup hintsLookup;
   private InputType inputType;

   public HintsFacetImpl(InputComponent<?, ?> origin, Environment environment)
   {
      super.setFaceted(origin);
      if (environment == null)
      {
         throw new IllegalStateException("Environment must not be null.");
      }

      this.hintsLookup = new HintsLookup(environment);
   }

   @Override
   public boolean install()
   {
      return true;
   }

   @Override
   public boolean isInstalled()
   {
      return getFaceted().hasFacet(this.getClass());
   }

   @Override
   public InputType getInputType()
   {
      if (inputType == null)
      {
         inputType = hintsLookup.getInputType(getFaceted().getValueType());
      }
      return inputType;
   }

   @Override
   public HintsFacet setInputType(InputType type)
   {
      inputType = type;
      return this;
   }

}
