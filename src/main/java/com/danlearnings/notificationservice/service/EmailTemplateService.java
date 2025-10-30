package com.danlearnings.notificationservice.service;

import org.springframework.stereotype.Service;

@Service
public class EmailTemplateService {

    public String getTestEmailBody(String recipientName) {
        return String.format("""
                Hello %s!
                
                This is a TEST email from the E-commerce Notification Service.
                
                If you received this email, the notification system is working correctly!
                
                Best regards,
                E-commerce Ecosystem Team
                """, recipientName);
    }

    public String getOrderCreatedEmailBody(String orderId, String customerEmail, Double totalAmount) {
        return String.format("""
                Order Created Successfully!
                
                Dear Customer,
                
                Your order has been created successfully.
                
                Order Details:
                - Order ID: %s
                - Customer: %s
                - Total Amount: $%.2f
                
                Please complete the payment within 10 minutes to confirm your order.
                
                Thank you for shopping with us!
                
                Best regards,
                E-commerce Ecosystem Team
                """, orderId, customerEmail, totalAmount);
    }

    public String getOrderPaidEmailBody(String orderId, String customerEmail) {
        return String.format("""
                Payment Confirmed!
                
                Dear Customer,
                
                Your payment has been confirmed successfully.
                
                Order Details:
                - Order ID: %s
                - Customer: %s
                
                Your order is now being processed and will be shipped soon.
                
                Thank you for your purchase!
                
                Best regards,
                E-commerce Ecosystem Team
                """, orderId, customerEmail);
    }

    public String getOrderExpiredEmailBody(String orderId, String customerEmail) {
        return String.format("""
                Order Expired
                
                Dear Customer,
                
                Unfortunately, your order has expired due to payment timeout.
                
                Order Details:
                - Order ID: %s
                - Customer: %s
                
                The reserved items have been released back to inventory.
                You can create a new order anytime.
                
                Best regards,
                E-commerce Ecosystem Team
                """, orderId, customerEmail);
    }

    public String getPaymentFailedEmailBody(String orderId, String customerEmail, String reason) {
        return String.format("""
                Payment Failed
                
                Dear Customer,
                
                Your payment could not be processed.
                
                Order Details:
                - Order ID: %s
                - Customer: %s
                - Reason: %s
                
                Please try again with a different payment method.
                
                Best regards,
                E-commerce Ecosystem Team
                """, orderId, customerEmail, reason);
    }

    public String getStockAvailableEmailBody(String productName, String customerEmail) {
        return String.format("""
                Product Available Again!
                
                Dear Customer,
                
                Good news! The product you were waiting for is now available.
                
                Product: %s
                Customer: %s
                
                Hurry! Limited stock available.
                
                Best regards,
                E-commerce Ecosystem Team
                """, productName, customerEmail);
    }
}