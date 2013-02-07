/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.classloader.mock.collisions;

import java.util.List;


/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ClassImplementsInterfaceModifiableContext implements InterfaceModifiableContext
{

   private List<InterfaceValue> values;

   public ClassImplementsInterfaceModifiableContext(List<InterfaceValue> values)
   {
      this.values = values;
   }

   @Override
   public void addValue(InterfaceValue value)
   {
      this.values.add(value);
   }

}
