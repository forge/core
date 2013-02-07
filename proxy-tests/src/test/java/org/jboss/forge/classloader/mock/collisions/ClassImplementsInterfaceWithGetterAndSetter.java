/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.classloader.mock.collisions;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ClassImplementsInterfaceWithGetterAndSetter implements InterfaceWithGetterAndSetter
{

   private InterfaceWithPassthroughMethod passthrough;

   @Override
   public InterfaceWithPassthroughMethod getPassthrough()
   {
      return passthrough;
   }

   @Override
   public void setPassthrough(InterfaceWithPassthroughMethod passthrough)
   {
      this.passthrough = passthrough;
   }

}
