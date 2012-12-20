package org.jboss.forge.arquillian.protocol;

import org.jboss.arquillian.container.spi.client.protocol.ProtocolDescription;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ServletProtocolDescription extends ProtocolDescription
{
   public ServletProtocolDescription()
   {
      super("_FORGE_2_");
   }
}
