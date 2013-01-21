package org.jboss.forge.git.gitignore;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.util.Streams;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;

public class GitIgnorePluginTest extends AbstractShellTest
{
   @Test
   public void should_setup_gibo() throws Exception
   {
      // given
      initializeJavaProject();
      Resource<?> cloneFolder = doSetup();

      // then
      List<Resource<?>> resources = cloneFolder.listResources();
      assertNotNull(resources);
      assertFalse(resources.isEmpty());

      int counter = 0;
      for (Resource<?> resource : resources)
      {
         String name = resource.getName();
         if (name != null && name.endsWith(".gitignore"))
         {
            counter++;
         }
      }
      assertTrue(counter > 0);
   }

   @Test
   public void should_update_gibo() throws Exception
   {
      // given
      initializeJavaProject();

      doSetup();

      getShell().execute("gitignore update-repo");

      // then
      assertTrue(getOutput().contains("Local gitignore repository updated"));
   }

   @Test
   public void should_list_templates() throws Exception
   {
      // given
      initializeJavaProject();
      doSetup();

      getShell().execute("gitignore list-templates");

      // then
      String listOutput = getOutput().substring(getOutput().indexOf("==="));
      assertFalse(listOutput.contains(".gitignore"));
      assertTrue(listOutput.contains("= Languages ="));
      assertTrue(listOutput.contains("= Globals ="));
      assertTrue(listOutput.contains("Java"));
      assertTrue(listOutput.contains("Eclipse"));
   }

   @Test
   public void should_create_gitignore() throws Exception
   {
      // given
      initializeJavaProject();
      doSetup();
      getShell().execute("gitignore create Eclipse Maven");

      // then
      GitIgnoreResource gitignore = gitIgnoreResource();
      assertTrue(gitignore.exists());
      String content = Streams.toString(gitignore.getResourceInputStream());
      assertTrue(content.contains(".classpath"));
      assertTrue(content.contains("target/"));
   }

   @Test
   public void should_add_pattern() throws Exception
   {
      // given
      initializeJavaProject();

      doSetup();

      getShell().execute("gitignore create Eclipse");
      getShell().execute("gitignore-edit add *.forge");

      // then
      String content = Streams.toString(gitIgnoreResource().getResourceInputStream());
      assertTrue(content.contains("*.forge"));
   }

   @Test
   public void should_remove_pattern() throws Exception
   {
      // given
      initializeJavaProject();
      Resource<?> cloneFolder = cloneFolder();
      queueInputLines(cloneFolder.getFullyQualifiedName(), "\n");

      // when
      doSetup();
      getShell().execute("gitignore create Eclipse");
      getShell().execute("gitignore-edit remove .classpath");

      // then
      String content = Streams.toString(gitIgnoreResource().getResourceInputStream());
      assertFalse(content.contains(".classpath"));
   }

   private Resource<?> doSetup() throws Exception
   {
      Resource<?> cloneFolder = cloneFolder();
      queueInputLines(cloneFolder.getFullyQualifiedName(), "\n");

      getShell().execute("project install-facet forge.vcs.git");
      getShell().execute("gitignore setup");

      return cloneFolder;
   }

   private GitIgnoreResource gitIgnoreResource()
   {
      return getProject().getProjectRoot()
               .getChildOfType(GitIgnoreResource.class, ".gitignore");
   }

   private Resource<?> cloneFolder()
   {
      return getProject().getProjectRoot().getChildDirectory("gibo");
   }

}
