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
package org.jboss.forge.spec.javaee.descriptor;

import java.util.List;

import org.jboss.shrinkwrap.descriptor.api.Descriptor;

/**
 * Bean Validation configuration descriptor
 * 
 * @author Kevin Pollet
 */
public interface ValidationDescriptor extends Descriptor
{
   ValidationDescriptor defaultProvider(String defaultProvider);

   ValidationDescriptor messageInterpolator(String messageInterpolator);

   ValidationDescriptor traversableResolver(String traversableResolver);

   ValidationDescriptor constraintValidatorFactory(String constraintValidatorFactory);

   ValidationDescriptor constraintMapping(String constraintMapping);

   String getDefaultProvider();

   String getMessageInterpolator();

   String getTraversableResolver();

   String getConstraintValidatorFactory();

   List<String> getConstraintMappings();
}
