package org.jboss.forge.shell.test.command.fshparser;

import java.util.Queue;

import javax.inject.Inject;

import junit.framework.Assert;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.shell.command.fshparser.FSHParser;
import org.jboss.forge.shell.command.fshparser.FSHRuntime;
import org.jboss.forge.shell.command.fshparser.LogicalStatement;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:koen.aers@gmail.com">Koen Aers</a>
 */
@RunWith(Arquillian.class)
public class FSHParserTest
{
   @Inject
   FSHRuntime runtime;
   
   @Test
   public void testParseLogicalStatement() {
      LogicalStatement ls;
      FSHParser parser;
      Queue<String> tokens;
      
      parser = new FSHParser("rm blah");
      ls = (LogicalStatement)parser.parse();
      tokens = ls.getTokens(runtime);
      Assert.assertEquals("rm", tokens.remove());
      Assert.assertEquals("blah", tokens.remove());
      Assert.assertTrue(tokens.isEmpty());

      parser = new FSHParser("rm b\\ l\\ a\\ h");
      ls = (LogicalStatement)parser.parse();
      tokens = ls.getTokens(runtime);
      Assert.assertEquals("rm", tokens.remove());
      Assert.assertEquals("b l a h", tokens.remove());
      Assert.assertTrue(tokens.isEmpty());
}

}
