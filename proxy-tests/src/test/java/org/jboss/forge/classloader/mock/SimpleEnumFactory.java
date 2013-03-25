/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.classloader.mock;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class SimpleEnumFactory
{
   public SimpleEnum getEnum()
   {
      return SimpleEnum.STARTED;
   }

   public void useEnum(SimpleEnum instance)
   {
      
   }
}
