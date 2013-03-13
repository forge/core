/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container.versions;

import org.jboss.forge.container.util.Assert;

/**
 * A single, fixed value {@link Version}.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class SingleVersion implements Version
{
   private String version;

   public SingleVersion(String version)
   {
      Assert.notNull(version, "Version must not be null.");
      this.version = version;
   }

   @Override
   public String toString()
   {
      return version;
   }
}
