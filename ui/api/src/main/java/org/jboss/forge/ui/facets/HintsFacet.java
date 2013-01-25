/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.ui.facets;

import org.jboss.forge.container.util.Assert;
import org.jboss.forge.environment.Environment;
import org.jboss.forge.facets.BaseFacet;
import org.jboss.forge.ui.UIInput;
import org.jboss.forge.ui.hints.HintsLookup;
import org.jboss.forge.ui.hints.InputType;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class HintsFacet extends BaseFacet<UIInput<?>>
{
   private HintsLookup hintsLookup;
   private InputType inputType;

   public HintsFacet(UIInput<?> origin, Environment environment)
   {
      super(origin);
      Assert.notNull(environment, "Environment must not be null.");

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

   public InputType getInputType()
   {
      if (inputType == null)
      {
         inputType = hintsLookup.getInputType(getOrigin().getValueType());
      }
      // TODO should we calculate and return a default input type here, or elsewhere?
      return inputType;
   }

   public HintsFacet setInputType(InputType type)
   {
      inputType = type;
      return this;
   }

}
