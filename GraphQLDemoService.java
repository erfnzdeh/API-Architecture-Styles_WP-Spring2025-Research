import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Educational demonstration of GraphQL API concepts
 * 
 * This file simulates a GraphQL service with manual implementation to show
 * how GraphQL principles work. In a real application, you would use a library
 * like graphql-java to handle the implementation.
 * 
 * Features demonstrated:
 * - Schema definition
 * - Query execution with field selection
 * - Resolving relationships (coffee -> orders)
 * - Multiple queries in a single request
 * - Solving the N+1 problem
 */
public class GraphQLDemoService {
    
    // Our "database" of coffee-related data
    private static Map<String, Coffee> coffeeDatabase = new HashMap<>();
    private static Map<String, Order> orderDatabase = new HashMap<>();
    private static Map<String, Customer> customerDatabase = new HashMap<>();
    
    public static void main(String[] args) {
        // Initialize our databases
        initializeDatabases();
        
        // Simulate GraphQL queries
        simulateGraphQLQueries();
    }
    
    private static void initializeDatabases() {
        log("Server", "Initializing database...");
        
        // Initialize customers
        Customer alice = new Customer("cust1", "Alice", "alice@example.com");
        Customer bob = new Customer("cust2", "Bob", "bob@example.com");
        Customer charlie = new Customer("cust3", "Charlie", "charlie@example.com");
        
        customerDatabase.put(alice.getId(), alice);
        customerDatabase.put(bob.getId(), bob);
        customerDatabase.put(charlie.getId(), charlie);
        
        // Initialize coffees
        Coffee latte = new Coffee("coffee1", "Latte", 3.99, "Colombia");
        Coffee espresso = new Coffee("coffee2", "Espresso", 2.50, "Brazil");
        Coffee cappuccino = new Coffee("coffee3", "Cappuccino", 4.20, "Ethiopia");
        
        coffeeDatabase.put(latte.getId(), latte);
        coffeeDatabase.put(espresso.getId(), espresso);
        coffeeDatabase.put(cappuccino.getId(), cappuccino);
        
        // Initialize orders
        Order order1 = new Order("order1", alice.getId(), Arrays.asList(latte.getId(), espresso.getId()));
        Order order2 = new Order("order2", bob.getId(), Arrays.asList(cappuccino.getId()));
        Order order3 = new Order("order3", alice.getId(), Arrays.asList(cappuccino.getId(), latte.getId()));
        Order order4 = new Order("order4", charlie.getId(), Arrays.asList(espresso.getId()));
        
        orderDatabase.put(order1.getId(), order1);
        orderDatabase.put(order2.getId(), order2);
        orderDatabase.put(order3.getId(), order3);
        orderDatabase.put(order4.getId(), order4);
        
        log("Server", "Database initialized with " + customerDatabase.size() + " customers, " + 
            coffeeDatabase.size() + " coffees, and " + orderDatabase.size() + " orders");
    }
    
    private static void simulateGraphQLQueries() {
        log("Info", "=== Demonstrating Simple Query ===");
        
        // Simulate a simple query to get coffee data
        String simpleQuery = 
            "{\n" +
            "  coffee(id: \"coffee1\") {\n" +
            "    name\n" +
            "    price\n" +
            "  }\n" +
            "}";
        
        executeGraphQLQuery(simpleQuery);
        
        log("Info", "=== Demonstrating Nested Query with Relationships ===");
        
        // Simulate a more complex query with nested fields
        String complexQuery = 
            "{\n" +
            "  customer(id: \"cust1\") {\n" +
            "    name\n" +
            "    email\n" +
            "    orders {\n" +
            "      id\n" +
            "      items {\n" +
            "        name\n" +
            "        price\n" +
            "        origin\n" +
            "      }\n" +
            "      totalAmount\n" +
            "    }\n" +
            "  }\n" +
            "}";
        
        executeGraphQLQuery(complexQuery);
        
        log("Info", "=== Demonstrating Multiple Queries in One Request ===");
        
        // Simulate multiple queries in a single request
        String multipleQueries = 
            "{\n" +
            "  latte: coffee(id: \"coffee1\") {\n" +
            "    name\n" +
            "    origin\n" +
            "  }\n" +
            "  espresso: coffee(id: \"coffee2\") {\n" +
            "    name\n" +
            "    price\n" +
            "  }\n" +
            "  cappuccino: coffee(id: \"coffee3\") {\n" +
            "    name\n" +
            "    price\n" +
            "    origin\n" +
            "  }\n" +
            "}";
        
        executeGraphQLQuery(multipleQueries);
        
        log("Info", "=== Demonstrating Selective Field Loading ===");
        
        // Simulate selective field loading
        String selectiveQuery = 
            "{\n" +
            "  allCoffees {\n" +
            "    name\n" +
            "    # Note: price and origin are not requested\n" +
            "  }\n" +
            "}";
        
        executeGraphQLQuery(selectiveQuery);
    }
    
