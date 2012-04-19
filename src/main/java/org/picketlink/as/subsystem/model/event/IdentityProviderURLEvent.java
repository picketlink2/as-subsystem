package org.picketlink.as.subsystem.model.event;


public class IdentityProviderURLEvent implements Event<IdentityProviderURLObserver> {

    private String identityURL;
    
    public IdentityProviderURLEvent(String identityURL) {
        this.identityURL = identityURL;
    }

    @Override
    public void raise(IdentityProviderURLObserver observer) {
        observer.onUpdateIdentityURL(this.identityURL);
    }
    
}
