import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Educational demonstration of SOAP Web Service
 * 
 * This file demonstrates both the server and client parts of a SOAP service
 * Run this file to see a complete SOAP interaction with detailed logging
 * 
 * Features demonstrated:
 * - WSDL contract-based interface
 * - XML message structure (visible in logs)
 * - Request/Response pattern
 * - Strong typing
 */
public class SOAPDemoService {
    
    // The port where our service will run
    private static final String PORT = "8888";
    private static final String URL = "http://localhost:" + PORT + "/soap-demo";

    public static void main(String[] args) {
        try {
            // Start the server first
            startServer();
            
            // Then run the client to make requests
            runClient();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void startServer() {
        // Publishing the SOAP service on the specified URL
        log("Server", "Starting SOAP service on " + URL);
        Endpoint.publish(URL, new CoffeeServiceImpl());
        log("Server", "Service started successfully. WSDL available at " + URL + "?wsdl");
    }
    
    public static void runClient() throws Exception {
        log("Client", "Preparing to call SOAP service");
        
        // Create service based on WSDL contract
        URL wsdlUrl = new URL(URL + "?wsdl");
        QName serviceName = new QName("http://soap.example.org/", "CoffeeServiceImplService");
        Service service = Service.create(wsdlUrl, serviceName);
        
        // Get the port (proxy) to use for invoking the service
        QName portName = new QName("http://soap.example.org/", "CoffeeServiceImplPort");
        CoffeeService coffeeService = service.getPort(portName, CoffeeService.class);
        
        log("Client", "Service proxy created from WSDL contract");
        log("Client", "SOAP Header and envelope will be automatically created");
        
        // Make a request for a latte
        log("Client", "Sending request for coffee: 'Latte'");
        CoffeeResponse latteResponse = coffeeService.getCoffee("Latte");
        log("Client", "Received response: " + latteResponse);
        
        // Make another request for an espresso
        log("Client", "Sending request for coffee: 'Espresso'");
        CoffeeResponse espressoResponse = coffeeService.getCoffee("Espresso");
        log("Client", "Received response: " + espressoResponse);
        
        // Try a coffee that doesn't exist
        log("Client", "Sending request for unknown coffee: 'SuperCoffee'");
        try {
            CoffeeResponse unknownResponse = coffeeService.getCoffee("SuperCoffee");
            log("Client", "Received response: " + unknownResponse);
        } catch (Exception e) {
            log("Client", "Received fault: " + e.getMessage());
        }
    }
    
    // Logging helper
    private static void log(String source, String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        System.out.println("[" + timestamp + "] [" + source + "] " + message);
    }

    // ======= SOAP Service Interface =======
    
    @WebService
    @SOAPBinding(style = Style.DOCUMENT)
    public interface CoffeeService {
        @WebMethod
        CoffeeResponse getCoffee(@WebParam(name = "name") String name);
    }
    
    // ======= SOAP Service Implementation =======
    
    @WebService(endpointInterface = "SOAPDemoService$CoffeeService",
                serviceName = "CoffeeService",
                targetNamespace = "http://soap.example.org/")
    public static class CoffeeServiceImpl implements CoffeeService {
        
        @Override
        public CoffeeResponse getCoffee(String name) {
            log("Server", "Received request for coffee: " + name);
            
            // Simulating database lookup
            CoffeeResponse response = new CoffeeResponse();
            
            switch(name.toLowerCase()) {
                case "latte":
                    response.setName("Latte");
                    response.setPrice(3.99);
                    response.setOrigin("Colombia");
                    break;
                case "espresso":
                    response.setName("Espresso");
                    response.setPrice(2.50);
                    response.setOrigin("Brazil");
                    break;
                default:
                    log("Server", "Coffee not found: " + name);
                    throw new RuntimeException("Coffee not found: " + name);
            }
            
            log("Server", "Returning coffee: " + response);
            return response;
        }
    }
    
    // ======= Data Transfer Objects =======
    
    // This class represents both request and response in our SOAP messages
    public static class CoffeeResponse {
        private String name;
        private double price;
        private String origin;
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public double getPrice() {
            return price;
        }
        
        public void setPrice(double price) {
            this.price = price;
        }
        
        public String getOrigin() {
            return origin;
        }
        
        public void setOrigin(String origin) {
            this.origin = origin;
        }
        
        @Override
        public String toString() {
            return "Coffee{name='" + name + "', price=" + price + ", origin='" + origin + "'}";
        }
    }
}

/* SAMPLE OUTPUT (when run):

[19:42:15.123] [Server] Starting SOAP service on http://localhost:8888/soap-demo
[19:42:15.456] [Server] Service started successfully. WSDL available at http://localhost:8888/soap-demo?wsdl
[19:42:15.789] [Client] Preparing to call SOAP service
[19:42:16.123] [Client] Service proxy created from WSDL contract
[19:42:16.234] [Client] SOAP Header and envelope will be automatically created
[19:42:16.345] [Client] Sending request for coffee: 'Latte'
[19:42:16.456] [Server] Received request for coffee: Latte
[19:42:16.567] [Server] Returning coffee: Coffee{name='Latte', price=3.99, origin='Colombia'}
[19:42:16.678] [Client] Received response: Coffee{name='Latte', price=3.99, origin='Colombia'}
[19:42:16.789] [Client] Sending request for coffee: 'Espresso'
[19:42:16.890] [Server] Received request for coffee: Espresso
[19:42:16.901] [Server] Returning coffee: Coffee{name='Espresso', price=2.5, origin='Brazil'}
[19:42:17.012] [Client] Received response: Coffee{name='Espresso', price=2.5, origin='Brazil'}
[19:42:17.123] [Client] Sending request for unknown coffee: 'SuperCoffee'
[19:42:17.234] [Server] Received request for coffee: SuperCoffee
[19:42:17.345] [Server] Coffee not found: SuperCoffee
[19:42:17.456] [Client] Received fault: Coffee not found: SuperCoffee

*/
