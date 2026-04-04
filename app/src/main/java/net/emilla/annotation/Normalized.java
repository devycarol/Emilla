package net.emilla.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/// Denotes that a string or character value must be "normalized" (simplified) to a callee's locale.
/// This includes, for example, conversion to standard letter-case and removal of diacritics.
@Retention(RetentionPolicy.SOURCE)
@Target({
    ElementType.FIELD,
    ElementType.METHOD,
    ElementType.PARAMETER,
    ElementType.LOCAL_VARIABLE,
    ElementType.TYPE_PARAMETER,
    ElementType.TYPE_USE,
    ElementType.RECORD_COMPONENT,
})
public @interface Normalized {
}
