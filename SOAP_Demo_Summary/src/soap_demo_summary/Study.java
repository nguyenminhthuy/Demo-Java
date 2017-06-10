
package soap_demo_summary;

import javax.xml.namespace.*;
import javax.xml.soap.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author MinhThuy
 */
public class Study {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            System.out.println("===========================");
            System.out.println("SOAP Request and SOAP Response");
            SOAPConnectionFactory soapConenctionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConenctionFactory.createConnection();
            
            String wsdlURL = "https://advancedresearch-test.eclinicalhosting.com/OpenClinica-ws/ws/study/v1/studyWsdl.wsdl";
            SOAPMessage soapMessage = soapConnection.call(createSOAPRequest(), wsdlURL);
            
//          printSOAPResponse(soapMessage);
            System.out.println("Response SOAP Message");
//            soapMessage.writeTo(System.out);            
            
            System.out.println("===========================");
            System.out.println("Extract element from soap message");
            
            NodeList nList = soapMessage.getSOAPBody().getElementsByTagName("study");
            for(int temp = 0; temp < nList.getLength(); temp ++){
                Node nNode = (Node) nList.item(temp);
                System.out.println("\nCurrent Element " + nNode.getNodeName());
                if(nNode.getNodeType()==Node.ELEMENT_NODE){
                    Element eElement = (Element) nNode;
                    System.out.println("Study: " + (temp + 1));
                    System.out.println("identifier: " + eElement.getElementsByTagName("identifier").item(0).getTextContent());
                    System.out.println("oid: " + eElement.getElementsByTagName("oid").item(0).getTextContent());
                    System.out.println("name: " + eElement.getElementsByTagName("name").item(0).getTextContent());
                }
            }      
            
            System.out.println("===========================");
            System.out.println("SOAP fault");
            
            SOAPBody soapBody = soapMessage.getSOAPBody();
            if(soapBody.hasFault()){
                SOAPFault newFault = soapBody.getFault();
                QName code = newFault.getFaultCodeAsQName();
                String str = newFault.getFaultString();
                String actor = newFault.getFaultActor();
                System.out.println("SOAP fault contains: ");
                System.out.println("Fault code " + code.toString());
                System.out.println("Local name " + code.getLocalPart());
                System.out.println("Namespace prefix " + code.getPrefix() + "bound to " + code.getNamespaceURI());
                System.out.println("Fault string = " + str);
                if(actor != null){
                    System.out.println("Fault actor: " + actor);
                }
            }      
            else{
                System.out.println("No error");
            }           
                        
            soapConnection.close();
            
        } catch (Exception e) {
            System.err.println("Error occurred while sending request to server. Please try again.");
        }
        System.out.println();
    }
    
    private static SOAPMessage createSOAPRequest() throws Exception {   
    
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        
        String studyURL = "http://openclinica.org/ws/study/v1";
        
        SOAPEnvelope envelope = soapPart.getEnvelope();
        envelope.addNamespaceDeclaration("v1", studyURL);
        
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
        
        soapMessage.saveChanges();
        
        System.out.println("Request SOAP Message: \n");
        soapMessage.writeTo(System.out);
        System.out.println();
        
        return soapMessage;        
    }

    /*
     * Print SOAP Response
     */
    private static void printSOAPResponse(SOAPMessage soapResponse) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        Source sourceContent = soapResponse.getSOAPPart().getContent();
        System.out.println("\nResponse SOAP Message = ");
        StreamResult result = new StreamResult(System.out);
        transformer.transform(sourceContent, result);
    }    
}
