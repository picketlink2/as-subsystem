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
 * @author pedroigor
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
        subsystemXml.append("<trust-domain name=\"mycompany.com\" />");
        subsystemXml.append("</trust>");
        subsystemXml.append("</identity-provider>");
        subsystemXml.append("<service-providers>");
        subsystemXml.append("<service-provider alias=\"sales.war\" url=\"http://localhost:8080/sales\"/>");
        subsystemXml.append("<service-provider alias=\"employee.war\" url=\"http://localhost:8080/employee\"/>");
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
        Assert.assertEquals("Unexpected number of operations. Make sure the XML used or the expected value is updated.", 7,
                operations.size());
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

        System.out.println(model);
        System.out.println(services.getPersistedSubsystemXml());
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
