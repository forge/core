package org.jboss.forge.shell.test.fsh;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.shell.command.fshparser.FSHRuntime;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Mike Brock
 */
@Ignore
@RunWith(Arquillian.class)
public class FSHErrorsTests extends AbstractShellTest
{
   @Inject
   public FSHRuntime runtime;

   @Test
   public void testSimple()
   {
      runtime.run("if ($foo) {");
   }

}