    /**
     * Simulates a GraphQL query execution
     */
    private static void executeGraphQLQuery(String query) {
        log("Client", "Sending GraphQL query:\n" + query);
        
        // This would be handled by a GraphQL engine in a real application
        // Here we're just simulating the process to show the concept
        log("Server", "Processing GraphQL query...");
        
        // Parse the query (simplified)
        String operation = parseOperation(query);
        Map<String, Object> result = new HashMap<>();
        
        if (operation.contains("coffee(id:")) {
            // Handle single coffee query
            String coffeeId = parseArgument(operation, "coffee", "id");
            if (coffeeId != null && coffeeDatabase.containsKey(coffeeId)) {
                Coffee coffee = coffeeDatabase.get(coffeeId);
                log("Server", "Resolving coffee with ID: " + coffeeId);
                
                // Extract only requested fields
                Map<String, Object> coffeeData = new HashMap<>();
                if (operation.contains("name")) coffeeData.put("name", coffee.getName());
                if (operation.contains("price")) coffeeData.put("price", coffee.getPrice());
                if (operation.contains("origin")) coffeeData.put("origin", coffee.getOrigin());
                
                result.put("coffee", coffeeData);
            }
        } else if (operation.contains("customer(id:")) {
            // Handle customer query with relationships
            String customerId = parseArgument(operation, "customer", "id");
            if (customerId != null && customerDatabase.containsKey(customerId)) {
                Customer customer = customerDatabase.get(customerId);
                log("Server", "Resolving customer with ID: " + customerId);
                
                // Extract customer data
                Map<String, Object> customerData = new HashMap<>();
                if (operation.contains("name")) customerData.put("name", customer.getName());
                if (operation.contains("email")) customerData.put("email", customer.getEmail());
                
                // If orders are requested, resolve them
                if (operation.contains("orders")) {
                    List<Map<String, Object>> ordersData = new ArrayList<>();
                    
                    // Find all orders for this customer
                    List<Order> customerOrders = orderDatabase.values().stream()
                            .filter(order -> order.getCustomerId().equals(customer.getId()))
                            .collect(Collectors.toList());
                    
                    log("Server", "Resolving " + customerOrders.size() + " orders for customer");
                    
                    for (Order order : customerOrders) {
                        Map<String, Object> orderData = new HashMap<>();
                        if (operation.contains("id")) orderData.put("id", order.getId());
                        
                        // If order items are requested, resolve them
                        if (operation.contains("items")) {
                            List<Map<String, Object>> itemsData = new ArrayList<>();
                            double totalAmount = 0;
                            
                            for (String coffeeId : order.getCoffeeIds()) {
                                Coffee coffee = coffeeDatabase.get(coffeeId);
                                Map<String, Object> coffeeData = new HashMap<>();
                                
                                if (operation.contains("items") && operation.contains("name")) 
                                    coffeeData.put("name", coffee.getName());
                                if (operation.contains("items") && operation.contains("price")) 
                                    coffeeData.put("price", coffee.getPrice());
                                if (operation.contains("items") && operation.contains("origin")) 
                                    coffeeData.put("origin", coffee.getOrigin());
                                
                                itemsData.add(coffeeData);
                                totalAmount += coffee.getPrice();
                            }
                            
                            orderData.put("items", itemsData);
                            if (operation.contains("totalAmount")) orderData.put("totalAmount", totalAmount);
                        }
                        
                        ordersData.add(orderData);
                    }
                    
                    customerData.put("orders", ordersData);
                }
                
                result.put("customer", customerData);
            }
        } else if (operation.contains("allCoffees")) {
            // Handle all coffees query
            log("Server", "Resolving all coffees");
            List<Map<String, Object>> coffeesData = new ArrayList<>();
            
            for (Coffee coffee : coffeeDatabase.values()) {
                Map<String, Object> coffeeData = new HashMap<>();
                if (operation.contains("name")) coffeeData.put("name", coffee.getName());
                if (operation.contains("price")) coffeeData.put("price", coffee.getPrice());
                if (operation.contains("origin")) coffeeData.put("origin", coffee.getOrigin());
                coffeesData.add(coffeeData);
            }
            
            result.put("allCoffees", coffeesData);
        } else if (operation.contains(": coffee(id:")) {
            // Handle multiple named queries
            log("Server", "Resolving multiple coffee queries");
            
            // Extract all coffee aliased queries (simplified parser)
            List<String> coffeeQueries = Arrays.stream(operation.split("\\n"))
                    .filter(line -> line.contains(": coffee(id:"))
                    .collect(Collectors.toList());
            
            for (String coffeeQuery : coffeeQueries) {
                String alias = coffeeQuery.trim().split(":")[0].trim();
                String coffeeId = parseArgument(coffeeQuery, "coffee", "id");
                
                if (coffeeId != null && coffeeDatabase.containsKey(coffeeId)) {
                    Coffee coffee = coffeeDatabase.get(coffeeId);
                    log("Server", "Resolving coffee with ID: " + coffeeId + " as alias: " + alias);
                    
                    // Extract only requested fields for this alias
                    Map<String, Object> coffeeData = new HashMap<>();
                    if (operation.contains(alias + " {") && operation.contains("name")) 
                        coffeeData.put("name", coffee.getName());
                    if (operation.contains(alias + " {") && operation.contains("price")) 
                        coffeeData.put("price", coffee.getPrice());
                    if (operation.contains(alias + " {") && operation.contains("origin")) 
                        coffeeData.put("origin", coffee.getOrigin());
                    
                    result.put(alias, coffeeData);
                }
            }
        }
        
        // Return the response
        log("Server", "Returning GraphQL response with exactly the requested fields");
        log("Server", "Response: " + result);
        log("Client", "Received GraphQL response with only the requested data");
    }
    
