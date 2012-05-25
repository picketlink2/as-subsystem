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

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.jboss.as.controller.descriptions.ModelDescriptionConstants;
import org.jboss.as.subsystem.test.AbstractSubsystemTest;
import org.jboss.as.subsystem.test.KernelServices;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.Property;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.junit.Before;
import org.picketlink.as.subsystem.PicketLinkExtension;
import org.picketlink.as.subsystem.model.ModelElement;
import org.picketlink.as.subsystem.service.FederationService;
import org.picketlink.as.subsystem.service.IdentityProviderService;

/**
 * @author <a href="mailto:psilva@redhat.com">Pedro Silva</a>
 * 
 */
public class AbstractPicketLinkSubsystemTestCase extends AbstractSubsystemTest {

    protected static final String FAKE_AS7_INSTALLATION_DIR = "target/jboss-as7-fake";
    protected static final String FAKE_AS7_DEPLOYMENTS = FAKE_AS7_INSTALLATION_DIR + "/deployments";

    private ModelNode resultingModelNode;
    private KernelServices kernelServices;

    public AbstractPicketLinkSubsystemTestCase() {
        super(PicketLinkExtension.SUBSYSTEM_NAME, new PicketLinkExtension());
    }

    @Before
    public void onSetup() {
        configureFakeAS7Installation();
        installModelIntoController();
    }

    /**
     * <p>
     * Creates a directory at FAKE_AS7_INSTALLATION_DIR to be used as a fake as7 installation.
     * </p>
     */
    private void configureFakeAS7Installation() {
        File fakeAsInstallation = new File(FAKE_AS7_INSTALLATION_DIR);

        if (fakeAsInstallation.exists()) {
            fakeAsInstallation.delete();
        }

        fakeAsInstallation.mkdir();

        File deploymentsDir = new File(FAKE_AS7_DEPLOYMENTS);

        deploymentsDir.mkdir();

        System.setProperty("jboss.server.base.dir", fakeAsInstallation.getAbsolutePath());
    }

    /**
     * <p>
     * Returns the resulting {@link ModelNode} instance after installing the XML into the controller.
     * </p>
     * 
     * @return
     */
    private ModelNode installModelIntoController() {
        if (this.resultingModelNode == null) {
            try {
                this.kernelServices = super.installInController(getValidSubsystemXML());
                this.resultingModelNode = this.kernelServices.readWholeModel();
            } catch (Exception e) {
                e.printStackTrace();
                Assert.fail("Error while installing the subsystem in the controller.");
            }
        }

        return this.resultingModelNode;
    }

    /**
     * Returns a valid XML for the subsystem.
     * 
     * @return
     */
    protected String getValidSubsystemXML() {
        String content = null;

        try {
            content = FileUtils.readFileToString(new File(Thread.currentThread().getContextClassLoader()
                    .getResource("picketlink-subsystem.xml").getFile()));
        } catch (IOException e) {
            Assert.fail("Error while reading the subsystem configuration file.");
        }

        return content;
    }

    /**
     * <p>
     * Returns a {@link ModelNode} instance for the federation configuration being tested.
     * </p>
     * 
     * @param model
     * @return
     */
    protected Property getFederationModel() {
        ModelNode federationNode = getResultingModelNode().get(ModelDescriptionConstants.SUBSYSTEM, PicketLinkExtension.SUBSYSTEM_NAME,
                ModelElement.FEDERATION.getName());
        
        return federationNode.asPropertyList().get(0);
    }
    
    protected IdentityProviderService getIdentityProviderService() {
        ServiceName serviceName = IdentityProviderService.createServiceName(getIdentityProvider().asProperty().getName());

        return (IdentityProviderService) getInstalledService(serviceName).getValue();
    }

    protected FederationService getFederationService() {
        ServiceName serviceName = FederationService.createServiceName(getFederationModel().getName());

        return (FederationService) getInstalledService(serviceName).getValue();
    }

    /**
     * <p>
     * Returns a {@link ModelNode} instance for the configured Identity Provider.
     * </p>
     * 
     * @return
     */
    protected ModelNode getIdentityProvider() {
        return getFederationModel().getValue().get(ModelElement.IDENTITY_PROVIDER.getName());
    }


    /**
     * <p>
     * Returns a installed service from the container.
     * </p>
     * 
     * @param serviceName
     * @return
     */
    protected ServiceController<?> getInstalledService(ServiceName serviceName) {
        return getKernelServices().getContainer().getService(serviceName);
    }

    protected ModelNode getResultingModelNode() {
        return this.resultingModelNode;
    }

    protected KernelServices getKernelServices() {
        return kernelServices;
    }

}
