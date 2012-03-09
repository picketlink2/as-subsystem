package org.picketlink.as.subsystem.describer;

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.ADD;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.ALLOWED;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.ATTRIBUTES;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.CHILDREN;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.DEFAULT;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.DESCRIPTION;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.HEAD_COMMENT_ALLOWED;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.MAX_OCCURS;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.MIN_OCCURS;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.MODEL_DESCRIPTION;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.NAMESPACE;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OPERATION_NAME;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.REMOVE;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.REQUIRED;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.TAIL_COMMENT_ALLOWED;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.TYPE;

import java.util.Locale;

import org.jboss.as.controller.descriptions.DescriptionProvider;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;
import org.picketlink.as.subsystem.Namespace;
import org.picketlink.as.subsystem.model.ModelDefinition;

/**
 * Contains the description providers. The description providers are what print out the information when you execute the
 * {@code read-resource-description} operation.
 * 
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 */
public class SubsystemProviders {

    /**
     * Used to create the description of the subsystem
     */
    public static DescriptionProvider SUBSYSTEM = new DescriptionProvider() {
        public ModelNode getModelDescription(Locale locale) {
            // The locale is passed in so you can internationalize the strings used in the descriptions

            final ModelNode subsystem = new ModelNode();
            // Change the description
            subsystem.get(DESCRIPTION).set("Picketlink subsystem");
            subsystem.get(HEAD_COMMENT_ALLOWED).set(true);
            subsystem.get(TAIL_COMMENT_ALLOWED).set(true);
            subsystem.get(NAMESPACE).set(Namespace.CURRENT.getUri());

            // Add information about the 'type' child
            subsystem.get(CHILDREN, ModelDefinition.FEDERATION.getKey(), DESCRIPTION)
                    .set("PicketLink federation configuration");
            subsystem.get(CHILDREN, ModelDefinition.FEDERATION.getKey(), MIN_OCCURS).set(1);
            subsystem.get(CHILDREN, ModelDefinition.FEDERATION.getKey(), MAX_OCCURS).set(1);
            subsystem.get(CHILDREN, ModelDefinition.FEDERATION.getKey(), MODEL_DESCRIPTION);

            return subsystem;
        }
    };

    /**
     * Used to create the description of the subsystem add method
     */
    public static DescriptionProvider SUBSYSTEM_ADD = new DescriptionProvider() {
        public ModelNode getModelDescription(Locale locale) {
            // The locale is passed in so you can internationalize the strings used in the descriptions

            final ModelNode subsystem = new ModelNode();

            subsystem.get(OPERATION_NAME).set(ADD);
            subsystem.get(DESCRIPTION).set("Adds my subsystem");

            return subsystem;
        }
    };

    /**
     * Used to create the description of the subsystem remove method
     */
    public static DescriptionProvider SUBSYSTEM_REMOVE = new DescriptionProvider() {
        public ModelNode getModelDescription(Locale locale) {
            // The locale is passed in so you can internationalize the strings used in the descriptions

            final ModelNode subsystem = new ModelNode();
            subsystem.get(OPERATION_NAME).set(REMOVE);
            subsystem.get(DESCRIPTION).set("Removes my subsystem");

            return subsystem;
        }
    };

    /**
     * Used to create the description of the {@code type} child
     */
    public static DescriptionProvider FEDERATION = new DescriptionProvider() {
        @Override
        public ModelNode getModelDescription(Locale locale) {
            ModelNode node = new ModelNode();

            // Change the description
            node.get(DESCRIPTION).set("Contains information about the federation");

            node.get(ATTRIBUTES, ModelDefinition.FEDERATION_ALIAS.getKey(), DESCRIPTION).set("Federations's alias");
            node.get(ATTRIBUTES, ModelDefinition.FEDERATION_ALIAS.getKey(), TYPE).set(ModelType.STRING);
            node.get(ATTRIBUTES, ModelDefinition.FEDERATION_ALIAS.getKey(), REQUIRED).set(false);
            node.get(ATTRIBUTES, ModelDefinition.FEDERATION_ALIAS.getKey(), DEFAULT).set(
                    ModelDefinition.FEDERATION_ALIAS.getDefinition().getDefaultValue());

            node.get(CHILDREN, ModelDefinition.IDENTITY_PROVIDER.getKey(), DESCRIPTION)
                    .set("Identity Provider's configuration");
            node.get(CHILDREN, ModelDefinition.IDENTITY_PROVIDER.getKey(), MIN_OCCURS).set(0);
            node.get(CHILDREN, ModelDefinition.IDENTITY_PROVIDER.getKey(), MAX_OCCURS).set(Integer.MAX_VALUE);
            node.get(CHILDREN, ModelDefinition.IDENTITY_PROVIDER.getKey(), MODEL_DESCRIPTION);

            return node;
        }
    };

