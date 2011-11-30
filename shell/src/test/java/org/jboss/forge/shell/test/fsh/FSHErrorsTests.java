package org.jboss.forge.shell.test.fsh;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.shell.command.fshparser.FSHRuntime;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

/**
 * @author Mike Brock
 */
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
