package com.example.salestax;

import java.util.*;

public class SalesTaxCalculator {
    public static void main(String[] args) {
        // Simulate user input like it's coming from a text file or command line
        List<String> inputLines = List.of(
            "1 book at 12.49",
            "1 music CD at 14.99",
            "1 chocolate bar at 0.85"
        );

        // Process the input to calculate taxes and totals
        Receipt receipt = Receipt.generateFromInput(inputLines);

        // Print the final bill/receipt to console
        ReceiptPrinter.print(receipt);
    }
}

class Product {
    String name;
    double price;
    boolean isImported;
    boolean isExempt;

    public Product(String name, double price, boolean isImported, boolean isExempt) {
        this.name = name;
        this.price = price;
        this.isImported = isImported;
        this.isExempt = isExempt;
    }
}

class ProductParser {
    // This method converts a line like "1 book at 12.49" into a Product object
    public static Product parse(String inputLine) {
        String[] parts = inputLine.split(" at ");
        double price = Double.parseDouble(parts[1]);

        String namePart = parts[0].substring(parts[0].indexOf(' ') + 1);
        boolean isImported = namePart.contains("imported");

        // Exemptions: if it's food, books or medical, we say it's tax-exempt
        boolean isExempt = namePart.matches(".*(book|chocolate|pill).*");

        return new Product(namePart, price, isImported, isExempt);
    }
}

class ReceiptItem {
    Product product;
    int quantity;
    double tax;
    double totalPrice;

    public ReceiptItem(Product product, int quantity, double tax, double totalPrice) {
        this.product = product;
        this.quantity = quantity;
        this.tax = tax;
        this.totalPrice = totalPrice;
    }
}

class TaxCalculator {
    // Core logic to compute tax based on whether it's imported and/or exempt
    public static double calculateTax(Product product) {
        double tax = 0.0;
        if (!product.isExempt) {
            tax += 0.10 * product.price; // Basic tax
        }
        if (product.isImported) {
            tax += 0.05 * product.price; // Import duty
        }
        return roundTax(tax);
    }

    // Rounding tax to nearest 0.05 as per rules
    private static double roundTax(double tax) {
        return Math.ceil(tax * 20.0) / 20.0;
    }
}

class Receipt {
    List<ReceiptItem> items = new ArrayList<>();
    double totalTax = 0.0;
    double total = 0.0;

    public static Receipt generateFromInput(List<String> inputLines) {
        Receipt receipt = new Receipt();

        for (String line : inputLines) {
            Product product = ProductParser.parse(line);
            double tax = TaxCalculator.calculateTax(product);
            double totalPrice = product.price + tax;

            receipt.items.add(new ReceiptItem(product, 1, tax, totalPrice));
            receipt.totalTax += tax;
            receipt.total += totalPrice;
        }

        return receipt;
    }

    public double getTotalTax() {
        return totalTax;
    }

    public double getTotal() {
        return total;
    }

    public List<ReceiptItem> getItems() {
        return items;
    }
}

class ReceiptPrinter {
    // This method prints out each item and the overall totals in a nice format
    public static void print(Receipt receipt) {
        for (ReceiptItem item : receipt.getItems()) {
            System.out.printf("%d %s: %.2f\n", item.quantity, item.product.name, item.totalPrice);
        }
        System.out.printf("Sales Taxes: %.2f\n", receipt.getTotalTax());
        System.out.printf("Total: %.2f\n", receipt.getTotal());
    }
}
