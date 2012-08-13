/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.maven;

import java.io.File;

import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;

public class MavenJdk6Test extends AbstractShellTest
{
   @Test
   public void shouldBeAbleToParseJDK6ActivatedProfile() throws Exception
   {
      // This just needs to succeed without exception
      getShell().execute("cd " + new File("src/test/resources/jdk6-pom").getAbsolutePath());
   }
}