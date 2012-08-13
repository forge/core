/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.ForgeEnvironment;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ShellConfig
{

   @Inject
   private ForgeEnvironment environment;

   public void loadConfig(final ShellImpl shell)
   {
      File configDir = environment.getConfigDirectory().getUnderlyingResourceObject();
      if ((configDir != null) && configDir.exists() && !shell.isNoInitMode())
      {
         boolean historyEnabled = shell.isHistoryEnabled();
         shell.setHistoryEnabled(false);
         File configFile = new File(configDir.getPath(), ShellImpl.FORGE_CONFIG_FILE);

         if (!configFile.exists())
         {
            createDefaultConfigFile(configFile);
         }

         try
         {
            shell.execute(configFile);
         }
         catch (Exception e)
         {
            e.printStackTrace();
            throw new RuntimeException("error loading file: " + configFile.getAbsolutePath());
         }
         shell.setHistoryEnabled(historyEnabled);
      }
   }

   public void loadHistory(final ShellImpl shell)
   {
      File configDir = environment.getConfigDirectory().getUnderlyingResourceObject();
      if ((configDir != null) && configDir.exists() && !shell.isNoInitMode())
      {
         File historyFile = new File(configDir.getPath(), ShellImpl.FORGE_COMMAND_HISTORY_FILE);
         try
         {
            if (!historyFile.exists())
            {
               if (!historyFile.createNewFile())
               {
                  System.err.println("could not create config file: " + historyFile.getAbsolutePath());
               }

            }
         }
         catch (IOException e)
         {
            throw new RuntimeException("could not create config file: " + historyFile.getAbsolutePath());
         }

         List<String> history = new ArrayList<String>();
         try
         {
            BufferedReader reader = new BufferedReader(new FileReader(historyFile));

            String line;
            while ((line = reader.readLine()) != null)
            {
               history.add(line);
            }

            reader.close();

            shell.setHistory(history);
         }
         catch (IOException e)
         {
            throw new RuntimeException("error loading file: " + historyFile.getAbsolutePath());
         }

         try
         {
            shell.setHistoryOutputStream(new BufferedOutputStream(new FileOutputStream(historyFile, true)));
         }
         catch (FileNotFoundException e)
         {
            throw new RuntimeException("error setting forge history output stream to file: "
                     + historyFile.getAbsolutePath());
         }
      }
   }

   private void createDefaultConfigFile(final File configFile)
   {
      try
      {
         /**
          * Create a default config file.
          */

         configFile.createNewFile();
         OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(configFile));
         String defaultConfig = getDefaultConfig();
         for (int i = 0; i < defaultConfig.length(); i++)
         {
            outputStream.write(defaultConfig.charAt(i));
         }
         outputStream.flush();
         outputStream.close();

      }
      catch (IOException e)
      {
         e.printStackTrace();
         throw new RuntimeException("error creating default config file: " + configFile.getAbsolutePath());
      }
   }

   private String getDefaultConfig()
   {
      return "@/* Automatically generated config file */;\n" +
               "about;\n" +
               "if ($OS_NAME.startsWith(\"Windows\")) {\n" +
               "    echo \"  Windows? Really? Okay...\\n\"\n" +
               "}\n" +
               "\n" +
               "set " + ShellImpl.PROP_HISTORY + " " + true + ";\n" +
               "set " + ShellImpl.PROP_PROMPT + " \"" + ShellImpl.DEFAULT_PROMPT + "\";\n" +
               "set " + ShellImpl.PROP_PROMPT_NO_PROJ + " \"" + ShellImpl.DEFAULT_PROMPT_NO_PROJ + "\";\n" +
               "set " + ShellImpl.PROP_DEFAULT_PLUGIN_REPO + " \"" + ShellImpl.DEFAULT_PLUGIN_REPO + "\";\n" +
               "set " + ShellImpl.PROP_IGNORE_EOF + " " + ShellImpl.DEFAULT_IGNORE_EOF + ";\n";

   }
}
