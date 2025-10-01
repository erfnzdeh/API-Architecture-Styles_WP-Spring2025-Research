import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Educational demonstration of Webhook API concepts
 * 
 * This file simulates a webhook system with one service registering
 * for notifications from another service, and receiving callbacks
 * when events occur.
 * 
 * Features demonstrated:
 * - Webhook registration
 * - Event-driven architecture
 * - HTTP callbacks
 * - Asynchronous communication
 */
public class WebhookDemoService {
    
    // Payment service (the source service that will call webhooks)
    private static PaymentService paymentService = new PaymentService();
    
    // Order service (the receiving service that registers webhooks)
    private static OrderService orderService = new OrderService();
    
    // Thread pool for async operations
    private static ExecutorService executorService = Executors.newFixedThreadPool(3);
    
    public static void main(String[] args) throws InterruptedException {
        // Ensure we shut down the executor service when done
        try {
            log("Main", "Starting Webhook demonstration");
            
            // Order service registers webhooks with payment service
            setupWebhookRegistration();
            
            // Simulate various payment-related events
            simulatePaymentEvents();
            
            log("Main", "Webhook demonstration completed");
        } finally {
            executorService.shutdown();
            executorService.awaitTermination(5, TimeUnit.SECONDS);
        }
    }
    
    private static void setupWebhookRegistration() {
        log("OrderService", "=== Registering Webhooks ===");
        
        // In a real app, this would make an HTTP request to register webhook endpoints
        log("OrderService", "POST /api/webhooks/register");
        log("OrderService", "Payload: { " + 
            "\"url\": \"https://order-service.example.com/webhook/payment-events\", " +
            "\"events\": [\"payment.success\", \"payment.failure\", \"refund.processed\"] " +
            "}");
        
        // Payment service processes the registration
        log("PaymentService", "Processing webhook registration request");
        log("PaymentService", "Validating webhook URL...");
        log("PaymentService", "Storing webhook configuration");
        
        // Register the webhook endpoints
        paymentService.registerWebhook("https://order-service.example.com/webhook/payment-events", 
                                      List.of("payment.success", "payment.failure", "refund.processed"));
        
        log("PaymentService", "Webhook registration successful");
        log("OrderService", "Received confirmation of webhook registration");
        
        log("Info", "The OrderService is now subscribed to payment events");
        log("Info", "When events occur, the PaymentService will call the OrderService's webhook endpoint");
    }
    
