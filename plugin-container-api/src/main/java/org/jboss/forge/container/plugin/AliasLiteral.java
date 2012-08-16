/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.container.plugin;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;

/**
 * Used to create instances of the {@link Alias} annotation for performing bean lookups with {@link BeanManager}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class AliasLiteral extends AnnotationLiteral<Alias> implements Alias
{
   private static final long serialVersionUID = -2981610076971124230L;
   private String value;

   public AliasLiteral(String value)
   {
      this.value = value;
   }

   @Override
   public String value()
   {
      return value;
   }
}