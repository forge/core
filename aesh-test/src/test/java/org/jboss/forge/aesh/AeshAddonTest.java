package org.jboss.forge.aesh;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.inject.Inject;

import junit.framework.TestCase;

import org.jboss.aesh.console.Config;
import org.jboss.aesh.console.settings.Settings;
import org.jboss.aesh.edit.KeyOperation;
import org.jboss.aesh.edit.actions.Operation;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.Addon;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.container.AddonDependency;
import org.jboss.forge.container.AddonId;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class AeshAddonTest extends TestCase
{

   private KeyOperation completeChar = new KeyOperation(9, Operation.COMPLETE);

   public AeshAddonTest()
   {
   }

   @Deployment
   @Dependencies({
            @Addon(name = "org.jboss.forge:ui", version = "2.0.0-SNAPSHOT"),
            @Addon(name = "org.jboss.forge:aesh", version = "2.0.0-SNAPSHOT"),
            @Addon(name = "org.jboss.forge:aesh-test", version = "2.0.0-SNAPSHOT")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addAsManifestResource(EmptyAsset.INSTANCE, ArchivePaths.create("beans.xml"))
               .addAsAddonDependencies(AddonDependency.create(AddonId.from("org.jboss.forge:ui", "2.0.0-SNAPSHOT")))
               .addAsAddonDependencies(
                        AddonDependency.create(AddonId.from("org.jboss.forge:aesh-test", "2.0.0-SNAPSHOT")));

      return archive;
   }

   @Inject
   private ShellStreamProvider streamProvider;

   @Inject
   private ForgeShell shell;

   @Inject
   private FooCommand fooCommand;

   @Test
   public void testContainerInjection()
   {
      try
      {

         Assert.assertNotNull(shell);

         PipedOutputStream outputStream = new PipedOutputStream();
         PipedInputStream pipedInputStream = new PipedInputStream(outputStream);
         ByteArrayOutputStream out = new ByteArrayOutputStream();

         setupSettings(pipedInputStream, out);

         shell.initShell();
         shell.addCommand(new ShellCommand(fooCommand, shell));

         outputStream.write(("foo\n").getBytes());
         shell.startShell();
         String outString = out.toString();
         assertEquals("boo",
                  outString.substring(shell.getPrompt().length() + "foo\n".length()));

         outputStream.write("fo".getBytes());
         outputStream.write(completeChar.getFirstValue());
         outputStream.write("\n".getBytes());
         shell.startShell();
         outString = out.toString();
         System.out.println(outString);

         outputStream.write(("list-services\n").getBytes());
         shell.startShell();
         // System.out.println("OUT:"+ out.toString());

         outputStream.write(("exit\n").getBytes());
         shell.startShell();

         // shell.stopShell();
      }
      catch (Exception ioe)
      {
         ioe.printStackTrace();
      }
   }

   private void setupSettings(InputStream input, OutputStream out)
   {
      streamProvider.setInputStream(input);
      streamProvider.setOutputStream(out);
      Settings.getInstance().setName("test");
      // Settings.getInstance().setInputStream(input);
      // Settings.getInstance().setStdOut(out);
      // aeshProducer.getSettings().setStdOut(new ByteArrayOutputStream());
      if (!Config.isOSPOSIXCompatible())
         Settings.getInstance().setAnsiConsole(false);

      Settings.getInstance().getOperationManager().addOperation(new KeyOperation(10, Operation.NEW_LINE));
   }

}
