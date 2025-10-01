import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Educational demonstration of gRPC concepts
 * 
 * This file simulates gRPC service with manual implementation to show
 * the core concepts. In a real application, you would use the gRPC library
 * and Protocol Buffers to handle the implementation.
 * 
 * Features demonstrated:
 * - Protocol Buffers message format (simulated)
 * - Service definition with strongly-typed contracts
 * - Unary, Server Streaming, and Bidirectional Streaming RPC
 * - Binary serialization benefits (described in logs)
 */
public class GRPCDemoService {
    
    // Our "database" of coffees
    private static Map<String, Coffee> coffeeDatabase = new HashMap<>();
    
    public static void main(String[] args) throws InterruptedException, IOException {
        // Start the gRPC server
        startGRPCServer();
        
        // Run the gRPC client
        runGRPCClient();
    }
    
    private static void startGRPCServer() {
        log("Server", "Starting gRPC server on port 50051");
        
        // Initialize our coffee database
        Coffee latte = new Coffee("latte", "Latte", 3.99, "Colombia");
        Coffee espresso = new Coffee("espresso", "Espresso", 2.50, "Brazil");
        Coffee cappuccino = new Coffee("cappuccino", "Cappuccino", 4.20, "Ethiopia");
        
        coffeeDatabase.put(latte.getId(), latte);
        coffeeDatabase.put(espresso.getId(), espresso);
        coffeeDatabase.put(cappuccino.getId(), cappuccino);
        
        log("Server", "Server started and ready to accept requests");
    }
    
    private static void runGRPCClient() throws InterruptedException, IOException {
        log("Client", "Creating gRPC channel to localhost:50051");
        log("Client", "In a real gRPC implementation, we'd be using generated stubs from .proto files");
        
        // Simulate unary RPC (request-response)
        simulateUnaryCoffeeRequest();
        
        // Simulate server streaming RPC
        simulateServerStreamingRequest();
        
        // Simulate bidirectional streaming RPC
        simulateBidirectionalStreamingRequest();
        
        log("Client", "All gRPC examples completed");
    }
    
    private static void simulateUnaryCoffeeRequest() {
        log("Client", "=== Demonstrating Unary RPC ===");
        log("Client", "This is the simplest RPC type: send one request, get one response");
        
        // This would be generated from a .proto file like:
        /*
        message GetCoffeeRequest {
            string coffee_id = 1;
        }
        
        message Coffee {
            string id = 1;
            string name = 2;
            double price = 3;
            string origin = 4;
        }
        
        service CoffeeService {
            rpc GetCoffee(GetCoffeeRequest) returns (Coffee) {}
        }
        */
        
        // Create a request message (in Protocol Buffers this would be binary serialized)
        log("Client", "Creating GetCoffeeRequest with ID 'latte'");
        log("Client", "In real gRPC, this would be serialized to a compact binary format");
        
        // Simulate the RPC call
        log("Client", "Making gRPC call: CoffeeService.GetCoffee()");
        
        // Server processes the request
        log("Server", "Received gRPC request for GetCoffee with ID 'latte'");
        
        // Simulate retrieving coffee from database
        Coffee coffee = coffeeDatabase.get("latte");
        
        // Simulate response serialization
        log("Server", "Found coffee: " + coffee);
        log("Server", "Serializing response to binary Protocol Buffer format");
        
        // Simulate client receiving and deserializing the response
        log("Client", "Received binary response");
        log("Client", "Deserializing response from binary Protocol Buffer format");
        log("Client", "Received Coffee: " + coffee);
    }
    
