package org.picketlink.as.subsystem.model.event;

import org.picketlink.as.subsystem.service.Observer;
import org.picketlink.identity.federation.core.config.KeyProviderType;

public class KeyProviderEvent implements Event<KeyProviderType> {

    private KeyProviderType keyProviderType;
    
    public KeyProviderEvent(KeyProviderType keyProviderType) {
        this.keyProviderType = keyProviderType;
    }
    
    public KeyProviderType getSource() {
        return this.keyProviderType;
    }

    @Override
    public void raise(EventManager manager) {
        if (manager.getObserver().get(this.getClass().getName()) != null) {
            for (Observer observer : manager.getObserver().get(this.getClass().getName())) {
                KeyStoreObserver keyStoreObserver = (KeyStoreObserver) observer;
                
                keyStoreObserver.onUpdateKeyStore(getSource());
            }
        }
    }
    
    public interface KeyStoreObserver extends Observer {

        void onUpdateKeyStore(KeyProviderType keyProviderType);
        
    }
    
}
