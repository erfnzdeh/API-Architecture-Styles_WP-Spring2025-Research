import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * Main demonstration class that compares all API architecture styles
 * 
 * This class allows you to run examples of each API style and learn about 
 * their differences and use cases.
 */
public class APIComparisonDemo {
    
    public static void main(String[] args) {
        printWelcomeMessage();
        
        try (Scanner scanner = new Scanner(System.in)) {
            boolean exit = false;
            
            while (!exit) {
                printMenu();
                System.out.print("Enter your choice (1-7): ");
                
                if (scanner.hasNextInt()) {
                    int choice = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline
                    
                    switch (choice) {
                        case 1:
                            runSOAPDemo();
                            break;
                        case 2:
                            runRESTDemo();
                            break;
                        case 3:
                            runGraphQLDemo();
                            break;
                        case 4:
                            runGRPCDemo();
                            break;
                        case 5:
                            runWebSocketDemo();
                            break;
                        case 6:
                            runWebhookDemo();
                            break;
                        case 7:
                            exit = true;
                            break;
                        default:
                            log("Error", "Invalid choice. Please enter a number between 1 and 7.");
                    }
                } else {
                    String input = scanner.nextLine();
                    if (input.equalsIgnoreCase("q") || input.equalsIgnoreCase("exit")) {
                        exit = true;
                    } else {
                        log("Error", "Invalid input. Please enter a number between 1 and 7.");
                    }
                }
                
                // If not exiting, wait for user to press enter to continue
                if (!exit) {
                    System.out.println("\nPress Enter to return to the main menu...");
                    scanner.nextLine();
                }
            }
        }
        
        System.out.println("\nThank you for exploring API architecture styles!");
    }
    
