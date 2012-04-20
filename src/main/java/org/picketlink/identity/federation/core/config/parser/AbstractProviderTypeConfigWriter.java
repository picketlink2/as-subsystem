package org.picketlink.identity.federation.core.config.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.picketlink.identity.federation.core.config.AuthPropertyType;
import org.picketlink.identity.federation.core.exceptions.ProcessingException;
import org.picketlink.identity.federation.core.parsers.config.SAMLConfigParser;
import org.picketlink.identity.federation.core.util.StaxUtil;

public abstract class AbstractProviderTypeConfigWriter<T extends ProviderType> implements ConfigWriter {

    private T configuration;
    
    public AbstractProviderTypeConfigWriter(T configuration) {
        this.configuration = configuration;
    }
    
    /* (non-Javadoc)
     * @see org.picketlink.identity.federation.core.config.parser.ConfigWriter#write(java.io.File)
     */
    @Override
    public void write(File file) {
        XMLStreamWriter writer = null;
        
        try {
            writer = StaxUtil.getXMLStreamWriter(new FileOutputStream(file));
            
            StaxUtil.writeStartElement(writer, "", getProviderElementName(), "urn:picketlink:identity-federation:config:1.0");
            
            writeIdentityURLConfig(writer);

            writeKeyProviderConfig(writer);
            
            doWrite(writer);
            
            StaxUtil.writeEndElement(writer);
        } catch (ProcessingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                } catch (XMLStreamException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected abstract String getProviderElementName();
    
    protected abstract void doWrite(XMLStreamWriter writer) throws ProcessingException;

    /**
     * <p>
     * Writes the <KeyProvider/> element and sub-elements.
     * </p>
     * 
     * @param writer
     * @throws ProcessingException
     */
    protected void writeKeyProviderConfig(XMLStreamWriter writer) throws ProcessingException {
        if (getConfiguration().getKeyProvider() != null) {
            StaxUtil.writeStartElement(writer, "", SAMLConfigParser.KEY_PROVIDER, "");
            StaxUtil.writeAttribute(writer, "ClassName", "org.picketlink.identity.federation.core.impl.KeyStoreKeyManager");
            
            for (AuthPropertyType authProperty : getConfiguration().getKeyProvider().getAuth()) {
                StaxUtil.writeStartElement(writer, "", SAMLConfigParser.AUTH, "");
                StaxUtil.writeAttribute(writer, SAMLConfigParser.KEY, authProperty.getKey());
                StaxUtil.writeAttribute(writer, SAMLConfigParser.VALUE, authProperty.getValue());
                StaxUtil.writeEndElement(writer);
            }
            
            writeValidatingAliasConfig(writer);
            
            StaxUtil.writeEndElement(writer);
        }
    }

    protected void writeValidatingAliasConfig(XMLStreamWriter writer) throws ProcessingException {
    }
    
    /**
     * <p>
     * Writes the <IdentityURL/> element.
     * </p>
     * 
     * @param writer
     * @throws ProcessingException
     */
    protected void writeIdentityURLConfig(XMLStreamWriter writer) throws ProcessingException {
        StaxUtil.writeStartElement(writer, "", SAMLConfigParser.IDENTITY_URL, "");
        StaxUtil.writeCharacters(writer, this.configuration.getIdentityURL() + "/");
        StaxUtil.writeEndElement(writer);
    }

    protected T getConfiguration() {
        return this.configuration;
    }
}
