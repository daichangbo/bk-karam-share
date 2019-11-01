package com.bk.karam.factory.cache.annotation;

import java.util.EventObject;

public class CacheKeyGenerationEvent extends EventObject{


	private static final long serialVersionUID = -1654713316883644353L;

	private final String name;

	private final Object[] args;

	public CacheKeyGenerationEvent(Object source, String name, Object[] args) {
		super(source);
		this.name = name;
		this.args = args;
	}

	public String getName() {
		return name;
	}

	public Object[] getArgs() {
		return args;
	}
}
