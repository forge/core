package org.jboss.forge.addon.parser.xml;

import java.io.InputStream;

import org.jboss.forge.furnace.services.Exported;

@Exported
public interface HelloService
{
   void sayHello();

   InputStream toXMLInputStream(final Node node);

   String toXMLString(final Node node);

   byte[] toXMLByteArray(final Node node);

   Node parse(final byte[] xml);

   Node parse(final String xml);

   Node parse(final InputStream stream) throws XMLParserException;

}
