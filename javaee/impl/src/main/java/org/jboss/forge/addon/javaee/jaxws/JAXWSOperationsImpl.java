/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jaxws;

import javax.jws.WebService;

import org.jboss.forge.roaster.model.source.JavaClassSource;

/**
 * @author <a href="mailto:antonio.goncalves@gmail.com">Antonio Goncalves</a>
 */
public class JAXWSOperationsImpl implements JAXWSOperations
{

   @Override
   public JavaClassSource newWebService(JavaClassSource source)
   {
      // Class
      source.addAnnotation(WebService.class);
      return source;
   }
}