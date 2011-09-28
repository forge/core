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