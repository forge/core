/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.test;

import java.io.File;

import org.jboss.shrinkwrap.resolver.api.maven.Maven;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public final class Tests
{
   public static File[] resolveDependencies(final String coords)
   {
      return Maven.resolver().loadPomFromFile("pom.xml")
               .resolve(coords)
               .withTransitivity().asFile();
   }
}
