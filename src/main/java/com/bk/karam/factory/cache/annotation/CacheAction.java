package com.bk.karam.factory.cache.annotation;

public enum CacheAction {

	/**
	 * 指定系统缓存结果
	 */
	CACHE,
	/**
	 * 让系统跳过缓存
	 */
	IGNORE,
	/**
	 * 让系统清除缓存
	 */
	CLEAN

}
