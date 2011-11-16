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
package org.jboss.forge.scaffold.faces.metawidget.inspector;

import static org.metawidget.inspector.faces.FacesInspectionResultConstants.*;

import java.util.Map;

import javax.persistence.ManyToOne;

import org.metawidget.inspector.impl.BaseObjectInspector;
import org.metawidget.inspector.impl.BaseObjectInspectorConfig;
import org.metawidget.inspector.impl.propertystyle.Property;
import org.metawidget.statically.faces.StaticFacesUtils;
import org.metawidget.util.ClassUtils;
import org.metawidget.util.CollectionUtils;
import org.metawidget.util.simple.StringUtils;

/**
 * @author Richard Kennard
 */

public class ForgeInspector
         extends BaseObjectInspector
{

   //
   // Constructor
   //

   public ForgeInspector()
   {

      this(new BaseObjectInspectorConfig());
   }

   public ForgeInspector(BaseObjectInspectorConfig config)
   {

      super(config);
   }

   //
   // Protected methods
   //

   @Override
   protected Map<String, String> inspectProperty(Property property)
            throws Exception
   {

      Map<String, String> attributes = CollectionUtils.newHashMap();

      // ManyToOne

      if (property.isAnnotationPresent(ManyToOne.class))
      {
         attributes.put(FACES_LOOKUP,
                  StaticFacesUtils.wrapExpression(StringUtils.lowercaseFirstLetter(ClassUtils.getSimpleName(property.getType())) + "Bean.all"));
         attributes.put(FACES_CONVERTER_ID,
                  StaticFacesUtils.wrapExpression(StringUtils.lowercaseFirstLetter(ClassUtils.getSimpleName(property.getType())) + "Bean.converter"));
      }

      return attributes;
   }
}
