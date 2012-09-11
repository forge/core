package org.jboss.forge.arquillian.runner;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jboss.forge.container.event.Shutdown;
import org.jboss.forge.container.event.Startup;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Singleton
public class ServletTestServer
{
   private Server server;

   public void startTestServer(@Observes Startup event) throws Exception
   {
      // TODO allow custom ports
      server = new Server(4141);

      Connector connector = new SelectChannelConnector();
      connector.setHost("127.0.0.1");
      connector.setPort(4141);
      server.setConnectors(new Connector[] { connector });

      ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
      context.setContextPath("/");
      server.setHandler(context);

      context.addServlet(new ServletHolder(new ServletTestRunner()), "/ArquillianServletRunner");
      server.start();

      System.out.println("Remote test server started.");
   }

   public void stopTestServer(@Observes Shutdown event) throws Exception
   {
      server.stop();
      System.out.println("Remote test server stopped.");
   }
}