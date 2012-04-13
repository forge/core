/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
