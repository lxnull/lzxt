package com.lx.xt.sso.service.impl;

import com.lx.xt.common.service.ServiceTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractService {

    @Autowired
    protected ServiceTemplate serviceTemplate;
}