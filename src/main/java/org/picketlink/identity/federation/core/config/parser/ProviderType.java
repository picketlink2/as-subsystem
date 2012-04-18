package org.picketlink.identity.federation.core.config.parser;

import org.picketlink.identity.federation.core.config.KeyProviderType;

public interface ProviderType {

    String getSecurityDomain();

    KeyProviderType getKeyProvider();
    
}
