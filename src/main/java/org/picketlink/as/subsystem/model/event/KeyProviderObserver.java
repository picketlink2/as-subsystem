package org.picketlink.as.subsystem.model.event;

import org.picketlink.identity.federation.core.config.KeyProviderType;

public interface KeyProviderObserver extends Observer {

    void onUpdateKeyProvider(KeyProviderType keyProviderType);
    
}
