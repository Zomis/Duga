package com.skiwi.githubhooksechatservice.events.github;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.skiwi.githubhooksechatservice.events.AnySetterJSONObject;
import com.skiwi.githubhooksechatservice.jackson.MessageTypeIdResolver;

@JsonTypeInfo(use = Id.CUSTOM, property = "type", include = As.PROPERTY)
@JsonTypeIdResolver(MessageTypeIdResolver.class)
public class AbstractEvent extends AnySetterJSONObject {

}
