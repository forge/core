package org.jboss.forge.aesh;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Ignore
@RunWith(Arquillian.class)
public class AeshAddonTest
{
   private KeyOperation completeChar = new KeyOperation(Key.CTRL_I, Operation.COMPLETE);

   @Deployment
   @Dependencies({ @Addon(name = "org.jboss.forge:ui", version = "2.0.0-SNAPSHOT"),
            @Addon(name = "org.jboss.forge:aesh", version = "2.0.0-SNAPSHOT") })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addClasses(FooCommand.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge:ui", "2.0.0-SNAPSHOT")),
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge:aesh", "2.0.0-SNAPSHOT"))
               );

      return archive;
   }

   @Inject
   private ForgeShell shell;

   @Inject
   private FooCommand fooCommand;

   @Test
   public void testContainerInjection() throws Exception
   {
      Assert.assertNotNull(shell);

      PipedOutputStream outputStream = new PipedOutputStream();
      PipedInputStream pipedInputStream = new PipedInputStream(outputStream);
      ByteArrayOutputStream out = new ByteArrayOutputStream();

      setupSettings(pipedInputStream, out);

      shell.addCommand(new ShellCommand(fooCommand, shell));

      outputStream.write(("foo\n").getBytes());
      shell.stopShell();
      shell.startShell();
      String outString = out.toString();
      Assert.assertEquals("boo", outString.substring(shell.getPrompt().length() + "foo\n".length()));

      outputStream.write("fo".getBytes());
      outputStream.write(completeChar.getFirstValue());
      outputStream.write("\n".getBytes());
      shell.stopShell();
      shell.startShell();
      outString = out.toString();

      outputStream.write(("list-services\n").getBytes());
      shell.stopShell();
      shell.startShell();
      System.out.println("OUT:" + out.toString());

      outputStream.write(("exit\n").getBytes());
      shell.stopShell();
      shell.startShell();

      shell.stopShell();
   }

   private void setupSettings(InputStream input, OutputStream out)
   {
      Settings.getInstance().setName("test");
      Settings.getInstance().setInputStream(input);
      Settings.getInstance().setStdOut(out);
      // aeshProducer.getSettings().setStdOut(new ByteArrayOutputStream());
      if (!Config.isOSPOSIXCompatible())
         Settings.getInstance().setAnsiConsole(false);

      Settings.getInstance().getOperationManager().addOperation(new KeyOperation(Key.ENTER, Operation.NEW_LINE));
   }

}
