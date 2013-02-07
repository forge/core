/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.classloader.mock.collisions;

import org.jboss.forge.proxy.ClassLoaderAdapterBuilder;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ClassImplementsInterfaceWithArrayParameterModification implements InterfaceWithArrayParameterModification
{
   private ClassLoader valueLoader;

   @Override
   public void setValueClassLoader(ClassLoader valueLoader)
   {
      this.valueLoader = valueLoader;
   }

   @Override
   public void modifyParameter(InterfaceModifiableContext parameter)
   {
      try
      {
         Object delegate = valueLoader.loadClass(ClassImplementsInterfaceExtendsInterfaceValue.class.getName())
                  .newInstance();
         InterfaceValue value = (InterfaceValue) ClassLoaderAdapterBuilder
                  .callingLoader(ClassImplementsInterfaceWithArrayParameterModification.class.getClassLoader())
                  .delegateLoader(valueLoader)
                  .enhance(delegate);
         parameter.addValue(value);
      }
      catch (Exception e)
      {
         throw new RuntimeException("Could not create value payload", e);
      }
   }
}
