/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
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
package org.jboss.forge.dev;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.PackagingFacet;
import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.util.OSUtils;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@Alias("as7")
@ApplicationScoped
public class AS7DeployerPlugin implements Plugin
{
   private static final String STANDALONE_DEPLOYMENTS = "/standalone/deployments";

   private static Process proc;

   @Inject
   private Project project;

   @Inject
   private ShellPrompt prompt;

   @Inject
   private Shell shell;

   @Inject
   private ResourceFactory factory;

   public static void main(final String[] args) throws IOException
   {
      String startup = "sh /Users/lbaxter/dev/jboss-7/bin/standalone.sh";

      proc = Runtime.getRuntime().exec(startup);
   }

   @Command
   public void deploy(final PipeOut out)
   {
      FileResource<?> finalArtifact = getFinalArtifact();
      FileResource<?> deployedArtifact = getDeployedArtifact();

      if (!deployedArtifact.exists()
               || prompt.promptBoolean("Overwrite existing deployment? [" + deployedArtifact.getFullyQualifiedName()
                        + "]"))
      {
         deployedArtifact.setContents(finalArtifact.getResourceInputStream());
         shell.execute("rm -rf " + deployedArtifact.getFullyQualifiedName() + ".*");
         ShellMessages.success(out, "Deployed [" + finalArtifact + "] to ["
                  + getDeploymentDirectory().getFullyQualifiedName() + "]");
      }
   }

   @Command
   public void undeploy(final PipeOut out)
   {
      FileResource<?> finalArtifact = getFinalArtifact();
      FileResource<?> deployedArtifact = getDeployedArtifact();

      if (deployedArtifact.exists()
               && prompt.promptBoolean("Really undeploy [" + deployedArtifact.getFullyQualifiedName()
                        + "]?"))
      {
         deployedArtifact.setContents(finalArtifact.getResourceInputStream());
         shell.execute("rm -rf " + deployedArtifact.getFullyQualifiedName() + "*");
         ShellMessages.success(out, "Removed deployment [" + finalArtifact + "] from ["
                  + getDeploymentDirectory().getFullyQualifiedName() + "]");
      }
   }

   // @Command
   public void start(final PipeOut out) throws IOException
   {
      Runtime r = Runtime.getRuntime();
      String command = getAS7ExecutableCommand() + getAS7Executable().getFullyQualifiedName();
      out.println(command);
      proc = r.exec(command);
      ShellMessages.info(shell, "JBoss AS7 instance started.");

   }

   // @Command
   public void stop()
   {
      if (proc != null)
      {
         proc.destroy();
         ShellMessages.info(shell, "JBoss AS7 instance terminated");
      }
      ShellMessages
               .info(shell,
                        "JBoss AS7 instance not running or handle to process was lost. Might need to shut things down manually.");
   }

   public FileResource<?> getFinalArtifact()
   {
      PackagingFacet packaging = project.getFacet(PackagingFacet.class);
      FileResource<?> finalArtifact = (FileResource<?>) packaging.getFinalArtifact();
      if (!finalArtifact.exists())
      {
         throw new RuntimeException("Project final artifact not found. Did you 'build' it?");
      }
      return finalArtifact;
   }

   public FileResource<?> getDeployedArtifact()
   {
      DirectoryResource deployDir = getDeploymentDirectory();
      FileResource<?> deployedArtifact = (FileResource<?>) deployDir.getChild(getFinalArtifact().getName());
      return deployedArtifact;
   }

   public DirectoryResource getDeploymentDirectory()
   {
      DirectoryResource deployDir = getJBossHome().getChildDirectory(STANDALONE_DEPLOYMENTS);
      if (!deployDir.exists())
      {
         throw new RuntimeException("Directory does not exist [" + deployDir
                     + "], are you sure JBOSS_HOME points to a JBoss 7 installation?");
      }
      return deployDir;
   }

   public FileResource<?> getAS7Executable()
   {
      DirectoryResource binDir = getJBossHome().getChildDirectory("bin");
      if (!binDir.exists())
      {
         throw new RuntimeException("Directory does not exist [" + binDir
                     + "], are you sure JBOSS_HOME points to a JBoss 7 installation?");
      }

      return (FileResource<?>) binDir.getChild(getAS7ExecutableName());
   }

   private String getAS7ExecutableName()
   {
      return OSUtils.isWindows() ? "standalone.bat" : "standalone.sh";
   }

   private String getAS7ExecutableCommand()
   {
      return OSUtils.isWindows() ? "" : "sh ";
   }

   public DirectoryResource getJBossHome()
   {
      Map<String, String> env = System.getenv();
      if (!env.containsKey("JBOSS_HOME"))
      {
         throw new RuntimeException("$JBOSS_HOME must be set before deployment can continue.");
      }
      String path = env.get("JBOSS_HOME");
      if (path.startsWith("~"))
      {
         path = OSUtils.getUserHomePath() + path.substring(1);
      }
      File jbossHome = new File(path);
      return (DirectoryResource) factory.getResourceFrom(jbossHome);
   }
}
