/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test.command;

import java.util.Arrays;
import java.util.List;

import javax.inject.Singleton;

import org.jboss.forge.shell.PromptType;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.Plugin;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("motp")
@Singleton
public class MockOptionTestPlugin implements Plugin
{
   private String suppliedOption = "";
   private String requiredOption = "";
   private Boolean booleanOptionOmitted = null;
   private String defaultCommandArg;
   private String[] varargsOptions;

   @DefaultCommand
   public void defaultCommand(@Option(required = true) final String args)
   {
      setDefaultCommandArg(args);
   }

   @Command("suppliedOption")
   public void suppliedOption(@Option(name = "package",
            description = "Your java package",
            type = PromptType.JAVA_PACKAGE) final String option)
   {
      suppliedOption = option;
   }

   @Command("requiredOption")
   public void requiredOption(@Option(name = "package",
            required = true,
            description = "Your java package",
            type = PromptType.JAVA_PACKAGE) final String option)
   {
      requiredOption = option;
   }

   @Command("varargsOption")
   public void requiredOption(@Option final String... options)
   {
      varargsOptions = options;
   }

   @Command("booleanOptionOmitted")
   public void booleanOptionOmitted(@Option(required = false,
            description = "Some boolean flag") final boolean option)
   {
      booleanOptionOmitted = option;
   }

   public String getSuppliedOption()
   {
      return suppliedOption;
   }

   public void setSuppliedOption(final String suppliedOption)
   {
      this.suppliedOption = suppliedOption;
   }

   public String getRequiredOption()
   {
      return requiredOption;
   }

   public void setRequiredOption(final String requiredOption)
   {
      this.requiredOption = requiredOption;
   }

   public Boolean getBooleanOptionOmitted()
   {
      return booleanOptionOmitted;
   }

   public void setBooleanOptionOmitted(final Boolean booleanOptionOmitted)
   {
      this.booleanOptionOmitted = booleanOptionOmitted;
   }

   public void setDefaultCommandArg(final String defaultCommandArg)
   {
      this.defaultCommandArg = defaultCommandArg;
   }

   public String getDefaultCommandArg()
   {
      return defaultCommandArg;
   }

   public List<String> getVarargsOptions()
   {
      return Arrays.asList(varargsOptions);
   }
}
