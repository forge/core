package org.jboss.forge.arquillian.protocol;

import java.net.URI;

import org.jboss.arquillian.container.test.spi.client.protocol.ProtocolConfiguration;

public class ServletProtocolConfiguration implements ProtocolConfiguration
{
   private String host = null;
   private Integer port = null;
   private String contextRoot = null;;

   /**
    * @return the host
    */
   public String getHost()
   {
      return host;
   }

   /**
    * @param host the host to set
    */
   public void setHost(String host)
   {
      this.host = host;
   }

   /**
    * @return the port
    */
   public Integer getPort()
   {
      return port;
   }

   /**
    * @param port the port to set
    */
   public void setPort(Integer port)
   {
      this.port = port;
   }

   /**
    * @return the context
    */
   public String getContextRoot()
   {
      return contextRoot;
   }

   /**
    * @param context the context to set
    */
   public void setContextRoot(String context)
   {
      this.contextRoot = context;
   }

   public URI getBaseURI()
   {
      return URI.create("http://" + host + ":" + port + "/" + contextRoot);
   }
}
