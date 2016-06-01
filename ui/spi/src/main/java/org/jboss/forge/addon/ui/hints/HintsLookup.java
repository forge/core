/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.hints;

import org.jboss.forge.addon.environment.Category;
import org.jboss.forge.addon.environment.Environment;

/**
 * Look up UI Hints. This allows manipulation of default UI type to render for a given input value type.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class HintsLookup implements Category
{
   private final Environment environment;

   public HintsLookup(Environment environment)
   {
      this.environment = environment;
   }

   public String getInputType(Class<?> valueType)
   {
      String inputType = (String) environment.get(HintsLookup.class).get(valueType);
      return inputType == null ? InputType.DEFAULT : inputType;
   }

   public void setInputType(Class<?> valueType, String type)
   {
      environment.get(HintsLookup.class).put(valueType, type);
   }
}
