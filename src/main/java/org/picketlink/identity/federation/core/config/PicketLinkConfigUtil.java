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

package org.picketlink.identity.federation.core.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import org.picketlink.identity.federation.core.handler.config.Handler;
import org.picketlink.identity.federation.core.parsers.sts.STSConfigParser;
import org.picketlink.identity.federation.core.saml.v2.interfaces.SAML2Handler;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 *
 */
public final class PicketLinkConfigUtil {
    
    public static STSType createSTSType() {
        STSType stsType = null;
        
        InputStream stream = null;
        
        try {
            ClassLoader clazzLoader = PicketLinkConfigUtil.class.getClassLoader();
            
            URL url = clazzLoader.getResource("core-sts.xml");

            if (url == null) {
                clazzLoader = Thread.currentThread().getContextClassLoader();
                url = clazzLoader.getResource("core-sts");
            }

            stream = url.openStream();
            stsType = (STSType) new STSConfigParser().parse(stream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return stsType;
    }
    
    public static void addHandler(Class<? extends SAML2Handler> handlerClassName, PicketLinkType picketLinkType) {
        Handler handler = new Handler();
        
        handler.setClazz(handlerClassName.getName());
        
        picketLinkType.getHandlers().add(handler);
    }
    
    public static void addHandler(Class<? extends SAML2Handler> handlerClassName, Map<String,String> options, PicketLinkType picketLinkType) {
        Handler handler = new Handler();
        
        handler.setClazz(handlerClassName.getName());

        for (Map.Entry<String, String> option: options.entrySet()) {
            KeyValueType kv = new KeyValueType();
            
            kv.setKey(option.getKey());
            kv.setValue(option.getValue());
            
            handler.add(kv);
        }
        
        picketLinkType.getHandlers().add(handler);
    }
    
}
