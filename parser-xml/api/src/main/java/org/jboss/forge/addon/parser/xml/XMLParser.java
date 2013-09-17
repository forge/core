/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.xml;

import java.io.InputStream;

import org.jboss.forge.furnace.services.Exported;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 */

@Exported
public interface XMLParser
{
   InputStream toXMLInputStream(final Node node);

   String toXMLString(final Node node);

   byte[] toXMLByteArray(final Node node);

   Node parse(final byte[] xml);

   Node parse(final String xml);

   Node parse(final InputStream stream) throws XMLParserException;

}
