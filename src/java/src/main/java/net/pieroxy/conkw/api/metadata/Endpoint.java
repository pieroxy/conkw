package net.pieroxy.conkw.api.metadata;

import net.pieroxy.conkw.config.UserRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Endpoint {
  ApiMethod method();
  UserRole role();
}
