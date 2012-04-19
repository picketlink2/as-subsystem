package org.picketlink.as.subsystem.model.event;

import org.picketlink.identity.federation.core.config.KeyProviderType;

public class KeyProviderEvent implements Event<KeyStoreObserver> {

    private KeyProviderType keyProviderType;
    
    public KeyProviderEvent(KeyProviderType keyProviderType) {
        this.keyProviderType = keyProviderType;
    }
    
    public KeyProviderType getSource() {
        return this.keyProviderType;
    }

    @Override
    public void raise(KeyStoreObserver observer) {
        observer.onUpdateKeyStore(this.keyProviderType);
    }

}
