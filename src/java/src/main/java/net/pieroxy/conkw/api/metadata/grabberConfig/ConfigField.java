package net.pieroxy.conkw.api.metadata.grabberConfig;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)

public @interface ConfigField {
  String label();
  String defaultValue() default "";
  /**
   * Applicable only to List fields
   */
  String listItemLabel() default "";
  String[]listOfChoices() default {};
  String listOfChoicesFunc() default "";
}
