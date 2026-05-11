package com.example.mongo.user.routes;

import com.example.mongo.base.routes.BaseRoutes;

public class UserRoutes {
    private final static String ROOT = BaseRoutes.API + "/user";
    public final static String REGISTRATION = BaseRoutes.NOT_SECURED + "/registration";
    public final static String EDIT = ROOT;
    public final static String BY_ID = ROOT + "/{id}";
    public final static String SEARCH = ROOT;
    public final static String INIT = BaseRoutes.NOT_SECURED + "/init";

}
