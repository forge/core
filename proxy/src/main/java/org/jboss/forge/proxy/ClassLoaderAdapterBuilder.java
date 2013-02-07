/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.proxy;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ClassLoaderAdapterBuilder implements ClassLoaderAdapterBuilderCallingLoader,
         ClassLoaderAdapterBuilderDelegateLoader
{
   private ClassLoader callingLoader;
   private ClassLoader delegateLoader;

   public static ClassLoaderAdapterBuilderCallingLoader callingLoader(ClassLoader callingLoader)
   {
      ClassLoaderAdapterBuilder result = new ClassLoaderAdapterBuilder();
      result.callingLoader = callingLoader;
      return result;
   }

   @Override
   public ClassLoaderAdapterBuilderDelegateLoader delegateLoader(ClassLoader delegateLoader)
   {
      this.delegateLoader = delegateLoader;
      return this;
   }

   @Override
   public <T> T enhance(T delegate)
   {
      return ClassLoaderAdapterCallback.enhance(callingLoader, delegateLoader, delegate);
   }

}
