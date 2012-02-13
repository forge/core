/*
 * JBoss, by Red Hat.
 * Copyright 2010, Red Hat, Inc., and individual contributors
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
package org.jboss.forge.resources.enumtype;

import org.jboss.forge.parser.java.JavaEnum;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.java.JavaMethodResource;

/**
 * @author Ricardo Martinelli
 */
public class EnumMethodResource extends JavaMethodResource {

   public EnumMethodResource(Resource<?> parent, Method<? extends JavaEnum> method)
	{
		super(parent, method);
	}
}
