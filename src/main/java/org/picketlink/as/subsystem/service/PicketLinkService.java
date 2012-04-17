package org.picketlink.as.subsystem.service;

import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceName;

public interface PicketLinkService<T> extends Service<T> {

    ServiceName getName();
    
}
