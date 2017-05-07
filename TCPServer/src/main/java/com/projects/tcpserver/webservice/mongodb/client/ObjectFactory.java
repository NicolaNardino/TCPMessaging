
package com.projects.tcpserver.webservice.mongodb.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.projects.tcpserver.webservice.mongodb.client package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ChatMessage_QNAME = new QName("http://mongodb.webservice.projects.com/", "ChatMessage");
    private final static QName _GetMessages_QNAME = new QName("http://mongodb.webservice.projects.com/", "getMessages");
    private final static QName _GetMessagesResponse_QNAME = new QName("http://mongodb.webservice.projects.com/", "getMessagesResponse");
    private final static QName _StoreMessages_QNAME = new QName("http://mongodb.webservice.projects.com/", "storeMessages");
    private final static QName _StoreMessagesResponse_QNAME = new QName("http://mongodb.webservice.projects.com/", "storeMessagesResponse");
    private final static QName _CredentialsCheckException_QNAME = new QName("http://mongodb.webservice.projects.com/", "CredentialsCheckException");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.projects.tcpserver.webservice.mongodb.client
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetMessages }
     * 
     */
    public GetMessages createGetMessages() {
        return new GetMessages();
    }

    /**
     * Create an instance of {@link GetMessagesResponse }
     * 
     */
    public GetMessagesResponse createGetMessagesResponse() {
        return new GetMessagesResponse();
    }

    /**
     * Create an instance of {@link StoreMessages }
     * 
     */
    public StoreMessages createStoreMessages() {
        return new StoreMessages();
    }

    /**
     * Create an instance of {@link StoreMessagesResponse }
     * 
     */
    public StoreMessagesResponse createStoreMessagesResponse() {
        return new StoreMessagesResponse();
    }

    /**
     * Create an instance of {@link CredentialsCheckException }
     * 
     */
    public CredentialsCheckException createCredentialsCheckException() {
        return new CredentialsCheckException();
    }

    /**
     * Create an instance of {@link MessageContainer }
     * 
     */
    public MessageContainer createMessageContainer() {
        return new MessageContainer();
    }

    /**
     * Create an instance of {@link ChatMessage }
     * 
     */
    public ChatMessage createChatMessage() {
        return new ChatMessage();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://mongodb.webservice.projects.com/", name = "ChatMessage")
    public JAXBElement<Object> createChatMessage(Object value) {
        return new JAXBElement<Object>(_ChatMessage_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMessages }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://mongodb.webservice.projects.com/", name = "getMessages")
    public JAXBElement<GetMessages> createGetMessages(GetMessages value) {
        return new JAXBElement<GetMessages>(_GetMessages_QNAME, GetMessages.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMessagesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://mongodb.webservice.projects.com/", name = "getMessagesResponse")
    public JAXBElement<GetMessagesResponse> createGetMessagesResponse(GetMessagesResponse value) {
        return new JAXBElement<GetMessagesResponse>(_GetMessagesResponse_QNAME, GetMessagesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StoreMessages }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://mongodb.webservice.projects.com/", name = "storeMessages")
    public JAXBElement<StoreMessages> createStoreMessages(StoreMessages value) {
        return new JAXBElement<StoreMessages>(_StoreMessages_QNAME, StoreMessages.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StoreMessagesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://mongodb.webservice.projects.com/", name = "storeMessagesResponse")
    public JAXBElement<StoreMessagesResponse> createStoreMessagesResponse(StoreMessagesResponse value) {
        return new JAXBElement<StoreMessagesResponse>(_StoreMessagesResponse_QNAME, StoreMessagesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CredentialsCheckException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://mongodb.webservice.projects.com/", name = "CredentialsCheckException")
    public JAXBElement<CredentialsCheckException> createCredentialsCheckException(CredentialsCheckException value) {
        return new JAXBElement<CredentialsCheckException>(_CredentialsCheckException_QNAME, CredentialsCheckException.class, null, value);
    }

}