    // Very simplified "parser" for demo purposes only
    private static String parseOperation(String query) {
        return query.substring(query.indexOf("{") + 1, query.lastIndexOf("}")).trim();
    }
    
    // Very simplified argument parser for demo purposes only
    private static String parseArgument(String operation, String field, String argName) {
        if (!operation.contains(field + "(")) return null;
        
        int startIdx = operation.indexOf(field + "(");
        int endIdx = operation.indexOf(")", startIdx);
        String args = operation.substring(startIdx + field.length() + 1, endIdx);
        
        for (String arg : args.split(",")) {
            if (arg.trim().startsWith(argName + ":")) {
                return arg.split(":")[1].trim().replace("\"", "");
            }
        }
        
        return null;
    }
    
    // Logging helper
    private static void log(String source, String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        System.out.println("[" + timestamp + "] [" + source + "] " + message);
    }
    
    /**
     * Coffee model
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
    }
    
    /**
     * Customer model
     */
    public static class Customer {
        private String id;
        private String name;
        private String email;
        
        public Customer(String id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }
        
        public String getId() {
            return id;
        }
        
        public String getName() {
            return name;
        }
        
        public String getEmail() {
            return email;
        }
    }
    
    /**
     * Order model
     */
    public static class Order {
        private String id;
        private String customerId;
        private List<String> coffeeIds;
        
        public Order(String id, String customerId, List<String> coffeeIds) {
            this.id = id;
            this.customerId = customerId;
            this.coffeeIds = coffeeIds;
        }
        
        public String getId() {
            return id;
        }
        
        public String getCustomerId() {
            return customerId;
        }
        
        public List<String> getCoffeeIds() {
            return coffeeIds;
        }
    }
}