    private static void printWelcomeMessage() {
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║                                                    ║");
        System.out.println("║    API ARCHITECTURE STYLES INTERACTIVE DEMO        ║");
        System.out.println("║                                                    ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
        System.out.println("\nThis program demonstrates different API architecture styles");
        System.out.println("with examples of how each one works and their unique features.");
    }
    
    private static void printMenu() {
        System.out.println("\n═════════════ API STYLES MENU ═════════════");
        System.out.println("1. SOAP (Simple Object Access Protocol)");
        System.out.println("2. REST (Representational State Transfer)");
        System.out.println("3. GraphQL");
        System.out.println("4. gRPC (Google Remote Procedure Call)");
        System.out.println("5. WebSocket");
        System.out.println("6. Webhook");
        System.out.println("7. Exit");
        System.out.println("═════════════════════════════════════════════");
    }
    
    private static void runSOAPDemo() {
        printAPITitle("SOAP");
        printAPIDescription("SOAP", 
            "A protocol that uses XML messages for exchanging structured information\n" +
            "in a decentralized, distributed environment. It relies on contract-based\n" +
            "WSDL files and supports advanced features like transactions and security.");
        
        System.out.println("\nKey Features of SOAP:");
        System.out.println("• XML-based messaging format with strict contracts");
        System.out.println("• Protocol-agnostic (works over HTTP, SMTP, etc.)");
        System.out.println("• Built-in error handling and security (WS-Security)");
        System.out.println("• ACID transaction support");
        System.out.println("• Platform and language independence");
        
        System.out.println("\nBest Used For:");
        System.out.println("• Enterprise applications with strict requirements");
        System.out.println("• Financial services and banking systems");
        System.out.println("• Legacy system integration");
        System.out.println("• Applications requiring high security and reliability");
        
        System.out.println("\nRunning SOAP Demo...\n");
        try {
            SOAPDemoService.main(new String[]{});
        } catch (Exception e) {
            log("Error", "Error running SOAP demo: " + e.getMessage());
        }
    }
    
    private static void runRESTDemo() {
        printAPITitle("REST");
        printAPIDescription("REST", 
            "An architectural style that uses HTTP methods to operate on resources,\n" +
            "typically represented as JSON. REST is stateless, cacheable, and follows\n" +
            "a uniform interface design with a client-server architecture.");
        
        System.out.println("\nKey Features of REST:");
        System.out.println("• Resource-oriented design with URLs");
        System.out.println("• Uses standard HTTP methods (GET, POST, PUT, DELETE)");
        System.out.println("• Stateless communication");
        System.out.println("• JSON or XML data format (usually JSON)");
        System.out.println("• Cacheability of responses");
        
        System.out.println("\nBest Used For:");
        System.out.println("• Public APIs");
        System.out.println("• Mobile application backends");
        System.out.println("• Web applications");
        System.out.println("• Microservices architecture");
        
        System.out.println("\nRunning REST Demo...\n");
        try {
            RESTDemoService.main(new String[]{});
        } catch (Exception e) {
            log("Error", "Error running REST demo: " + e.getMessage());
        }
    }
    
    private static void runGraphQLDemo() {
        printAPITitle("GraphQL");
        printAPIDescription("GraphQL", 
            "A query language for APIs that enables clients to request exactly the data\n" +
            "they need. GraphQL provides a complete description of the data, giving clients\n" +
            "the power to ask for precisely what they need and nothing more.");
        
        System.out.println("\nKey Features of GraphQL:");
        System.out.println("• Single endpoint for all operations");
        System.out.println("• Client specifies exactly what data to fetch");
        System.out.println("• Strongly typed schema");
        System.out.println("• Hierarchical queries matching the structure of the response");
        System.out.println("• Introspection for self-documentation");
        
        System.out.println("\nBest Used For:");
        System.out.println("• Mobile apps with limited bandwidth");
        System.out.println("• Dashboards aggregating data from multiple sources");
        System.out.println("• Complex UIs with nested/related data");
        System.out.println("• APIs serving multiple client types with different data needs");
        
        System.out.println("\nRunning GraphQL Demo...\n");
        try {
            GraphQLDemoService.main(new String[]{});
        } catch (Exception e) {
            log("Error", "Error running GraphQL demo: " + e.getMessage());
        }
    }
    
    private static void runGRPCDemo() {
        printAPITitle("gRPC");
        printAPIDescription("gRPC", 
            "A high-performance RPC framework that uses Protocol Buffers for serialization\n" +
            "and runs on HTTP/2. It enables efficient communication between services with\n" +
            "support for streaming, authentication, and load balancing.");
        
        System.out.println("\nKey Features of gRPC:");
        System.out.println("• Protocol Buffers binary serialization");
        System.out.println("• HTTP/2 transport with multiplexing");
        System.out.println("• Bidirectional streaming");
        System.out.println("• Contract-first approach with .proto files");
        System.out.println("• Code generation for multiple languages");
        
        System.out.println("\nBest Used For:");
        System.out.println("• Microservice-to-microservice communication");
        System.out.println("• Low latency, high throughput systems");
        System.out.println("• Polyglot environments with multiple languages");
        System.out.println("• Real-time streaming services");
        
        System.out.println("\nRunning gRPC Demo...\n");
        try {
            GRPCDemoService.main(new String[]{});
        } catch (Exception e) {
            log("Error", "Error running gRPC demo: " + e.getMessage());
        }
    }
    
    private static void runWebSocketDemo() {
        printAPITitle("WebSocket");
        printAPIDescription("WebSocket", 
            "A communication protocol that provides full-duplex communication over a single,\n" +
            "long-lived connection. It enables real-time data transfer between clients and\n" +
            "servers with reduced latency compared to HTTP polling.");
        
        System.out.println("\nKey Features of WebSocket:");
        System.out.println("• Persistent, bidirectional connection");
        System.out.println("• Real-time data streaming");
        System.out.println("• Lower overhead than HTTP polling");
        System.out.println("• Text and binary message formats");
        System.out.println("• Built-in connection management");
        
        System.out.println("\nBest Used For:");
        System.out.println("• Chat applications");
        System.out.println("• Live dashboards and monitoring");
        System.out.println("• Multiplayer games");
        System.out.println("• Collaborative editing tools");
        System.out.println("• Financial trading platforms");
        
        System.out.println("\nRunning WebSocket Demo...\n");
        try {
            WebSocketDemoService.main(new String[]{});
        } catch (Exception e) {
            log("Error", "Error running WebSocket demo: " + e.getMessage());
        }
    }
    
    private static void runWebhookDemo() {
        printAPITitle("Webhook");
        printAPIDescription("Webhook", 
            "A method of augmenting or altering an application's behavior with callbacks.\n" +
            "When events occur, source applications make HTTP requests to URLs configured\n" +
            "in advance (webhooks) to notify other applications.");
        
        System.out.println("\nKey Features of Webhook:");
        System.out.println("• Event-driven architecture");
        System.out.println("• Server-to-server communication");
        System.out.println("• Push-based notifications");
        System.out.println("• Asynchronous processing");
        System.out.println("• Minimal polling required");
        
        System.out.println("\nBest Used For:");
        System.out.println("• Payment processing notifications");
        System.out.println("• CI/CD pipeline triggers");
        System.out.println("• SaaS integration (GitHub, Stripe, etc.)");
        System.out.println("• IoT event notifications");
        System.out.println("• Workflow automation");
        
        System.out.println("\nRunning Webhook Demo...\n");
        try {
            WebhookDemoService.main(new String[]{});
        } catch (Exception e) {
            log("Error", "Error running Webhook demo: " + e.getMessage());
        }
    }
    
    private static void printAPITitle(String apiName) {
        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║                                                    ║");
        String title = "    " + apiName + " API DEMONSTRATION    ";
        int padding = (52 - title.length()) / 2;
        System.out.print("║");
        for (int i = 0; i < padding; i++) System.out.print(" ");
        System.out.print(title);
        for (int i = 0; i < padding; i++) System.out.print(" ");
        if ((52 - title.length()) % 2 != 0) System.out.print(" ");
        System.out.println("║");
        System.out.println("║                                                    ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
    }
    
    private static void printAPIDescription(String apiName, String description) {
        System.out.println("\n" + apiName + " is: " + description);
    }
    
    // Logging helper
    private static void log(String source, String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        System.out.println("[" + timestamp + "] [" + source + "] " + message);
    }
}
