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

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.DESCRIBE;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP_ADDR;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.SUBSYSTEM;

import java.util.List;

import junit.framework.Assert;

import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PathElement;
import org.jboss.as.subsystem.test.AbstractSubsystemTest;
import org.jboss.as.subsystem.test.KernelServices;
import org.jboss.dmr.ModelNode;
import org.junit.Test;
import org.picketlink.as.subsystem.Namespace;
import org.picketlink.as.subsystem.PicketLinkExtension;

/**
 * Tests that the PicketLink Subsystem parsing mechanism is functional.
 * 
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * @since Mar 9, 2012
 */
public class PicketLinkSubsystemParsingTestCase extends AbstractSubsystemTest {

    public PicketLinkSubsystemParsingTestCase() {
        super(PicketLinkExtension.SUBSYSTEM_NAME, new PicketLinkExtension());
    }

    /**
     * Returns a valid XML for the subsystem.
     * 
     * @return
     */
    private String getValidSubsystemXML() {
        StringBuffer subsystemXml = new StringBuffer();

        subsystemXml.append("<subsystem xmlns=\"" + Namespace.CURRENT.getUri() + "\">");
        subsystemXml.append("<federation alias=\"my-fed\">");
        subsystemXml
                .append("<identity-provider alias=\"idp.war\" url=\"http://localhost:8080/idp\" signOutgoingMessages=\"false\" ignoreIncomingSignatures=\"true\">");
        subsystemXml.append("<trust>");
        subsystemXml.append("<trust-domain name=\"localhost\" />");
        subsystemXml.append("<trust-domain name=\"mycompany.com2\" />");
        subsystemXml.append("<trust-domain name=\"mycompany.com3\" />");
        subsystemXml.append("<trust-domain name=\"mycompany.com4\" />");
        subsystemXml.append("</trust>");
        subsystemXml.append("</identity-provider>");
        subsystemXml.append("<service-providers>");
        subsystemXml.append("<service-provider alias=\"sales.war\" url=\"http://localhost:8080/sales\" post-binding=\"true\"/>");
        subsystemXml.append("<service-provider alias=\"employee.war\" url=\"http://localhost:8080/employee\" post-binding=\"true\"/>");
        subsystemXml.append("<service-provider alias=\"employee2.war\" url=\"http://localhost:8080/employee2\" post-binding=\"true\"/>");
        subsystemXml.append("</service-providers>");
        subsystemXml.append("</federation>");
        subsystemXml.append("</subsystem>");

        return subsystemXml.toString();
    }

    /**
     * Tests that the xml is parsed into the correct operations.
     */
    @Test
    public void testParseSubsystem() throws Exception {
        List<ModelNode> operations = super.parse(getValidSubsystemXML());

        Assert.assertNotNull("No operations found. Check if the XML used is valid.", operations);
        // Assert.assertEquals("Unexpected number of operations. Make sure the XML used or the expected value is updated.", 7,
        // operations.size());
    }

    /**
     * Tests that the xml installs properly into the controller.
     * 
     * @throws Exception
     */
    @Test
    public void testInstallIntoController() throws Exception {
        KernelServices services = super.installInController(getValidSubsystemXML());

        ModelNode model = services.readWholeModel();
    }

    /**
     * Tests that the SubsystemParser.writeContent() works as expected.
     * 
     * @throws Exception
     */
    @Test
    public void testParseAndMarshalModel() throws Exception {
        String subsystemXml = getValidSubsystemXML();

        KernelServices servicesA = super.installInController(subsystemXml);

        ModelNode modelA = servicesA.readWholeModel();
        String marshalled = servicesA.getPersistedSubsystemXml();

        System.out.println(marshalled);

        KernelServices servicesB = super.installInController(marshalled);

        ModelNode modelB = servicesB.readWholeModel();

        super.compare(modelA, modelB);
    }

    @Test
    public void testDescribeHandler() throws Exception {
        String subsystemXml = getValidSubsystemXML();
        KernelServices servicesA = super.installInController(subsystemXml);

        ModelNode modelA = servicesA.readWholeModel();
        ModelNode describeOp = new ModelNode();
        describeOp.get(OP).set(DESCRIBE);
        describeOp.get(OP_ADDR).set(
                PathAddress.pathAddress(PathElement.pathElement(SUBSYSTEM, PicketLinkExtension.SUBSYSTEM_NAME)).toModelNode());
        List<ModelNode> operations = super.checkResultAndGetContents(servicesA.executeOperation(describeOp)).asList();

        KernelServices servicesB = super.installInController(operations);

        ModelNode modelB = servicesB.readWholeModel();

        super.compare(modelA, modelB);
    }

}
