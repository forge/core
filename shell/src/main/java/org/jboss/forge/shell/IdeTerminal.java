package org.jboss.forge.shell;

import org.jboss.forge.shell.console.jline.TerminalSupport;

import java.io.IOException;
import java.io.InputStream;

public class IdeTerminal extends TerminalSupport
{
	
	public final int DEFAULT_WIDTH = Integer.MAX_VALUE;
   
	public IdeTerminal() 
	{
	   super(true);
	   setEchoEnabled(false);
	   setAnsiSupported(true);
	}
	
	public int readCharacter(final InputStream in) throws IOException 
	{
	   int result = in.read();
	   return result == '\r' ? in.read() : result;
	}

    public int getWidth() 
    {
       return DEFAULT_WIDTH;
    }

}
