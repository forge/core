package org.jboss.forge.parser.java.binary;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.FieldHolder;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.Member;

public class WrappedFieldHolder<O extends JavaSource<O>> implements FieldHolder<O> {

	private final Class<?> clzz;
	private final O delegatee;

	public WrappedFieldHolder(O delegatee, Class<?> c) {
		clzz = c;
		this.delegatee = delegatee;
	}
	

	public List<Member<O, ?>> getMembers() {
		List<Member<O, ?>> result = new ArrayList<Member<O,?>>();
		result.addAll(getFields());
		return result;
	}


	@Override
	public Field<O> addField() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Field<O> addField(String declaration) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasField(String name) {
		boolean result = true;
		try {
			clzz.getDeclaredField(name);
		} catch (NoSuchFieldException e) {
			result = false;
		}

		return result;
	}

	@Override
	public boolean hasField(Field<O> field) {
		return hasField(field.getName());
	}

	@Override
	public Field<O> getField(String name) {
		Field<O> result = null;
		try {
			java.lang.reflect.Field field = clzz.getDeclaredField(name);
			result = new WrappedField<O>(this, field);
		} catch (NoSuchFieldException e) {
		}
		return result;
	}

	@Override
	public List<Field<O>> getFields() {
		List<Field<O>> result = new ArrayList<Field<O>>();
		for(java.lang.reflect.Field f: clzz.getDeclaredFields()) {
			result.add(new WrappedField<O>(this, f));
		}
		return result;
	}

	@Override
	public O removeField(Field<O> field) {
		// TODO Auto-generated method stub
		return null;
	}

	public O getDelegatee() {
		return delegatee;
	}



}