    private static void simulateServerStreamingRequest() throws InterruptedException {
        log("Client", "=== Demonstrating Server Streaming RPC ===");
        log("Client", "Client sends one request, server sends back a stream of responses");
        
        // This would be generated from a .proto file like:
        /*
        message ListCoffeesRequest {
            double min_price = 1;
            double max_price = 2;
        }
        
        service CoffeeService {
            rpc ListCoffees(ListCoffeesRequest) returns (stream Coffee) {}
        }
        */
        
        // Create a request message
        log("Client", "Creating ListCoffeesRequest with price range $2.00-$4.00");
        
        // Simulate the RPC call
        log("Client", "Making gRPC call: CoffeeService.ListCoffees()");
        
        // Server processes the request
        log("Server", "Received gRPC request for ListCoffees in price range $2.00-$4.00");
        log("Server", "Will stream back 3 coffees that match criteria");
        
        // Stream back responses one at a time
        final CountDownLatch latch = new CountDownLatch(coffeeDatabase.size());
        
        for (Coffee coffee : coffeeDatabase.values()) {
            if (coffee.getPrice() >= 2.0 && coffee.getPrice() <= 4.0) {
                // In real gRPC, each item would be sent as it's processed
                log("Server", "Streaming coffee: " + coffee);
                
                // Simulate client processing each streaming response
                log("Client", "Received streaming coffee: " + coffee);
            }
            
            latch.countDown();
            Thread.sleep(500); // Simulate delay between stream items
        }
        
        latch.await(2, TimeUnit.SECONDS);
        log("Client", "Stream completed");
    }
    
    private static void simulateBidirectionalStreamingRequest() throws InterruptedException {
        log("Client", "=== Demonstrating Bidirectional Streaming RPC ===");
        log("Client", "Both client and server can send messages at any time");
        
        // This would be generated from a .proto file like:
        /*
        message OrderCoffeeRequest {
            string coffee_id = 1;
            int32 quantity = 2;
        }
        
        message OrderStatus {
            string coffee_id = 1;
            string status = 2; // "preparing", "ready", etc.
        }
        
        service CoffeeService {
            rpc OrderCoffees(stream OrderCoffeeRequest) returns (stream OrderStatus) {}
        }
        */
        
        log("Client", "Opening bidirectional stream for OrderCoffees");
        log("Server", "Bidirectional stream established for OrderCoffees");
        
        // Client can send multiple messages
        log("Client", "Sending order for 2 lattes");
        log("Server", "Received order for 2 lattes");
        log("Server", "Sending status update: latte - preparing");
        log("Client", "Received status update: latte - preparing");
        
        Thread.sleep(500);
        
        // Client can continue sending messages
        log("Client", "Sending order for 1 espresso");
        log("Server", "Received order for 1 espresso");
        log("Server", "Sending status update: espresso - preparing");
        log("Client", "Received status update: espresso - preparing");
        
        Thread.sleep(700);
        
        // Server can send messages at any time
        log("Server", "Sending status update: latte - ready");
        log("Client", "Received status update: latte - ready");
        
        Thread.sleep(300);
        
        log("Server", "Sending status update: espresso - ready");
        log("Client", "Received status update: espresso - ready");
        
        // Client signals it's done sending
        log("Client", "Closing sending side of stream (no more orders)");
        
        // Server can still send messages
        log("Server", "Sending final status: all orders complete");
        log("Client", "Received final status: all orders complete");
        
        log("Server", "Closing server side of the stream");
        log("Client", "Bidirectional stream closed");
        
        log("Info", "Note: gRPC with HTTP/2 allows multiplexing multiple streams over a single connection");
        log("Info", "This is much more efficient than creating new connections for each request");
    }
    
    // Logging helper
    private static void log(String source, String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        System.out.println("[" + timestamp + "] [" + source + "] " + message);
    }
    
    /**
     * Coffee model (simulating a Protocol Buffer generated class)
     */
    public static class Coffee {
        private String id;
        private String name;
        private double price;
        private String origin;
        
        public Coffee(String id, String name, double price, String origin) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.origin = origin;
        }
        
        public String getId() {
            return id;
        }
        
        public String getName() {
            return name;
        }
        
        public double getPrice() {
            return price;
        }
        
        public String getOrigin() {
            return origin;
        }
        
        @Override
        public String toString() {
            return "{id: \"" + id + "\", name: \"" + name + "\", price: " + price + ", origin: \"" + origin + "\"}";
        }
    }
}
