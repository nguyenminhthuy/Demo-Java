/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soap_demo_summary;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.xml.namespace.*;
import javax.xml.soap.*;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class EventName {

    /**
     * @throws javax.xml.soap.SOAPException
     * @throws java.io.IOException
     */    
    
    public static List<String> arr_oid = new ArrayList<>();    
    
    public static void main(String[] args) throws SOAPException, IOException {
        try {
            System.out.println("===========================");
            SOAPConnectionFactory soapConenctionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConenctionFactory.createConnection();
            
            String wsdlURL = "https://advancedresearch-test.eclinicalhosting.com/OpenClinica-ws/ws/studySubject/v1/studySubjectWsdl.wsdl";

            SOAPMessage soapMessage = soapConnection.call(createSOAPRequest_StudySubject("D3820L00017"), wsdlURL);
            
            
            NodeList nList = soapMessage.getSOAPBody().getElementsByTagName("ns2:studySubject");
            if(nList != null){                
                for(int i = 0; i < nList.getLength(); i++){                    
                    Node nNode = (Node) nList.item(i);                    
                    if(nNode.getNodeType()==Node.ELEMENT_NODE){                        
                        Element eElement = (Element) nNode;                        
                        String nLabel  = eElement.getElementsByTagName("ns2:label").item(0).getTextContent();                        
                        if(nLabel.equals("Quy-Test2")){                            
                            NodeList elementList = eElement.getElementsByTagName("ns2:event");                                                        
                                for(int j = 0; j < elementList.getLength(); j++){                                    
                                    Node nNode1 = (Node) elementList.item(j);
                                    if(nNode1.getNodeType() == Node.ELEMENT_NODE){
                                        Element element1 = (Element) nNode1;
                                        arr_oid.add(element1.getElementsByTagName("ns2:eventDefinitionOID").item(0).getTextContent());
                                    }
                                }
                            break;
                        }
                    }
                }      
            }    
            
            String eventURL = "https://advancedresearch-test.eclinicalhosting.com/OpenClinica-ws/ws/studyEventDefinition/v1/studyEventDefinitionWsdl.wsdl";
            SOAPMessage soapMessage1 = soapConnection.call(createSOAPRequest_EventName("D3820L00017"), eventURL);
            
            NodeList nList1 = soapMessage1.getSOAPBody().getElementsByTagName("studyEventDefinition");
            if(nList1 != null){
                for(int i = 0; i < nList1.getLength(); i++){
                    Node nNode1 = (Node) nList1.item(i);
                    if(nNode1.getNodeType()==Node.ELEMENT_NODE){
                        Element eElement1 = (Element) nNode1;     

                          String oid = eElement1.getElementsByTagName("oid").item(0).getTextContent();
                            
                          for(int k = 0;k < arr_oid.size();k++){
                              if(arr_oid.get(k).equals(oid)){
                                  System.out.println("name: " + eElement1.getElementsByTagName("name").item(0).getTextContent());
                              }
                          }   
                            
                    }
                }
            } 
            
            soapConnection.close();
        } catch (Exception e) {
            System.err.println("Error occurred while sending request to server. Please try again.");
        }
        System.out.println();
    }
    
    private static SOAPMessage createSOAPRequest_EventName(String identify) throws Exception {   
    
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        
        String studyURL = "http://openclinica.org/ws/studyEventDefinition/v1";
        
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("v1", studyURL);
        envelope.addNamespaceDeclaration("bean", "http://openclinica.org/ws/beans");
        
        String prefix = "wsse";
        String prefixURI = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-";
        String security_uri = prefixURI + "wssecurity-secext-1.0.xsd";
        String usernametoken_uri = prefixURI + "wssecurity-utility-1.0.xsd";
        String password_uri = prefixURI + "username-token-profile-1.0#PasswordText";    
        
        //soap header
        SOAPHeader soapheader = envelope.getHeader();
        SOAPElement security = soapheader.addChildElement("Security", prefix, security_uri);
        
        SOAPElement usernameToken = security.addChildElement("UsernameToken",prefix);
        usernameToken.addAttribute(new QName("wsu:Id"), "UsernameToken-27777511");
        usernameToken.addAttribute(new QName("xmlns:wsu"), usernametoken_uri);        
        
        SOAPElement username = usernameToken.addChildElement("Username",prefix);
        username.addTextNode("Vinh_Nguyen");
        
        SOAPElement password = usernameToken.addChildElement("Password",prefix);
        password.addAttribute(new QName("Type"), password_uri);
        password.addTextNode("6f3c57f8e374eb61c73c1232e3b44177da0e4f22");
        
        //soap body
        SOAPBody soapBody = envelope.getBody();
        SOAPElement soapBodyElement = soapBody.addChildElement("listAllRequest","v1");
        SOAPElement soapBodyElement1 = soapBodyElement.addChildElement("studyEventDefinitionListAll","v1");
        SOAPElement soapElement = soapBodyElement1.addChildElement("studyRef","bean");
        SOAPElement soapElement1 = soapElement.addChildElement("identifier","bean");
        soapElement1.addTextNode(identify);
        
        soapMessage.saveChanges();
        
        return soapMessage;        
    }
    
    private static SOAPMessage createSOAPRequest_StudySubject(String identify) throws Exception {   
    
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        
        String studyURL = "http://openclinica.org/ws/studySubject/v1";
        
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("v1", studyURL);
        envelope.addNamespaceDeclaration("bean", "http://openclinica.org/ws/beans");
        
        String prefix = "wsse";
        String prefixURI = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-";
        String security_uri = prefixURI + "wssecurity-secext-1.0.xsd";
        String usernametoken_uri = prefixURI + "wssecurity-utility-1.0.xsd";
        String password_uri = prefixURI + "username-token-profile-1.0#PasswordText";    
        
        //soap header
        SOAPHeader soapheader = envelope.getHeader();
        SOAPElement security = soapheader.addChildElement("Security", prefix, security_uri);
        
        SOAPElement usernameToken = security.addChildElement("UsernameToken",prefix);
        usernameToken.addAttribute(new QName("wsu:Id"), "UsernameToken-27777511");
        usernameToken.addAttribute(new QName("xmlns:wsu"), usernametoken_uri);        
        
        SOAPElement username = usernameToken.addChildElement("Username",prefix);
        username.addTextNode("Vinh_Nguyen");
        
        SOAPElement password = usernameToken.addChildElement("Password",prefix);
        password.addAttribute(new QName("Type"), password_uri);
        password.addTextNode("6f3c57f8e374eb61c73c1232e3b44177da0e4f22");
        
        //soap body
        SOAPBody soapBody = envelope.getBody();
        SOAPElement soapBodyElement = soapBody.addChildElement("listAllByStudyRequest","v1");
        SOAPElement soapElement = soapBodyElement.addChildElement("studyRef","bean");
        SOAPElement soapElement1 = soapElement.addChildElement("identifier","bean");
        soapElement1.addTextNode(identify);
        
        soapMessage.saveChanges();
        
        return soapMessage;        
    }
}
