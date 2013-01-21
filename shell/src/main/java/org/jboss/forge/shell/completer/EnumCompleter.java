/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.completer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class EnumCompleter extends SimpleTokenCompleter
{
   private final Class<? extends Enum<?>> type;

   public EnumCompleter(Class<? extends Enum<?>> type)
   {
      this.type = type;
   }

   @Override
   public List<Object> getCompletionTokens()
   {
      List<Object> result = new ArrayList<Object>();
      Enum<?>[] constants = type.getEnumConstants();
      if (constants != null)
      {
         List<Enum<?>> list = Arrays.asList(constants);
         for (Enum<?> e : list)
         {
            result.add(e.toString());
         }
      }
      return result;
   }

}
