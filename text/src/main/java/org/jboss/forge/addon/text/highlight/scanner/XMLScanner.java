/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.text.highlight.scanner;


public class XMLScanner extends HTMLScanner {

   public static final Type TYPE = new Type("XML", "\\.(xml|cfc|cfm|tmproj|xaml)$");

   @Override
   public Type getType() {
      return TYPE;
   }
}