    /**
     * Used to create the description of the {@code type} child
     */
    public static DescriptionProvider IDENTITY_PROVIDER = new DescriptionProvider() {
        @Override
        public ModelNode getModelDescription(Locale locale) {
            ModelNode node = new ModelNode();

            // Change the description
            node.get(DESCRIPTION).set("Contains information about the Identity Provider");

            node.get(ATTRIBUTES, ModelDefinition.IDENTITY_PROVIDER_ALIAS.getKey(), DESCRIPTION)
                    .set("Identity Provider's alias");
            node.get(ATTRIBUTES, ModelDefinition.IDENTITY_PROVIDER_ALIAS.getKey(), TYPE).set(ModelType.STRING);
            node.get(ATTRIBUTES, ModelDefinition.IDENTITY_PROVIDER_ALIAS.getKey(), REQUIRED).set(false);
            node.get(ATTRIBUTES, ModelDefinition.IDENTITY_PROVIDER_ALIAS.getKey(), DEFAULT).set(
                    ModelDefinition.IDENTITY_PROVIDER_ALIAS.getDefinition().getDefaultValue());

            node.get(ATTRIBUTES, ModelDefinition.IDENTITY_PROVIDER_URL.getKey(), DESCRIPTION).set("Identity Provider's url");
            node.get(ATTRIBUTES, ModelDefinition.IDENTITY_PROVIDER_URL.getKey(), TYPE).set(ModelType.STRING);
            node.get(ATTRIBUTES, ModelDefinition.IDENTITY_PROVIDER_URL.getKey(), REQUIRED).set(true);

            node.get(ATTRIBUTES, ModelDefinition.IDENTITY_PROVIDER_SIGN_OUTGOING_MESSAGES.getKey(), DESCRIPTION).set(
                    "Tells the IDP to sign all outgoing messages");
            node.get(ATTRIBUTES, ModelDefinition.IDENTITY_PROVIDER_SIGN_OUTGOING_MESSAGES.getKey(), TYPE).set(ModelType.STRING);
            node.get(ATTRIBUTES, ModelDefinition.IDENTITY_PROVIDER_SIGN_OUTGOING_MESSAGES.getKey(), REQUIRED).set(false);
            node.get(ATTRIBUTES, ModelDefinition.IDENTITY_PROVIDER_SIGN_OUTGOING_MESSAGES.getKey(), DEFAULT).set(
                    ModelDefinition.IDENTITY_PROVIDER_SIGN_OUTGOING_MESSAGES.getDefinition().getDefaultValue());

            node.get(ATTRIBUTES, ModelDefinition.IDENTITY_PROVIDER_IGNORE_INCOMING_SIGNATURES.getKey(), DESCRIPTION).set(
                    "Tells the IDP to ignore incomming signatures from messages");
            node.get(ATTRIBUTES, ModelDefinition.IDENTITY_PROVIDER_IGNORE_INCOMING_SIGNATURES.getKey(), TYPE).set(ModelType.STRING);
            node.get(ATTRIBUTES, ModelDefinition.IDENTITY_PROVIDER_IGNORE_INCOMING_SIGNATURES.getKey(), REQUIRED).set(false);
            node.get(ATTRIBUTES, ModelDefinition.IDENTITY_PROVIDER_IGNORE_INCOMING_SIGNATURES.getKey(), DEFAULT).set(
                    ModelDefinition.IDENTITY_PROVIDER_IGNORE_INCOMING_SIGNATURES.getDefinition().getDefaultValue());

            node.get(CHILDREN, ModelDefinition.TRUST_DOMAIN.getKey(), DESCRIPTION).set(
                    "Identity Provider's Trust domain relationship configuration");
            node.get(CHILDREN, ModelDefinition.TRUST_DOMAIN.getKey(), MIN_OCCURS).set(0);
            node.get(CHILDREN, ModelDefinition.TRUST_DOMAIN.getKey(), MAX_OCCURS).set(Integer.MAX_VALUE);
            node.get(CHILDREN, ModelDefinition.TRUST_DOMAIN.getKey(), MODEL_DESCRIPTION);

            return node;
        }
    };

    /**
     * Used to create the description of the {@code type} child
     */
    public static DescriptionProvider TRUST_DOMAIN = new DescriptionProvider() {
        @Override
        public ModelNode getModelDescription(Locale locale) {
            ModelNode node = new ModelNode();

            node.get(DESCRIPTION).set("Contains information about the trusted domains");

            node.get(ATTRIBUTES, ModelDefinition.TRUST_DOMAIN_NAME.getKey(), DESCRIPTION).set("Domain's url");
            node.get(ATTRIBUTES, ModelDefinition.TRUST_DOMAIN_NAME.getKey(), TYPE).set(ModelType.STRING);
            node.get(ATTRIBUTES, ModelDefinition.TRUST_DOMAIN_NAME.getKey(), REQUIRED).set(true);

            return node;
        }
    };

}
