package org.picketlink.as.subsystem.model.event;

public interface IdentityProviderURLObserver extends Observer {

    void onUpdateIdentityURL(String identityURL);
    
}
