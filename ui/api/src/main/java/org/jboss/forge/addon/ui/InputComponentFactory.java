/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.ui;

import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.input.UISelectOne;

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
   public abstract <T> UIInput<T> createInput(String name, Class<T> valueType);

   /**
    * Creates an {@link UIInput} component
    */
   public abstract <T> UIInput<T> createInput(String name, char shortName, Class<T> valueType);

   /**
    * Creates an {@link UIInputMany} component
    */
   public abstract <T> UIInputMany<T> createInputMany(String name, Class<T> valueType);

   /**
    * Creates an {@link UIInputMany} component
    */
   public abstract <T> UIInputMany<T> createInputMany(String name, char shortName, Class<T> valueType);

   /**
    * Creates an {@link UISelectOne} component
    */
   public abstract <T> UISelectOne<T> createSelectOne(String name, Class<T> valueType);

   /**
    * Creates an {@link UISelectOne} component
    */
   public abstract <T> UISelectOne<T> createSelectOne(String name, char shortName, Class<T> valueType);

   /**
    * Creates an {@link UISelectMany} component
    */
   public abstract <T> UISelectMany<T> createSelectMany(String name, Class<T> valueType);

   /**
    * Creates an {@link UISelectMany} component
    */
   public abstract <T> UISelectMany<T> createSelectMany(String name, char shortName, Class<T> valueType);
}