/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.plugins.builtin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.resources.UnknownFileResource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.Topic;
import org.jboss.forge.shell.util.PluginUtil;

/**
 * @author Pablo Palazón
 * @author <a href="mailto:koen.aers@gmail.com">Koen Aers</a>
 */
@Alias("run-url")
@Topic("Shell Environment")
public class RunUrlPlugin implements Plugin
{
   private final Shell shell;

   @Inject
   private ResourceFactory factory;

   @Inject
   public RunUrlPlugin(final Shell shell)
   {
      this.shell = shell;
   }

   @DefaultCommand
   public void run(@Option(description = "url...", required = true) final String url, final PipeOut pipeOut,
            final String... args)
            throws Exception
   {
      String urlPattern = "^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
      if (Pattern.matches(urlPattern, url))
      {
         URL remote = new URL(url);
         String temporalDir = System.getProperty("java.io.tmpdir");
         File tempFile = new File(temporalDir, "temp" + UUID.randomUUID().toString().replace("-", ""));
         tempFile.createNewFile();
         UnknownFileResource tempResource = new UnknownFileResource(factory, tempFile);
         PluginUtil.downloadFromURL(pipeOut, remote, tempResource);

         try
         {
            shell.execute(tempFile, args);
         }
         catch (UnknownHostException e)
         {
            throw e;
         }
         catch (IOException e)
         {
            throw new RuntimeException("error executing script from url " + url);
         }
      }
      else
      {
         throw new RuntimeException("resource must be a url: " + url);
      }
   }
}
