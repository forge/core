/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.shell.squelch;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@Squelched
@Interceptor
public class SquelchingInterceptor
{
   public SquelchingInterceptor()
   {
   }

   @AroundInvoke
   public Object manage(InvocationContext ic) throws Exception
   {
      PrintStream out = System.out;
      System.setOut(new PrintStream(new OutputStream()
      {
         @Override
         public void write(int b) throws IOException
         {
            // die bad messages, die!
         }
      }));

      PrintStream err = System.err;
      System.setErr(new PrintStream(new OutputStream()
      {
         @Override
         public void write(int b) throws IOException
         {
            // die bad messages, die!
         }
      }));
      try
      {
         return ic.proceed();
      }
      finally
      {
         System.setOut(out);
         System.setErr(err);
      }
   }
}
