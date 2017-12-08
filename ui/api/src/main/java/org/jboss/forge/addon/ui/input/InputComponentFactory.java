/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.input;

/**
 * Allows creation of input components without injection
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public interface InputComponentFactory
{
   /**
    * Creates an {@link UIInput} component
    */
   <T> UIInput<T> createInput(String name, Class<T> valueType);

   /**
    * Creates an {@link UIInput} component
    */
   <T> UIInput<T> createInput(String name, char shortName, Class<T> valueType);

   /**
    * Creates an {@link UIInputMany} component
    */
   <T> UIInputMany<T> createInputMany(String name, Class<T> valueType);

   /**
    * Creates an {@link UIInputMany} component
    */
   <T> UIInputMany<T> createInputMany(String name, char shortName, Class<T> valueType);

   /**
    * Creates an {@link UISelectOne} component
    */
   <T> UISelectOne<T> createSelectOne(String name, Class<T> valueType);

   /**
    * Creates an {@link UISelectOne} component
    */
   <T> UISelectOne<T> createSelectOne(String name, char shortName, Class<T> valueType);

   /**
    * Creates an {@link UISelectMany} component
    */
   <T> UISelectMany<T> createSelectMany(String name, Class<T> valueType);

   /**
    * Creates an {@link UISelectMany} component
    */
   <T> UISelectMany<T> createSelectMany(String name, char shortName, Class<T> valueType);
}