    private static void simulatePaymentEvents() throws InterruptedException {
        log("Info", "=== Simulating Payment Events ===");
        log("Info", "We'll create several payment events that will trigger webhook calls");
        
        // Ensure we wait for all async operations to complete
        final CountDownLatch demoCompleteLatch = new CountDownLatch(1);
        
        // Simulate a successful payment
        simulateSuccessfulPayment("order-123", "customer-456", 59.99)
            .thenRun(() -> {
                try {
                    Thread.sleep(1500);
                    
                    // Simulate a failed payment
                    simulateFailedPayment("order-124", "customer-789", 129.50)
                        .thenRun(() -> {
                            try {
                                Thread.sleep(1500);
                                
                                // Simulate a refund
                                simulateRefund("order-123", "customer-456", 59.99)
                                    .thenRun(() -> demoCompleteLatch.countDown());
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        
        // Wait for all demonstrations to complete
        demoCompleteLatch.await(10, TimeUnit.SECONDS);
        
        log("Info", "All payment events have been processed");
        log("Info", "Note: Webhooks allow the OrderService to be updated about events without polling");
        log("Info", "This is more efficient than periodically checking for status updates");
    }
    
    private static CompletableFuture<Void> simulateSuccessfulPayment(
            String orderId, String customerId, double amount) {
        
        return CompletableFuture.runAsync(() -> {
            log("Client", "Submitting payment for order " + orderId);
            log("PaymentService", "Processing payment of $" + amount + " for order " + orderId);
            
            // Simulate payment processing delay
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Payment is successful
            String paymentId = "pmt-" + UUID.randomUUID().toString().substring(0, 8);
            PaymentEvent event = new PaymentEvent(
                "payment.success",
                paymentId,
                orderId,
                customerId,
                amount,
                "Payment processed successfully"
            );
            
            log("PaymentService", "Payment successful for order " + orderId + " (ID: " + paymentId + ")");
            
            // Trigger the webhook
            paymentService.triggerWebhook(event);
        }, executorService);
    }
    
    private static CompletableFuture<Void> simulateFailedPayment(
            String orderId, String customerId, double amount) {
        
        return CompletableFuture.runAsync(() -> {
            log("Client", "Submitting payment for order " + orderId);
            log("PaymentService", "Processing payment of $" + amount + " for order " + orderId);
            
            // Simulate payment processing delay
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Payment fails
            String paymentId = "pmt-" + UUID.randomUUID().toString().substring(0, 8);
            PaymentEvent event = new PaymentEvent(
                "payment.failure",
                paymentId,
                orderId,
                customerId,
                amount,
                "Insufficient funds"
            );
            
            log("PaymentService", "Payment failed for order " + orderId + ": Insufficient funds");
            
            // Trigger the webhook
            paymentService.triggerWebhook(event);
        }, executorService);
    }
    
    private static CompletableFuture<Void> simulateRefund(
            String orderId, String customerId, double amount) {
        
        return CompletableFuture.runAsync(() -> {
            log("Customer Service", "Processing refund for order " + orderId);
            log("PaymentService", "Issuing refund of $" + amount + " for order " + orderId);
            
            // Simulate refund processing delay
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // Refund is processed
            String refundId = "ref-" + UUID.randomUUID().toString().substring(0, 8);
            PaymentEvent event = new PaymentEvent(
                "refund.processed",
                refundId,
                orderId,
                customerId,
                amount,
                "Refund processed successfully"
            );
            
            log("PaymentService", "Refund processed for order " + orderId + " (ID: " + refundId + ")");
            
            // Trigger the webhook
            paymentService.triggerWebhook(event);
        }, executorService);
    }
    
    // Logging helper
    private static void log(String source, String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        System.out.println("[" + timestamp + "] [" + source + "] " + message);
    }
    
    /**
     * Payment service that triggers webhooks when payment events occur
     */
    public static class PaymentService {
        private List<WebhookRegistration> webhooks = new ArrayList<>();
        
        public void registerWebhook(String url, List<String> events) {
            webhooks.add(new WebhookRegistration(url, events));
        }
        
        public void triggerWebhook(PaymentEvent event) {
            log("PaymentService", "Event occurred: " + event.getType());
            
            // Find webhooks registered for this event type
            for (WebhookRegistration webhook : webhooks) {
                if (webhook.getEvents().contains(event.getType())) {
                    // In a real system, this would make an HTTP POST request to the webhook URL
                    log("PaymentService", "Triggering webhook: POST " + webhook.getUrl());
                    log("PaymentService", "Webhook payload: " + event);
                    
                    // Simulate network delay
                    CompletableFuture.runAsync(() -> {
                        try {
                            Thread.sleep(200); // Simulate network latency
                            
                            // Simulate the HTTP request to the order service
                            orderService.receiveWebhook(webhook.getUrl(), event);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }, executorService);
                }
            }
        }
    }
    
    /**
     * Order service that receives webhooks from the payment service
     */
    public static class OrderService {
        private Map<String, String> orderStatuses = new HashMap<>();
        
        public void receiveWebhook(String endpoint, PaymentEvent event) {
            // In a real system, this would be an HTTP endpoint receiving the webhook
            log("OrderService", "Received webhook on " + endpoint);
            log("OrderService", "Processing event: " + event.getType() + " for order " + event.getOrderId());
            
            // Update order status based on the payment event
            switch (event.getType()) {
                case "payment.success":
                    updateOrderStatus(event.getOrderId(), "PAID");
                    log("OrderService", "Updated order " + event.getOrderId() + " status to PAID");
                    
                    // Trigger fulfillment process
                    log("OrderService", "Triggering fulfillment process for order " + event.getOrderId());
                    break;
                    
                case "payment.failure":
                    updateOrderStatus(event.getOrderId(), "PAYMENT_FAILED");
                    log("OrderService", "Updated order " + event.getOrderId() + " status to PAYMENT_FAILED");
                    
                    // Notify customer about failed payment
                    log("OrderService", "Sending payment failure notification to customer " + event.getCustomerId());
                    break;
                    
                case "refund.processed":
                    updateOrderStatus(event.getOrderId(), "REFUNDED");
                    log("OrderService", "Updated order " + event.getOrderId() + " status to REFUNDED");
                    
                    // Notify customer about refund
                    log("OrderService", "Sending refund confirmation to customer " + event.getCustomerId());
                    break;
                    
                default:
                    log("OrderService", "Unknown event type: " + event.getType());
            }
            
            // Send a 200 OK response
            log("OrderService", "Webhook processed successfully");
            log("OrderService", "Responding with HTTP 200 OK");
        }
        
        private void updateOrderStatus(String orderId, String status) {
            orderStatuses.put(orderId, status);
        }
    }
    
    /**
     * Webhook registration information
     */
    public static class WebhookRegistration {
        private String url;
        private List<String> events;
        
        public WebhookRegistration(String url, List<String> events) {
            this.url = url;
            this.events = events;
        }
        
        public String getUrl() {
            return url;
        }
        
        public List<String> getEvents() {
            return events;
        }
    }
    
    /**
     * Payment event data
     */
    public static class PaymentEvent {
        private String type;
        private String paymentId;
        private String orderId;
        private String customerId;
        private double amount;
        private String message;
        
        public PaymentEvent(String type, String paymentId, String orderId, 
                           String customerId, double amount, String message) {
            this.type = type;
            this.paymentId = paymentId;
            this.orderId = orderId;
            this.customerId = customerId;
            this.amount = amount;
            this.message = message;
        }
        
        public String getType() {
            return type;
        }
        
        public String getPaymentId() {
            return paymentId;
        }
        
        public String getOrderId() {
            return orderId;
        }
        
        public String getCustomerId() {
            return customerId;
        }
        
        public double getAmount() {
            return amount;
        }
        
        public String getMessage() {
            return message;
        }
        
        @Override
        public String toString() {
            return "{\"type\":\"" + type + "\",\"paymentId\":\"" + paymentId + "\",\"orderId\":\"" + 
                   orderId + "\",\"customerId\":\"" + customerId + "\",\"amount\":" + amount + 
                   ",\"message\":\"" + message + "\"}";
        }
    }
}
