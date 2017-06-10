/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package soap_demo_summary;

import static java.lang.System.out;
import javax.xml.namespace.*;
import javax.xml.soap.*;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class StudySubject {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try {
            System.out.println("===========================");
            System.out.println("SOAP Request and SOAP Response");
            SOAPConnectionFactory soapConenctionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConenctionFactory.createConnection();
            
            String wsdlURL = "https://advancedresearch-test.eclinicalhosting.com/OpenClinica-ws/ws/studySubject/v1/studySubjectWsdl.wsdl";

            SOAPMessage soapMessage = soapConnection.call(createSOAPRequest_StudySubject("D3820L00017"), wsdlURL);
            
            System.out.println("===========================");
            System.out.println("Extract element from soap message");
            
//            soapMessage.writeTo(out);
            
            NodeList nList = soapMessage.getSOAPBody().getElementsByTagName("ns2:studySubject");
            if(nList != null){
                for(int i = 0; i < nList.getLength(); i++){
                    System.out.println();
                    Node nNode = (Node) nList.item(i);
                    if(nNode.getNodeType()==Node.ELEMENT_NODE){
                        Element eElement = (Element) nNode;
                        System.out.println("Study Subject: " + (i + 1));
                        System.out.println("label: " + eElement.getElementsByTagName("ns2:label").item(0).getTextContent());
                        System.out.println("enrollmentDate: " + eElement.getElementsByTagName("ns2:enrollmentDate").item(0).getTextContent());
                        
                        NodeList nList1 = eElement.getElementsByTagName("ns2:event");
                        if(nList1 != null){
                            for(int j = 0; j < nList1.getLength(); j++){
                                Node nNode1 = (Node) nList1.item(j);
                                if(nNode1.getNodeType() == Node.ELEMENT_NODE){
                                    Element element1 = (Element) nNode1;
                                    System.out.println("eventDefinitionOID: " + element1.getElementsByTagName("ns2:eventDefinitionOID").item(0).getTextContent());
                                }
                            }
                        }
                        else{
                            System.out.println("No event");
                        }
                    }
                }      
            }            
            else{
                System.out.println("No study subject");
            }
            soapConnection.close();
        } catch (Exception e) {
            System.err.println("Error occurred while sending request to server. Please try again.");
        }
        System.out.println();
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
        
        System.out.println("Request SOAP Message: \n");
        soapMessage.writeTo(System.out);
        System.out.println();
        
        return soapMessage;        
    }
}
