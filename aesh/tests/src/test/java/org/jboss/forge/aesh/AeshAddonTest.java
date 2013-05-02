package org.jboss.forge.aesh;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.inject.Inject;

import org.jboss.aesh.console.Config;
import org.jboss.aesh.console.settings.Settings;
import org.jboss.aesh.edit.KeyOperation;
import org.jboss.aesh.edit.actions.Operation;
import org.jboss.aesh.terminal.Key;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.Addon;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.container.addons.AddonId;
import org.jboss.forge.container.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class AeshAddonTest
{
   private KeyOperation completeChar = new KeyOperation(Key.CTRL_I, Operation.COMPLETE);

   @Deployment
   @Dependencies({ @Addon(name = "org.jboss.forge:ui", version = "2.0.0-SNAPSHOT"),
            @Addon(name = "org.jboss.forge:aesh-test-harness", version = "2.0.0-SNAPSHOT"),
            @Addon(name = "org.jboss.forge:aesh", version = "2.0.0-SNAPSHOT"),
            @Addon(name = "org.jboss.forge:resources", version = "2.0.0-SNAPSHOT")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addClasses(FooCommand.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge:ui", "2.0.0-SNAPSHOT")),
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge:aesh", "2.0.0-SNAPSHOT")),
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge:aesh-spi", "2.0.0-SNAPSHOT")),
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge:aesh-test-harness", "2.0.0-SNAPSHOT")),
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge:resources", "2.0.0-SNAPSHOT"))
               );

      return archive;
   }

   @Inject
   private ForgeShell shell;
   
   @Inject
   private TestShellStreamProvider shellStreamProvider;

   @Inject
   private FooCommand fooCommand;

   PipedOutputStream outputStream;
   ByteArrayOutputStream out;

   @Test
   public void testContainerInjection() throws Exception
   {
      Assert.assertNotNull(shell);

      reset();

      shell.addCommand(new ShellCommand(fooCommand, shell));

      outputStream.write(("foo\n").getBytes());
      System.out.println("OUT:" + out.toString());

      String prompt = shell.getPrompt();
      Assert.assertEquals("[forge]$ ", prompt);

      outputStream.write("fo".getBytes());
      outputStream.write(completeChar.getFirstValue());
      System.out.println("OUT:" + out.toString());
      outputStream.write("\n".getBytes());
      System.out.println("OUT:" + out.toString());
      
      reset();

      outputStream.write(("list-services\n").getBytes());
      System.out.println("OUT:" + out.toString());
      
      reset();
      

      outputStream.write(("exit\n").getBytes());
      System.out.println("OUT:" + out.toString());
      
      reset();

      shell.stopShell();
   }

   private void reset()
   {
      try
      {
         shell.stopShell();
         
         outputStream = new PipedOutputStream();
         out = new ByteArrayOutputStream();
         
         Settings.getInstance().setName("test");
         Settings.getInstance().setInputStream(new PipedInputStream(outputStream));
         Settings.getInstance().setStdOut(out);
         
         if (!Config.isOSPOSIXCompatible())
            Settings.getInstance().setAnsiConsole(false);

         Settings.getInstance().getOperationManager().addOperation(new KeyOperation(Key.ENTER, Operation.NEW_LINE));

         shell.startShell();
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

}
