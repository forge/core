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
package org.jboss.forge.git;

import javax.inject.Inject;

import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyInstaller;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.shell.plugins.Alias;

/**
 * @author <a href="mailto:jevgeni.zelenkov@gmail.com">Jevgeni Zelenkov</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("forge.vcs.git.api")
public class GitAPIFacet extends BaseFacet
{
   private static final Dependency GIT_DEPENDENCY = DependencyBuilder.create()
            .setGroupId("org.eclipse.jgit")
            .setArtifactId("org.eclipse.jgit.pgm");

   @Inject
   public DependencyInstaller installer;

   @Override
   public boolean install()
   {
      installer.install(project, GIT_DEPENDENCY);
      return true;
   }

   @Override
   public boolean isInstalled()
   {
      return installer.isInstalled(project, GIT_DEPENDENCY);
   }

}