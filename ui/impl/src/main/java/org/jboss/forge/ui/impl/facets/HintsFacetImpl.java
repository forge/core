/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.ui.impl.facets;

import org.jboss.forge.environment.Environment;
import org.jboss.forge.facets.BaseFacet;
import org.jboss.forge.ui.facets.HintsFacet;
import org.jboss.forge.ui.hints.HintsLookup;
import org.jboss.forge.ui.hints.InputType;
import org.jboss.forge.ui.input.InputComponent;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class HintsFacetImpl extends BaseFacet<InputComponent<?, ?>> implements HintsFacet
{
   private HintsLookup hintsLookup;
   private InputType inputType;

   public HintsFacetImpl(InputComponent<?, ?> origin, Environment environment)
   {
      super(origin);
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
      return getOrigin().hasFacet(this.getClass());
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.forge.ui.facets.HintsFacet#getInputType()
    */
   @Override
   public InputType getInputType()
   {
      if (inputType == null)
      {
         inputType = hintsLookup.getInputType(getOrigin().getValueType());
      }
      // TODO should we calculate and return a default input type here, or elsewhere?
      return inputType;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.forge.ui.facets.HintsFacet#setInputType(org.jboss.forge.ui.hints.InputType)
    */
   @Override
   public HintsFacet setInputType(InputType type)
   {
      inputType = type;
      return this;
   }

}
