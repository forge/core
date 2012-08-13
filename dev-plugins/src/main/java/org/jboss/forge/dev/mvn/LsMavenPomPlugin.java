/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.forge.dev.mvn;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;

import org.apache.maven.model.Dependency;
import org.jboss.forge.maven.resources.MavenDependencyResource;
import org.jboss.forge.maven.resources.MavenPomResource;
import org.jboss.forge.maven.resources.MavenProfileResource;
import org.jboss.forge.maven.resources.MavenRepositoryResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Current;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresResource;
import org.jboss.forge.shell.plugins.Topic;

/**
 * LsMavenPomPlugin
 * 
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
@Alias("ls")
@RequiresResource(MavenPomResource.class)
@Topic("File & Resources")
@Help("Prints the contents current pom file")
public class LsMavenPomPlugin implements Plugin
{
   @Inject
   @Current
   private MavenPomResource pom;

   @DefaultCommand
   public void run(
            @Option(flagOnly = true, name = "all", shortName = "a", required = false) final boolean showAll,
                   @Option(flagOnly = true, name = "list", shortName = "l", required = false) final boolean list,
                   @Option(description = "path", defaultValue = ".") final Resource<?>[] paths,
                   final PipeOut out) throws IOException
   {
      if (showAll)
      {
         InputStream stream = pom.getResourceInputStream();
         StringBuilder buf = new StringBuilder();

         int c;
         while ((c = stream.read()) != -1)
         {
            buf.append((char) c);
         }
         out.println(buf.toString());
      }
      else
      {

         out.println();
         out.println(out.renderColor(ShellColor.RED, "[dependencies] "));
         List<Resource<?>> children = pom.listResources();
         for (Resource<?> child : children)
         {
            if (child instanceof MavenDependencyResource)
            {
               MavenDependencyResource resource = (MavenDependencyResource) child;
               Dependency dep = resource.getDependency();
               out.println(
                        out.renderColor(ShellColor.BLUE, dep.getGroupId())
                                 +
                                 out.renderColor(ShellColor.BOLD, " : ")
                                 +
                                 out.renderColor(ShellColor.BLUE, dep.getArtifactId())
                                 +
                                 out.renderColor(ShellColor.BOLD, " : ")
                                 +
                                 out.renderColor(ShellColor.NONE, dep.getVersion() == null ? "" : dep.getVersion())
                                 +
                                 out.renderColor(ShellColor.BOLD, " : ")
                                 +
                                 out.renderColor(ShellColor.NONE, dep.getType() == null ? "" : dep
                                          .getType().toLowerCase())
                                 +
                                 out.renderColor(ShellColor.BOLD, " : ")
                                 +
                                 out.renderColor(determineDependencyShellColor(dep.getScope()),
                                          dep.getScope() == null ? "compile" : dep.getScope()
                                                   .toLowerCase()));
            }
         }

         out.println();
         out.println(out.renderColor(ShellColor.RED, "[profiles] "));

         for (Resource<?> child : children)
         {
            if (child instanceof MavenProfileResource)
            {
               out.println(out.renderColor(ShellColor.BLUE, child.getName()));
            }
         }

         out.println();
         out.println(out.renderColor(ShellColor.RED, "[repositories] "));

         for (Resource<?> child : children)
         {
            if (child instanceof MavenRepositoryResource)
            {
               out.println(out.renderColor(ShellColor.BLUE, child.getName()) + " -> "
                        + ((MavenRepositoryResource) child).getURL());
            }
         }

      }
   }

   private ShellColor determineDependencyShellColor(final String string)
   {
      if (string == null)
      {
         return ShellColor.YELLOW;
      }
      if ("provided".equalsIgnoreCase(string))
         return ShellColor.GREEN;
      else if ("compile".equalsIgnoreCase(string))
         return ShellColor.YELLOW;
      else if ("runtime".equalsIgnoreCase(string))
         return ShellColor.MAGENTA;
      else if ("system".equalsIgnoreCase(string))
         return ShellColor.BLACK;
      else if ("test".equalsIgnoreCase(string))
         return ShellColor.BLUE;

      return ShellColor.NONE;
   }
}