package org.jboss.forge.aesh;

import javax.inject.Inject;

import junit.framework.TestCase;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.Addon;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.container.AddonDependency;
import org.jboss.forge.container.AddonId;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Ignore
@RunWith(Arquillian.class)
public class AeshAddonTest extends TestCase
{

   // private KeyOperation completeChar = new KeyOperation(Key.CTRL_I, Operation.COMPLETE);

   public AeshAddonTest()
   {
   }

   @Deployment
   @Dependencies(@Addon(name = "org.jboss.forge:ui", version = "2.0.0-SNAPSHOT"))
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addPackages(true, ForgeShell.class.getPackage())
               .addAsLibraries(Maven.resolver().loadPomFromFile("pom.xml")
                        .resolve("org.jboss.aesh:aesh:0.33").withTransitivity().asFile())
               .addAsLibraries(Maven.resolver().loadPomFromFile("pom.xml")
                        .resolve("org.jboss.aesh:aesh-extensions:0.33").withTransitivity().asFile())
               .addBeansXML()
               .addAsAddonDependencies(AddonDependency.create(AddonId.from("org.jboss.forge:ui", "2.0.0-SNAPSHOT")));

      return archive;
   }

   @Inject
   private ForgeShell shell;

   @Inject
   private FooCommand fooCommand;

   @Test
   public void testContainerInjection()
   {
      /*
       * try {
       * 
       * Assert.assertNotNull(shell);
       * 
       * PipedOutputStream outputStream = new PipedOutputStream(); PipedInputStream pipedInputStream = new
       * PipedInputStream(outputStream); ByteArrayOutputStream out = new ByteArrayOutputStream();
       * 
       * setupSettings(pipedInputStream, out);
       * 
       * shell.initShell(); shell.addCommand(new ShellCommand(fooCommand));
       * 
       * outputStream.write(("foo\n").getBytes()); shell.startShell(); String outString = out.toString();
       * assertEquals("boo", outString.substring(shell.getPrompt().length() + "foo\n".length()));
       * 
       * outputStream.write("fo".getBytes()); outputStream.write(completeChar.getFirstValue());
       * outputStream.write("\n".getBytes()); shell.startShell(); outString = out.toString();
       * System.out.println(outString);
       * 
       * outputStream.write(("list-services\n").getBytes()); shell.startShell(); // System.out.println("OUT:"+
       * out.toString());
       * 
       * outputStream.write(("exit\n").getBytes()); shell.startShell();
       * 
       * // shell.stopShell(); } catch (Exception ioe) { ioe.printStackTrace(); }
       */
   }

   // private void setupSettings(InputStream input, OutputStream out)
   // {
   // Settings.getInstance().setName("test");
   // Settings.getInstance().setInputStream(input);
   // Settings.getInstance().setStdOut(out);
   // // aeshProducer.getSettings().setStdOut(new ByteArrayOutputStream());
   // if (!Config.isOSPOSIXCompatible())
   // Settings.getInstance().setAnsiConsole(false);
   //
   // Settings.getInstance().getOperationManager().addOperation(new KeyOperation(10, Operation.NEW_LINE));
   // }

}
