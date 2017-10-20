package mps.bachelor2017.bfh.ti.ch.mobiltypricing.interfaces;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Pascal on 20.10.2017.
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface DBField {
    public String Name();
    public boolean PrimaryKey() default false;
}