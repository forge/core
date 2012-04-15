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
package org.jboss.forge.shell;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.forge.shell.events.CommandExecuted;

/**
 * Display a "Please wait" spinner for the user, until cancelled. It is a good idea to wrap usage of {@link Wait} in a
 * try-finally block to ensure that the wait is always completed.
 * 
 * TODO Create a cleanup mechanism for these on command execution.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class Wait
{
    private static String[] spinnerChars = new String[] { "/", "-", "\\", "|" };
    private boolean complete = true;
    private Runnable runnable;
    private Thread thread;

    private Shell shell;

    @Inject
    public Wait(final Shell shell)
    {
        this.shell = shell;
    }

    /**
     * Make sure we don't continue to wait after a command has completed.
     */
    void cleanup(@Observes CommandExecuted event)
    {
        stop();
    }

    /**
     * Start waiting, printing the default message.
     */
    public void start()
    {
        start("Please wait");
    }

    /**
     * Start waiting.
     */
    public void start(String message)
    {
        runnable = new Runnable()
        {
            @Override
            public void run()
            {
                int i = 0;
                while (isWaiting())
                {
                    shell.print(spinnerChars[i++]);
                    try
                    {
                        Thread.sleep(50);
                        shell.write('\b');
                    }
                    catch (InterruptedException e)
                    {
                        break;
                    }
                    if (i == spinnerChars.length)
                        i = 0;
                }
            }
        };

        try
        {
            shell.println();
            shell.print(message + "... ");
            complete = false;
            thread = new Thread(runnable);
            thread.start();
        }
        catch (Exception e)
        {
            stop();
        }
    }

    /**
     * Stop waiting.
     */
    public void stop()
    {
        if (isWaiting())
        {
            complete = true;
            shell.println();
        }
    }

    /**
     * Returns true if the waiting spinner is currently being displayed; otherwise, return false.
     */
    public boolean isWaiting()
    {
        return !complete;
    }
}
