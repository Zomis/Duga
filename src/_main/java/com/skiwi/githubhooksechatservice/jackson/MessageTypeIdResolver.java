package com.skiwi.githubhooksechatservice.jackson;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class MessageTypeIdResolver implements TypeIdResolver {

	private JavaType mBaseType;
	
	@Override
	public void init(JavaType baseType) {
		mBaseType = baseType;
	}

	@Override
	public Id getMechanism() {
		return Id.CUSTOM;
	}

	@Override
	public String idFromValue(Object obj) {
		return idFromValueAndType(obj, obj.getClass());
	}

	@Override
	public String idFromBaseType() {
		throw new AssertionError("this should never happen");
	}

	@Override
	public String idFromValueAndType(Object obj, Class<?> clazz) {
		return clazz.getSimpleName();// AnySetterJSONObject mess = (AnySetterJSONObject) obj;
//		return mess.getCommand();
	}

	@Override
	public JavaType typeFromId(String type) {
		Class<?> clazz;
		try {
			clazz = Class.forName("com.skiwi.githubhooksechatservice.events.github." + type);
		} catch (ClassNotFoundException e) {
			throw new UnsupportedOperationException("No such defined type: " + type);
		}
		return TypeFactory.defaultInstance().constructSpecializedType(mBaseType, clazz);
	}
} 