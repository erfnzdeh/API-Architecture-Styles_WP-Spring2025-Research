import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Educational demonstration of WebSocket API concepts
 * 
 * This file simulates a WebSocket service with manual implementation to show
 * how WebSocket communication works. In a real application, you would use a library
 * like Java WebSocket or Spring's WebSocket support.
 * 
 * Features demonstrated:
 * - Initial handshake
 * - Full-duplex communication
 * - Real-time updates
 * - Connection lifecycle
 * - Message broadcasting
 */
public class WebSocketDemoService {
    
    // Store of active client connections (in a real app, this would be WebSocket sessions)
    private static Map<String, ClientConnection> activeConnections = new HashMap<>();
    
    // Our "database" of coffees that changes over time
    private static Map<String, Coffee> coffeeInventory = new HashMap<>();
    
    public static void main(String[] args) throws InterruptedException {
        // Start the WebSocket server
        startWebSocketServer();
        
        // Simulate client connections and real-time updates
        simulateRealTimeInteractions();
    }
    
    private static void startWebSocketServer() {
        log("Server", "Starting WebSocket server on ws://localhost:8080/coffee-updates");
        
        // Initialize coffee inventory
        coffeeInventory.put("latte", new Coffee("latte", "Latte", 3.99, 10));
        coffeeInventory.put("espresso", new Coffee("espresso", "Espresso", 2.50, 15));
        coffeeInventory.put("cappuccino", new Coffee("cappuccino", "Cappuccino", 4.20, 8));
        
        log("Server", "WebSocket server started and ready to accept connections");
    }
    
    private static void simulateRealTimeInteractions() throws InterruptedException {
        final CountDownLatch demoCompleteLatch = new CountDownLatch(1);
        
        // Simulate handshake and connection setup
        simulateClientConnection("browser1", "Web Browser Client");
        simulateClientConnection("mobile1", "Mobile App Client");
        
        Thread.sleep(500);
        
        // First client sends a message to the server
        simulateClientMessage("browser1", "Subscribe to coffee inventory updates");
        
        Thread.sleep(500);
        
        // Server sends a message to all connected clients
        broadcastInventoryUpdate("Server announces: Coffee shop is now open!");
        
        Thread.sleep(1000);
        
        log("Info", "=== Demonstrating Real-Time Updates ===");
        log("Info", "Now we'll simulate inventory changes that are pushed to clients automatically");
        
        // Simulate inventory changes and notifications
        for (int i = 0; i < 3; i++) {
            // Simulate coffee being ordered/inventory changing
            simulateInventoryChange();
            
            // Wait a bit between updates
            Thread.sleep(1500);
        }
        
        // Second client sends a specific request
        simulateClientMessage("mobile1", "Request: Current price of Cappuccino");
        
        Thread.sleep(500);
        
        // Server responds directly to that client
        simulateServerToClientMessage("mobile1", 
            "Cappuccino current price: $" + coffeeInventory.get("cappuccino").getPrice());
        
        Thread.sleep(1000);
        
        // Client disconnects
        simulateClientDisconnection("browser1");
        
        Thread.sleep(800);
        
        // Even with one client disconnected, other clients still get updates
        simulateInventoryChange();
        
        Thread.sleep(1000);
        
        // Clean up remaining connections
        simulateClientDisconnection("mobile1");
        
        log("Info", "WebSocket demo completed");
        log("Info", "Note: Unlike REST which is request-response, WebSocket maintains a persistent connection");
        log("Info", "This allows real-time, bidirectional communication without the overhead of establishing new connections");
        
        demoCompleteLatch.countDown();
        demoCompleteLatch.await(1, TimeUnit.SECONDS);
    }
    
    private static void simulateClientConnection(String clientId, String clientInfo) {
        // Simulate the WebSocket handshake
        log("Client (" + clientId + ")", "Initiating WebSocket handshake with HTTP Upgrade request");
        log("Client (" + clientId + ")", "GET /coffee-updates HTTP/1.1");
        log("Client (" + clientId + ")", "Upgrade: websocket");
        log("Client (" + clientId + ")", "Connection: Upgrade");
        log("Client (" + clientId + ")", "Sec-WebSocket-Key: dGhlIHNhbXBsZSBub25jZQ==");
        log("Client (" + clientId + ")", "Sec-WebSocket-Version: 13");
        
        // Simulate server accepting the WebSocket connection
        log("Server", "Received WebSocket handshake request from " + clientInfo + " (" + clientId + ")");
        log("Server", "HTTP/1.1 101 Switching Protocols");
        log("Server", "Upgrade: websocket");
        log("Server", "Connection: Upgrade");
        log("Server", "Sec-WebSocket-Accept: s3pPLMBiTxaQ9kYGzzhZRbK+xOo=");
        
        // Add to active connections
        activeConnections.put(clientId, new ClientConnection(clientId, clientInfo));
        
        log("Server", "WebSocket connection established with " + clientInfo + " (" + clientId + ")");
        log("Client (" + clientId + ")", "WebSocket connection established");
        
        // Send initial data
        String inventorySummary = generateInventorySummary();
        simulateServerToClientMessage(clientId, "Welcome! Current inventory: " + inventorySummary);
    }
    
