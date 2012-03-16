package test.org.picketlink.as.subsystem.parser;

import java.util.List;

import junit.framework.Assert;

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
     * Tests that the xml is parsed into the correct operations
     */
    @Test
    public void testParseSubsystem() throws Exception {
        // Parse the subsystem xml into operations
        StringBuffer subsystemXml = new StringBuffer();

        subsystemXml.append("<subsystem xmlns=\"" + Namespace.CURRENT.getUri() + "\">");
        subsystemXml.append("<federation alias=\"my-fed\">");
        subsystemXml.append("<identity-provider alias=\"idp.war\" url=\"http://localhost:8080/idp\" signOutgoingMessages=\"false\" ignoreIncomingSignatures=\"true\">");
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

        List<ModelNode> operations = super.parse(subsystemXml.toString());
        
        Assert.assertNotNull(operations);
        Assert.assertFalse(operations.isEmpty());
    }

    @Test
    public void testInstallIntoController() throws Exception {
        StringBuffer subsystemXml = new StringBuffer();

        subsystemXml.append("<subsystem xmlns=\"" + Namespace.CURRENT.getUri() + "\">");
        subsystemXml.append("<federation alias=\"my-fed\">");
        subsystemXml.append("<identity-provider alias=\"idp.war\" url=\"http://localhost:8080/idp\" signOutgoingMessages=\"false\" ignoreIncomingSignatures=\"true\">");
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

        KernelServices services = super.installInController(subsystemXml.toString());

        ModelNode model = services.readWholeModel();
        
        System.out.println(model);
        System.out.println(services.getPersistedSubsystemXml());
    }

}
