import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Educational demonstration of REST API concepts
 * 
 * This file simulates a Spring Boot REST API with manual HTTP handling to show
 * how REST principles work. In a real application, you would use a framework like
 * Spring Boot to handle the HTTP mechanics.
 * 
 * Features demonstrated:
 * - Resource-based design
 * - HTTP methods (GET, POST, PUT, DELETE)
 * - JSON data exchange
 * - Stateless communication
 * - Status codes
 */
public class RESTDemoService {
    
    // In-memory database of coffees
    private static final Map<String, Coffee> coffeeDatabase = new HashMap<>();
    
    // Simulated HTTP request counter for demonstration
    private static int requestCount = 0;
    
    public static void main(String[] args) {
        // Initialize our coffee database
        initializeDatabase();
        
        // Simulate HTTP requests to our REST API
        simulateRequests();
    }
    
    private static void initializeDatabase() {
        log("Server", "Initializing coffee database");
        
        Coffee latte = new Coffee("latte", "Latte", 3.99, "Colombia");
        Coffee espresso = new Coffee("espresso", "Espresso", 2.50, "Brazil");
        Coffee cappuccino = new Coffee("cappuccino", "Cappuccino", 4.20, "Ethiopia");
        
        coffeeDatabase.put(latte.getId(), latte);
        coffeeDatabase.put(espresso.getId(), espresso);
        coffeeDatabase.put(cappuccino.getId(), cappuccino);
        
        log("Server", "Database initialized with " + coffeeDatabase.size() + " coffees");
    }
    
    private static void simulateRequests() {
        log("Client", "=== Demonstrating GET - Retrieving Resources ===");
        
        // GET all coffees
        simulateHttpRequest("GET", "/coffees", null);
        
        // GET a specific coffee
        simulateHttpRequest("GET", "/coffees/latte", null);
        
        // GET a non-existent coffee
        simulateHttpRequest("GET", "/coffees/unknown", null);
        
        log("Client", "=== Demonstrating POST - Creating Resources ===");
        
        // POST a new coffee
        Coffee mocha = new Coffee("mocha", "Mocha", 4.50, "Yemen");
        simulateHttpRequest("POST", "/coffees", mocha);
        
        // Verify the new coffee was created
        simulateHttpRequest("GET", "/coffees/mocha", null);
        
        log("Client", "=== Demonstrating PUT - Updating Resources ===");
        
        // PUT (update) an existing coffee
        Coffee updatedMocha = new Coffee("mocha", "Mocha Deluxe", 5.50, "Yemen Premium");
        simulateHttpRequest("PUT", "/coffees/mocha", updatedMocha);
        
        // Verify the coffee was updated
        simulateHttpRequest("GET", "/coffees/mocha", null);
        
        log("Client", "=== Demonstrating DELETE - Removing Resources ===");
        
        // DELETE a coffee
        simulateHttpRequest("DELETE", "/coffees/cappuccino", null);
        
        // Verify the coffee was deleted
        simulateHttpRequest("GET", "/coffees/cappuccino", null);
        
        // GET all coffees to confirm our changes
        simulateHttpRequest("GET", "/coffees", null);
    }
    
    /**
     * Simulates an HTTP request and response cycle
     */
    private static void simulateHttpRequest(String method, String path, Coffee body) {
        requestCount++;
        
        // Request logging
        log("Client", "HTTP " + method + " " + path + 
            (body != null ? " with body: " + body : ""));
        
        // Process the request based on HTTP method and path
        HttpResponse response = processRequest(method, path, body);
        
        // Response logging
        log("Server", "HTTP " + response.getStatus() + " " + 
            (response.getBody() != null ? "with body: " + response.getBody() : "(No content)"));
        
        // Emphasize statelessness of REST
        log("Info", "Note: Each request contains all information needed (stateless). " +
            "Request #" + requestCount + " has no knowledge of previous requests.");
    }
    
    /**
     * Processes a simulated HTTP request
     */
    private static HttpResponse processRequest(String method, String path, Coffee body) {
        // Simulate server processing the request
        log("Server", "Processing " + method + " " + path);
        
        // Split path into segments for routing
        String[] pathSegments = path.split("/");
        
        // Basic routing based on path and HTTP method
        if (pathSegments.length >= 2 && "coffees".equals(pathSegments[1])) {
            // Route: /coffees
            if (pathSegments.length == 2) {
                if ("GET".equals(method)) {
                    // GET /coffees - Get all coffees
                    log("Server", "Retrieving all coffees");
                    return new HttpResponse(200, new ArrayList<>(coffeeDatabase.values()));
                } else if ("POST".equals(method)) {
                    // POST /coffees - Create a new coffee
                    if (body == null) {
                        return new HttpResponse(400, "Bad Request: No coffee data provided");
                    }
                    
                    log("Server", "Creating new coffee: " + body.getId());
                    if (coffeeDatabase.containsKey(body.getId())) {
                        return new HttpResponse(409, "Conflict: Coffee with ID " + body.getId() + " already exists");
                    }
                    
                    coffeeDatabase.put(body.getId(), body);
                    return new HttpResponse(201, body);
                } else {
                    return new HttpResponse(405, "Method Not Allowed");
                }
            } 
            // Route: /coffees/{id}
            else if (pathSegments.length == 3) {
                String coffeeId = pathSegments[2];
                
                if ("GET".equals(method)) {
                    // GET /coffees/{id} - Get a specific coffee
                    log("Server", "Retrieving coffee with ID: " + coffeeId);
                    Coffee coffee = coffeeDatabase.get(coffeeId);
                    if (coffee != null) {
                        return new HttpResponse(200, coffee);
                    } else {
                        return new HttpResponse(404, "Not Found: No coffee with ID " + coffeeId);
                    }
                } else if ("PUT".equals(method)) {
                    // PUT /coffees/{id} - Update a coffee
                    log("Server", "Updating coffee with ID: " + coffeeId);
                    if (!coffeeDatabase.containsKey(coffeeId)) {
                        return new HttpResponse(404, "Not Found: No coffee with ID " + coffeeId);
                    }
                    if (body == null) {
                        return new HttpResponse(400, "Bad Request: No coffee data provided");
                    }
                    
                    coffeeDatabase.put(coffeeId, body);
                    return new HttpResponse(200, body);
                } else if ("DELETE".equals(method)) {
                    // DELETE /coffees/{id} - Delete a coffee
                    log("Server", "Deleting coffee with ID: " + coffeeId);
                    if (!coffeeDatabase.containsKey(coffeeId)) {
                        return new HttpResponse(404, "Not Found: No coffee with ID " + coffeeId);
                    }
                    
                    Coffee removed = coffeeDatabase.remove(coffeeId);
                    return new HttpResponse(200, removed);
                } else {
                    return new HttpResponse(405, "Method Not Allowed");
                }
            }
        }
        
        return new HttpResponse(404, "Not Found: " + path);
    }
    
    // Logging helper
    private static void log(String source, String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        System.out.println("[" + timestamp + "] [" + source + "] " + message);
    }
    
    /**
     * Coffee resource model
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
            return "{\"id\":\"" + id + "\",\"name\":\"" + name + "\",\"price\":" + price + ",\"origin\":\"" + origin + "\"}";
        }
    }
    
    /**
     * Simple HTTP Response class
     */
    public static class HttpResponse {
        private int status;
        private Object body;
        
        public HttpResponse(int status, Object body) {
            this.status = status;
            this.body = body;
        }
        
        public int getStatus() {
            return status;
        }
        
        public Object getBody() {
            return body;
        }
    }
}
