package org.picketlink.as.subsystem.model.event;


public class IdentityProviderURLEvent implements Event<String> {

    private String identityURL;
    
    public IdentityProviderURLEvent(String identityURL) {
        this.identityURL = identityURL;
    }

    @Override
    public String getSource() {
        return this.identityURL;
    }
    
    @Override
    public void raise(EventManager manager) {
        if (manager.getObserver().get(this.getClass().getName()) != null) {
            for (Observer observer : manager.getObserver().get(this.getClass().getName())) {
                IdentityProviderURLObserver keyStoreObserver = (IdentityProviderURLObserver) observer;
                
                keyStoreObserver.onUpdateIdentityURL(getSource());
            }
        }
    }
    
    public interface IdentityProviderURLObserver extends Observer {

        void onUpdateIdentityURL(String identityURL);
        
    }
    
}
