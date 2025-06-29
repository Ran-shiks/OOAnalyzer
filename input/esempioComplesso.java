package ecommerce;

import java.util.List;
import java.util.ArrayList;

public class OrderManager extends BaseManager {

    private List orders;
    private PaymentService paymentService;
    private Logger logger;

    public OrderManager() {
        this.orders = new ArrayList();
        this.paymentService = new PaymentService();
        this.logger = Logger.getInstance();
    }

    public void addOrder(Order order) {
        orders.add(order);
        logger.log("Order added: " + order.getId());
    }

    public boolean processOrder(int orderId) {
        Order order = findOrder(orderId);
        if (order == null) {
            logger.log("Order not found: " + orderId);
            return false;
        }
        boolean result = paymentService.processPayment(order.getTotal());
        if (result) {
            order.setProcessed(true);
            logger.log("Order processed: " + orderId);
        }
        return result;
    }

    private Order findOrder(int orderId) {
        for (Order order : orders) {
            if (order.getId() == orderId) {
                return order;
            }
        }
        return null;
    }

    public Lis getAllOrders() {
        return orders;
    }
}



public class BaseManager {
    public void logStart() {
        System.out.println("Starting manager...");
    }

    public void logEnd() {
        System.out.println("Ending manager...");
    }
}



public class Order {
    private int id;
    private double total;
    private boolean processed;

    public Order(int id, double total) {
        this.id = id;
        this.total = total;
        this.processed = false;
    }

    public int getId() { return id; }
    public double getTotal() { return total; }
    public boolean isProcessed() { return processed; }
    public void setProcessed(boolean processed) { this.processed = processed; }
}



public class PaymentService {
    public boolean processPayment(double amount) {
        // Simula logica di pagamento
        return amount > 0;
    }
}



public class Logger {
    private static Logger instance = new Logger();

    private Logger() {}

    public static Logger getInstance() {
        return instance;
    }

    public void log(String message) {
        System.out.println("[LOG] " + message);
    }
}
