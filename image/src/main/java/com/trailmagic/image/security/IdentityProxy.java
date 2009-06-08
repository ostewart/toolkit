package com.trailmagic.image.security;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/** Take note that things will blow up if this is set on a field that could be null **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface IdentityProxy {
}