/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.dependency;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.jboss.forge.parser.xml.Node;

public class AddonLoader
{
   private Set<Coordinate> artifactCoordinates = new HashSet<Coordinate>();

   private Logger log = Logger.getLogger(getClass().getName());

   public void load(Node root) throws IOException
   {
      Node installedAddons = root.getSingle("installed");
      if (installedAddons != null)
      {
         for (Node addonChild : installedAddons.getChildren())
         {
            Map<String, String> atts = addonChild.getAttributes();
            Coordinate c = CoordinateBuilder.create(atts);
            log.info("Adding to registry: " + c);
            artifactCoordinates.add(c);
         }
      }
   }

   public Set<Coordinate> getCoordinates()
   {
      return Collections.unmodifiableSet(artifactCoordinates);
   }
}
