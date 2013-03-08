package org.jboss.forge.parser.java.binary;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.parser.java.InterfaceCapable;
import org.jboss.forge.parser.java.JavaInterface;
import org.jboss.forge.parser.java.JavaSource;

public class WrappedInterfaceCapable<O extends JavaSource<O>> implements InterfaceCapable<O> {

	private final Class<?> clzz;
	private final O delegatee;

	public WrappedInterfaceCapable(O delegatee, Class<?> c) {
		clzz = c;
		this.delegatee = delegatee;
	}
	

	@Override
	public List<String> getInterfaces() {
		List<String> result = new ArrayList<String>();
		for(Class<?> i: clzz.getInterfaces()) {
			result.add(i.getName());
		}
		return result;
	}

	@Override
	public O addInterface(String type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public O addInterface(Class<?> type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public O addInterface(JavaInterface type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasInterface(String type) {
		boolean result = false;
		for(Class<?> i: clzz.getInterfaces()) {
			result = i.getName().equals(type);
			if(result) {
				break;
			}
		}
		return result;
	}

	@Override
	public boolean hasInterface(Class<?> type) {
		return hasInterface(type.getName());
	}

	@Override
	public boolean hasInterface(JavaInterface type) {
		return hasInterface(type.getName());
	}

	@Override
	public O removeInterface(String type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public O removeInterface(Class<?> type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public O removeInterface(JavaInterface type) {
		// TODO Auto-generated method stub
		return null;
	}
}