    private static void simulateClientMessage(String clientId, String message) {
        if (!activeConnections.containsKey(clientId)) {
            log("Error", "Cannot send message: Client " + clientId + " is not connected");
            return;
        }
        
        log("Client (" + clientId + ")", "Sending message: " + message);
        log("Server", "Received message from " + clientId + ": " + message);
        
        // In a real app, we'd process the message here
    }
    
    private static void simulateServerToClientMessage(String clientId, String message) {
        if (!activeConnections.containsKey(clientId)) {
            log("Error", "Cannot send message: Client " + clientId + " is not connected");
            return;
        }
        
        ClientConnection client = activeConnections.get(clientId);
        log("Server", "Sending message to " + client.getClientInfo() + " (" + clientId + "): " + message);
        log("Client (" + clientId + ")", "Received message: " + message);
    }
    
    private static void broadcastInventoryUpdate(String message) {
        log("Server", "Broadcasting message to all connected clients: " + message);
        
        for (ClientConnection client : activeConnections.values()) {
            log("Client (" + client.getClientId() + ")", "Received broadcast message: " + message);
        }
    }
    
    private static void simulateInventoryChange() {
        // Randomly select a coffee to update
        String[] coffeeIds = {"latte", "espresso", "cappuccino"};
        String selectedCoffeeId = coffeeIds[(int)(Math.random() * coffeeIds.length)];
        
        Coffee coffee = coffeeInventory.get(selectedCoffeeId);
        boolean isQuantityChange = Math.random() > 0.5;
        
        // Update either quantity or price
        if (isQuantityChange) {
            int oldQuantity = coffee.getQuantity();
            int newQuantity = Math.max(0, oldQuantity - (int)(Math.random() * 3 + 1));
            coffee.setQuantity(newQuantity);
            
            log("Server", "Inventory change: " + coffee.getName() + " quantity updated from " + 
                oldQuantity + " to " + newQuantity);
        } else {
            double oldPrice = coffee.getPrice();
            double priceChange = Math.random() * 0.5 - 0.25; // -0.25 to +0.25
            double newPrice = Math.max(1.0, Math.round((oldPrice + priceChange) * 100) / 100.0);
            coffee.setPrice(newPrice);
            
            log("Server", "Inventory change: " + coffee.getName() + " price updated from $" + 
                oldPrice + " to $" + newPrice);
        }
        
        // Broadcast the update to all connected clients
        String updateMessage = generateUpdateMessage(selectedCoffeeId, isQuantityChange);
        broadcastInventoryUpdate(updateMessage);
    }
    
    private static String generateUpdateMessage(String coffeeId, boolean isQuantityChange) {
        Coffee coffee = coffeeInventory.get(coffeeId);
        if (isQuantityChange) {
            return "INVENTORY UPDATE: " + coffee.getName() + " - " + coffee.getQuantity() + " remaining";
        } else {
            return "PRICE UPDATE: " + coffee.getName() + " - now $" + coffee.getPrice();
        }
    }
    
    private static String generateInventorySummary() {
        StringBuilder summary = new StringBuilder();
        for (Coffee coffee : coffeeInventory.values()) {
            summary.append(coffee.getName())
                   .append(" ($").append(coffee.getPrice()).append(") - ")
                   .append(coffee.getQuantity()).append(" available; ");
        }
        return summary.toString();
    }
    
    private static void simulateClientDisconnection(String clientId) {
        if (!activeConnections.containsKey(clientId)) {
            log("Error", "Cannot disconnect: Client " + clientId + " is not connected");
            return;
        }
        
        ClientConnection client = activeConnections.get(clientId);
        log("Client (" + clientId + ")", "Closing WebSocket connection");
        log("Server", "Client " + client.getClientInfo() + " (" + clientId + ") disconnected");
        
        activeConnections.remove(clientId);
        log("Server", "Active connections remaining: " + activeConnections.size());
    }
    
    // Logging helper
    private static void log(String source, String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        System.out.println("[" + timestamp + "] [" + source + "] " + message);
    }
    
    /**
     * Represents a client WebSocket connection
     */
    public static class ClientConnection {
        private String clientId;
        private String clientInfo;
        
        public ClientConnection(String clientId, String clientInfo) {
            this.clientId = clientId;
            this.clientInfo = clientInfo;
        }
        
        public String getClientId() {
            return clientId;
        }
        
        public String getClientInfo() {
            return clientInfo;
        }
    }
    
    /**
     * Coffee model with inventory information
     */
    public static class Coffee {
        private String id;
        private String name;
        private double price;
        private int quantity;
        
        public Coffee(String id, String name, double price, int quantity) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.quantity = quantity;
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
        
        public void setPrice(double price) {
            this.price = price;
        }
        
        public int getQuantity() {
            return quantity;
        }
        
        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}
