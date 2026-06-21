package com.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Dit à Java de garder cette étiquette visible même pendant que le programme s'exécute.
@Retention(RetentionPolicy.RUNTIME)
// Dit à Java que cette étiquette ne peut être collée que sur des classes, des interfaces ou des enums.
@Target(ElementType.TYPE)
public @interface JsonSerializable {
    // default : si l'utilisateur ne précise rien, Java utilisera configuration.json
    String nomFichier() default "configuration.json";
}
