package mps.bachelor2017.bfh.ti.ch.mobiltypricing.interfaces;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Pascal on 20.10.2017.
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface DBClass {
    public String Name();
}