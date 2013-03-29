package org.jboss.forge.parser.java.binary;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.Member;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.parser.java.MethodHolder;
import org.jboss.forge.parser.java.Parameter;

/**
 * a wrapped method holder implemented as a delegate.
 * @author john
 *
 * @param <O>
 */
public class WrappedMethodHolder<O extends JavaSource<O>> implements MethodHolder<O> {

	private final Class<?> clzz;
	
	/* the wrapped javasource delegating to this wrapped method holder */
	private final O delegatee;
	
	public WrappedMethodHolder(O delegatee, Class<?> c) {
		clzz = c;
		this.delegatee = delegatee;
	}
	

	@Override
	public List<Member<O, ?>> getMembers() {
		List<Member<O, ?>> result = new ArrayList<Member<O,?>>();
		result.addAll(getMethods());
		return result;
	}


	@Override
	public Method<O> addMethod() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Method<O> addMethod(String method) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasMethod(Method<O> name) {
		return hasMethodSignature(name);
	}

	@Override
	public boolean hasMethodSignature(Method<?> method) {
		List<String> pnames = new ArrayList<String>();
		for(Parameter<?> p: method.getParameters()) {
			pnames.add(p.getName());
		}
		return hasMethodSignature(method.getName(), (String [])pnames.toArray());
	}

	@Override
	public boolean hasMethodSignature(String name) {
		return hasMethodSignature(name, new Class<?>[0]);
	}

	@Override
	public boolean hasMethodSignature(String name, String... paramTypes) {
		return getMethod(name, paramTypes) != null;
	}

	@Override
	public boolean hasMethodSignature(String name, Class<?>... paramTypes) {
		return getMethod(name, paramTypes) != null;
	}

	@Override
	public Method<O> getMethod(String name) {
		return getMethod(name, new String[0]);
	}

	@Override
	public Method<O> getMethod(String name, String... paramTypes) {
		List<Class<?>> pl = new ArrayList<Class<?>>();
		for(String pType: paramTypes) {
			try {
				Class<?> pclass = Class.forName(pType);
				pl.add(pclass);
			} catch (ClassNotFoundException e) {
				// TODO: should we quietly return false instead?
				throw new RuntimeException(e);
			}
		}
		return getMethod(name, (Class<?>[])pl.toArray(new Class<?>[0]));
	}

	@Override
	public Method<O> getMethod(String name, Class<?>... paramTypes) {
		Method<O> result = null;
		try {
			result = new WrappedMethod<O>(this, clzz.getDeclaredMethod(name, paramTypes));
		} catch (NoSuchMethodException e) {
		}
		return result;
	}

	public O getDelegatee() {
		return delegatee;
	}


	@Override
	public List<Method<O>> getMethods() {
		List<Method<O>> result = new ArrayList<Method<O>>();
		for(java.lang.reflect.Method m: clzz.getDeclaredMethods()) {
			result.add(new WrappedMethod<O>(this, m));
		}
		return result;
	}

	@Override
	public O removeMethod(Method<O> method) {
		// TODO Auto-generated method stub
		return null;
	}


}
