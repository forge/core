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

import java.util.Collections;
import java.util.List;

import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.FieldHolder;
import org.jboss.forge.parser.java.JavaEnum;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.ResourceFlag;
import org.jboss.forge.resources.java.JavaMemberResource;

/**
 * 
 * @author Ricardo Martinelli
 *
 */
public class EnumFieldResource extends JavaMemberResource<Field<? extends JavaEnum>>
{
	private final Field<? extends JavaEnum> field;
	
   public EnumFieldResource(final EnumTypeResource parent, final Field<? extends JavaEnum> field)
	{
		super(parent, field);
		this.field = field;
		
		setFlag(ResourceFlag.Leaf);
	}

	@Override
	public Resource<Field<? extends JavaEnum>> createFrom(final Field<? extends JavaEnum> file)
	{
		throw new RuntimeException("not implemented");
	}
	
	@Override
	public List<Resource<?>> listResources()
	{
		return Collections.emptyList();
	}
	
	@Override
	public Field<? extends JavaEnum> getUnderlyingResourceObject()
	{
		return field;
	}

	@Override
	public String getName()
	{
		return field.getName().toUpperCase();
	}

	@Override
	public String toString()
	{
		return field.toString();
	}
	
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean delete() throws UnsupportedOperationException
	{
		JavaEnum origin = field.getOrigin();
		if (origin instanceof FieldHolder)
		{
			((FieldHolder)origin).removeField(field);
			if (!((FieldHolder)origin).hasField(field))
			{
	            ((EnumTypeResource) this.getParent()).setContents(origin.toString());
	            return true;
			}
		}
		return false;
	}

	@Override
	public boolean delete(boolean recursive) throws UnsupportedOperationException
	{
		return delete();
	}
}
