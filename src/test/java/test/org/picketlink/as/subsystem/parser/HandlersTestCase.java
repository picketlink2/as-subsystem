/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package test.org.picketlink.as.subsystem.parser;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.jboss.dmr.ModelNode;
import org.jboss.dmr.Property;
import org.junit.Test;
import org.picketlink.as.subsystem.model.ModelElement;
import org.picketlink.as.subsystem.service.IdentityProviderService;
import org.picketlink.as.subsystem.service.ServiceProviderService;
import org.picketlink.identity.federation.core.config.SPConfiguration;
import org.picketlink.identity.federation.core.handler.config.Handler;
import org.picketlink.identity.federation.core.handler.config.Handlers;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * 
 */
public class HandlersTestCase extends AbstractPicketLinkSubsystemTestCase {

    private static final String FAKE_HANDLER_CLASS_NAME = "org.picketlink.NonExistentHandler";

    /* (non-Javadoc)
     * @see test.org.picketlink.as.subsystem.parser.AbstractPicketLinkSubsystemTestCase#getFederationAliasToTest()
     */
    @Override
    protected String getFederationAliasToTest() {
        return "unit-test-federation-with-handlers";
    }
    
    /**
     * <p>
     * Tests if the handlers are properly configure in the Identity Provider.
     * </p>
     * 
     * @throws Exception
     */
    @Test
    public void testIdentityProviderHandlerConfiguration() throws Exception {
        IdentityProviderService identityProviderService = getIdentityProviderService();
        
        Handlers handlerChain = identityProviderService.getPicketLinkType().getHandlers();
        List<Handler> handlers = handlerChain.getHandler();
        boolean hasFakeHandler = false;
        
        for (Handler handler : handlers) {
            if (handler.getClazz().equals(FAKE_HANDLER_CLASS_NAME)) {
                hasFakeHandler = true;
                break;
            }
        }
        
        assertTrue(hasFakeHandler);
    }
    
    /**
     * <p>
     * Tests if the handlers are properly configure in the Identity Provider.
     * </p>
     * 
     * @throws Exception
     */
    @Test
    public void testHandlerParameterConfiguration() throws Exception {
        IdentityProviderService identityProviderService = getIdentityProviderService();
        
        Handlers handlerChain = identityProviderService.getPicketLinkType().getHandlers();
        List<Handler> handlers = handlerChain.getHandler();
        
        for (Handler handler : handlers) {
            if (handler.getClazz().equals(FAKE_HANDLER_CLASS_NAME)) {
                assertEquals(3, handler.getOption().size());
            }
        }
    }

    /**
     * <p>
     * Tests if the handlers are properly configure in the Identity Provider.
     * </p>
     * 
     * @throws Exception
     */
    @Test
    public void testServiceProviderHandlerConfiguration() throws Exception {
        ServiceProviderService serviceProviderService = getServiceProviderService("unit-test-fake-sp.war");
        
        Handlers handlerChain = serviceProviderService.getPicketLinkType().getHandlers();
        List<Handler> handlers = handlerChain.getHandler();
        boolean hasFakeHandler = false;
        
        for (Handler handler : handlers) {
            if (handler.getClazz().equals(FAKE_HANDLER_CLASS_NAME)) {
                hasFakeHandler = true;
                break;
            }
        }
        
        assertTrue(hasFakeHandler);
    }

}
