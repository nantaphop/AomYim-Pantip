package com.nantaphop.pantipfanapp.pref;

import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by nantaphop on 07-Sep-14.
 */
@SharedPref(SharedPref.Scope.APPLICATION_DEFAULT)
public interface UserPref {

    String username();
    String avatar();
    String bio();
    String password();
}
