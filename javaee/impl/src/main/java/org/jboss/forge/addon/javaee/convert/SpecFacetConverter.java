/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.convert;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.javaee.JavaEEFacet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class SpecFacetConverter implements Converter<JavaEEFacet, String>
{
   @Override
   public String convert(JavaEEFacet source)
   {
      return source.getSpecVersion().toString();
   }
}
