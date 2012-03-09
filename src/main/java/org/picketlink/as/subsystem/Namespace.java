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
package org.picketlink.as.subsystem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.as.controller.persistence.SubsystemMarshallingContext;
import org.jboss.dmr.ModelNode;
import org.jboss.staxmapper.XMLElementReader;
import org.jboss.staxmapper.XMLElementWriter;
import org.picketlink.as.subsystem.parser.PicketLinkSubsystemReader_1_0;
import org.picketlink.as.subsystem.parser.PicketLinkSubsystemWriter_1_0;

/**
 * <p>
 * <code>Enum</code> class with informations about the subsystem's namespace and its versions, providing a register for
 * instances of <code>org.jboss.staxmapper.XMLElementReader</code> and <code>org.jboss.staxmapper.XMLElementWriter</code> for
 * each namespace version.
 * </p>
 * 
 * @author pedroigor
 * @author Paul Ferraro
 * @author Tristan Tarrant
 * 
 * @sice Mar 7, 2012
 */
public enum Namespace {

    PICKETLINK_1_0(1, 0, new PicketLinkSubsystemReader_1_0(), new PicketLinkSubsystemWriter_1_0());

    private static final String BASE_URN = "urn:jboss:picketlink:";

    /**
     * The current namespace version.
     */
    public static final Namespace CURRENT = PICKETLINK_1_0;

    private static final Map<String, Namespace> namespaces;

    static {
        final Map<String, Namespace> map = new HashMap<String, Namespace>();

        for (Namespace namespace : values()) {
            final String name = namespace.getUri();
            if (name != null)
                map.put(name, namespace);
        }

        namespaces = map;
    }

    private final int major;
    private final int minor;

    private final XMLElementReader<List<ModelNode>> reader;
    private final XMLElementWriter<SubsystemMarshallingContext> writer;

    Namespace(int major, int minor, XMLElementReader<List<ModelNode>> reader,
            XMLElementWriter<SubsystemMarshallingContext> writer) {
        this.major = major;
        this.minor = minor;
        this.reader = reader;
        this.writer = writer;
    }

    /**
     * @return the major
     */
    public int getMajor() {
        return this.major;
    }
    
    /**
     * @return the minor
     */
    public int getMinor() {
        return this.minor;
    }
    
    /**
     * Get the URI of this namespace.
     * 
     * @return the URI
     */
    public String getUri() {
        return BASE_URN + this.major + "." + this.minor;
    }

    /**
     * Returns a xml reader for a specific namespace version.
     * 
     * @return
     */
    public XMLElementReader<List<ModelNode>> getXMLReader() {
        return this.reader;
    }

    /**
     * Returns a xml writer for a specific namespace version.
     * 
     * @return
     */
    public XMLElementWriter<SubsystemMarshallingContext> getXMLWriter() {
        return this.writer;
    }

    /**
     * Converts the specified uri to a {@link Namespace}.
     * 
     * @param uri a namespace uri
     * @return the matching namespace enum.
     */
    public static Namespace forUri(String uri) {
        return namespaces.get(uri) == null ? null : namespaces.get(uri);
    }

}