/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.command.fshparser;

import java.util.LinkedList;
import java.util.Queue;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.command.Execution;
import org.jboss.forge.shell.command.ExecutionParser;
import org.jboss.forge.shell.command.PluginRegistry;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.util.PipeOutImpl;

/**
 * @author Mike Brock .
 */
@Singleton
public class FSHRuntime
{
   private final Shell shell;
   private final PluginRegistry pluginRegistry;
   private final Instance<Execution> executionInstance;
   private final ExecutionParser executionParser;

   @Inject
   public FSHRuntime(Shell shell, PluginRegistry pluginRegistry,
            Instance<Execution> executionInstance,
            ExecutionParser executionParser)
   {
      this.shell = shell;
      this.pluginRegistry = pluginRegistry;
      this.executionInstance = executionInstance;
      this.executionParser = executionParser;
   }

   public void run(final String str)
   {
      run(new FSHParser(str).parse(), null);
   }

   public void run(final Node startNode, final PipeOut forwardPipe)
   {
      AutoReducingQueue arQueue;
      Node n = startNode;
      PipeOut lastPipe = null;

      do
      {
         if (n instanceof LogicalStatement)
         {
            arQueue = new AutoReducingQueue(((LogicalStatement) n).getNest(), this);
         }
         else if (n instanceof PipeNode)
         {
            if (lastPipe == null)
            {
               throw new RuntimeException("broken pipe");
            }

            run(((PipeNode) n).getNest(), lastPipe);
            continue;
         }
         else
         {
            throw new RuntimeException("badly formed stack:" + n);
         }

         Queue<String> outQueue = new LinkedList<String>();
         for (String s : arQueue)
         {
            if (s == null || s.equals(""))
            {
               continue;
            }
            outQueue.add(s);
         }

         if (!outQueue.isEmpty())
         {
            PipeOut pipeOut = new PipeOutImpl(shell);

            if (n.next != null && n.next instanceof PipeNode)
            {
               pipeOut.setPiped(true);
               lastPipe = pipeOut;
            }

            Node x = n;
            while (x instanceof LogicalStatement && (x = ((LogicalStatement) x).nest) != null)
            {
               if (x instanceof ScriptNode || x.next != null)
               {
                  break;
               }
            }

            String pipeIn = forwardPipe != null ? forwardPipe.getBuffer() : null;
            Execution execution = executionParser.parse(outQueue, pipeIn, pipeOut);
            execution.verifyConstraints(shell);
            execution.perform(forwardPipe);
         }
      }
      while ((n = n.next) != null);
   }

   public void shell(String command)
   {
      run(command);
   }

   public Shell getShell()
   {
      return shell;
   }

   public PluginRegistry getPluginRegistry()
   {
      return pluginRegistry;
   }

   public Instance<Execution> getExecutionInstance()
   {
      return executionInstance;
   }
}
