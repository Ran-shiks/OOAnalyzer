package example;

import java.util.List;

class BaseClass {
    protected int baseField;

    public void baseMethod() {
        System.out.println("Base method");
    }
}

class Helper {
    public void help() {}
}

class Logger {
    public void log(String msg) {
        System.out.println(msg);
    }
}

class Order {
    private int id;
    private double amount;
    private String status;

    public int getId() {
        return id;
    }

    public void process() {
        System.out.println("Processing: " + status);
    }

    public void reset() {
        status = "NEW";
        amount = 0.0;
    }
}

class OrderManager extends BaseClass {
    private Order order;
    private Logger logger;
    private int retryCount;

    public void createOrder() {
        order = new Order();
        logger.log("Order created");
    }

    public void cancelOrder() {
        order.reset();
        logger.log("Order cancelled");
    }

    public void printStatus() {
        System.out.println(order.getId());
    }
}

class Utility {
    public static void printSomething() {
        System.out.println("Utils");
    }
}

class Unrelated {
    private int x;
    private int y;

    public void method1() {
        int z = x + 2;
    }

    public void method2() {
        int w = y * 5;
    }

    public void method3() {
        System.out.println("No field access");
    }
}
