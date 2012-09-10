package org.jboss.forge.arquillian.runner;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.forge.container.event.Shutdown;
import org.jboss.forge.container.event.Startup;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Singleton
public class RemoteTestServer
{
   @Inject
   private ServletTestRunner handler;

   private Server server;

   public void startTestServer(@Observes Startup event) throws Exception
   {
      System.out.println("Remote test server starting up.");

      // TODO allow custom ports
      server = new Server(4141);
      ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
      context.setContextPath("/");
      server.setHandler(context);

      context.addServlet(new ServletHolder(handler), "/ArquillianServletRunner");
      server.start();

      System.out.println("Remote test server started.");
   }

   public void stopTestServer(@Observes Shutdown event) throws Exception
   {
      server.stop();
   }
}