/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.scaffold.faces.metawidget.processor;

import static org.jboss.forge.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.*;
import static org.metawidget.inspector.InspectionResultConstants.*;

import java.util.Map;

import org.metawidget.inspectionresultprocessor.iface.InspectionResultProcessor;
import org.metawidget.statically.StaticMetawidget;
import org.metawidget.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Thomas Frühbeck
 */
public class ForgeInspectionResultProcessor implements InspectionResultProcessor<StaticMetawidget>
{
   @Override
   public String processInspectionResult(String inspectionResult, StaticMetawidget metawidget, Object toInspect,
            String type, String... names)
   {
      Document document = XmlUtils.documentFromString(inspectionResult);
      NodeList entities = document.getElementsByTagName(ENTITY);

      if (entities.getLength() > 0)
      {
         for (int i = 0; i < entities.getLength(); i++)
         {
            Node entity = entities.item(i);
            Map<String, String> attributes = XmlUtils.getAttributesAsMap(entity);

            String primaryKey = attributes.get(PRIMARY_KEY);
            if (null != primaryKey)
            {

               NodeList properties = document.getElementsByTagName(PROPERTY);
               if (properties.getLength() > 0)
               {
                  for (int j = 0; j < properties.getLength(); j++)
                  {
                     Element property = (Element) properties.item(j);
                     Map<String, String> propAttribs = XmlUtils.getAttributesAsMap(property);
                     propAttribs.put(ENTITY_PRIMARY_KEY, primaryKey);

                     XmlUtils.setMapAsAttributes(property, propAttribs);
                  }
               }
            }
         }
      }

      return XmlUtils.documentToString(document, false);
   }
}
