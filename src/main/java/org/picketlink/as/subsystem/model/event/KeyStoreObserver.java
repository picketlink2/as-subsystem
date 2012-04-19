package org.picketlink.as.subsystem.model.event;

import org.picketlink.identity.federation.core.config.KeyProviderType;

public interface KeyStoreObserver extends Observer {

    void onUpdateKeyStore(KeyProviderType keyProviderType);
    
}
