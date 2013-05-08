/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.scaffoldx.faces;

import org.jboss.forge.project.Project;
import org.jboss.forge.test.AbstractShellTest;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */

public abstract class AbstractFacesScaffoldTest extends AbstractShellTest
{
   //
   // Protected methods
   //

   protected Project setupScaffoldProject() throws Exception
   {
      Project project = initializeJavaProject();
      queueInputLines("HIBERNATE", "JBOSS_AS7", "", "", "");
      getShell().execute("persistence setup");
      queueInputLines("", "", "", "");
      getShell().execute("scaffold-x setup");
      return project;
   }

   protected Project setupScaffoldProject(String targetDir) throws Exception
   {
      Project project = initializeJavaProject();
      queueInputLines("HIBERNATE", "JBOSS_AS7", "", "", "");
      getShell().execute("persistence setup");
      queueInputLines("", "", "");
      getShell().execute("scaffold-x setup --targetDir " + targetDir);
      return project;
   }

   protected String normalized(StringBuilder sb)
   {
      return normalized(sb.toString());
   }

   protected String normalized(String input)
   {
      return input.replaceAll("(\r|\n|\\s)+", " ");
   }
}
