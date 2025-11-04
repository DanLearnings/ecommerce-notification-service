# 📧 E-commerce Notification Service

A microservice responsible for sending notifications (emails, SMS, push notifications) in the E-commerce Microservices Ecosystem. This service consumes events from RabbitMQ and sends notifications via various channels, maintaining a complete history of all notifications sent.

---

## 🎯 Purpose

The Notification Service acts as a **central notification hub** for the entire e-commerce platform. Instead of each service implementing its own notification logic, they publish events to RabbitMQ, and this service handles all notification delivery asynchronously.

### Key Responsibilities:

- ✅ **Email Delivery** via Gmail SMTP (currently implemented)
- ✅ **Event Consumption** from RabbitMQ queues
- ✅ **Notification History** - Persistent storage of all notifications
- ✅ **Template Management** - Pre-defined email templates for various events
- ⏳ **SMS Delivery** (planned)
- ⏳ **Push Notifications** (planned)

---

## 🏗️ Architecture

### Event-Driven Design

```
┌─────────────────┐         ┌──────────────┐         ┌─────────────────┐
│ Order Service   │         │   RabbitMQ   │         │  Notification   │
│                 │─publish─│              │─consume─│    Service      │
│ (Event Source)  │         │  (Broker)    │         │  (Consumer)     │
└─────────────────┘         └──────────────┘         └────────┬────────┘
                                                               │
                                    ┌──────────────────────────┤
                                    │                          │
                            ┌───────▼────────┐         ┌───────▼────────┐
                            │  Gmail SMTP    │         │  H2 Database   │
                            │  (Email Send)  │         │  (History)     │
                            └────────────────┘         └────────────────┘
```

### Communication Patterns

**Asynchronous (RabbitMQ):**
- Order Service → `OrderCreated` event → Notification Service → Email
- Payment Service → `PaymentApproved` event → Notification Service → Email
- Inventory Service → `StockAvailable` event → Notification Service → Email

**Synchronous (REST):**
- Direct notification requests via POST `/notifications/send`
- Health checks via `/actuator/health`
- Notification history queries via GET `/notifications`

---

## 🛠️ Tech Stack

- **Framework:** Spring Boot 3.5.7
- **Language:** Java 21
- **Messaging:** Spring AMQP (RabbitMQ)
- **Email:** Spring Boot Starter Mail (JavaMail)
- **Database:** H2 (in-memory) → PostgreSQL (planned)
- **ORM:** Spring Data JPA
- **Service Discovery:** Eureka Client
- **Configuration:** Spring Cloud Config Client
- **Utilities:** Lombok
- **Monitoring:** Spring Boot Actuator

---

## 📦 Project Structure

```
ecommerce-notification-service/
├── src/
│   ├── main/
│   │   ├── java/com/danlearnings/notificationservice/
│   │   │   ├── config/
│   │   │   │   └── RabbitMQConfig.java           # RabbitMQ exchanges, queues, bindings
│   │   │   ├── controller/
│   │   │   │   └── NotificationController.java   # REST endpoints
│   │   │   ├── dto/
│   │   │   │   ├── NotificationEvent.java        # Event payload structure
│   │   │   │   ├── SendEmailRequest.java         # Direct send request
│   │   │   │   └── TestEmailRequest.java         # Test email request
│   │   │   ├── listener/
│   │   │   │   └── NotificationListener.java     # RabbitMQ consumer
│   │   │   ├── model/
│   │   │   │   ├── Notification.java             # JPA entity
│   │   │   │   ├── NotificationStatus.java       # Enum (PENDING, SENT, FAILED)
│   │   │   │   └── NotificationType.java         # Enum (EMAIL, SMS, PUSH)
│   │   │   ├── repository/
│   │   │   │   └── NotificationRepository.java   # Spring Data JPA
│   │   │   ├── service/
│   │   │   │   ├── EmailTemplateService.java     # Email templates
│   │   │   │   └── NotificationService.java      # Business logic
│   │   │   └── NotificationServiceApplication.java
│   │   └── resources/
│   │       └── application.yml                    # Local configuration
│   └── test/
├── Dockerfile                                     # Multi-stage Docker build
├── pom.xml                                        # Maven dependencies
└── README.md                                      # This file
```

---

## 🚀 Getting Started

### Prerequisites

- **Java 21** or higher
- **Maven 3.8+**
- **RabbitMQ** (running on localhost:5672 or via Docker)
- **Gmail Account** with App Password configured
- **Eureka Server** (running on localhost:8761)
- **Config Server** (running on localhost:8888)

---

## 💻 Running Locally (Development)

### 1. Configure Gmail SMTP

Create a Gmail App Password:
1. Go to https://myaccount.google.com/security
2. Enable "2-Step Verification"
3. Go to "App passwords"
4. Create a new app password for "Mail"
5. Copy the 16-character password

### 2. Update Configuration

Edit `src/main/resources/application.yml`:

```yaml
spring:
  mail:
    username: your-email@gmail.com
    password: your-16-char-app-password
```

### 3. Start Dependencies

Ensure these services are running:
- RabbitMQ (localhost:5672)
- Eureka Server (localhost:8761)
- Config Server (localhost:8888)

### 4. Run the Service

```bash
mvn spring-boot:run
```

**Expected Output:**
```
Started NotificationServiceApplication in X seconds
Registering application NOTIFICATION-SERVICE with eureka
```

### 5. Verify Registration

Open Eureka Dashboard: http://localhost:8761

You should see **NOTIFICATION-SERVICE** registered.

---

## 🐳 Running with Docker

### Build Docker Image

```bash
docker build -t ecommerce-notification-service:latest .
```

### Run Container

```bash
docker run -d \
  --name notification-service \
  --network ecommerce-network \
  -p 8083:8083 \
  -e SPRING_MAIL_USERNAME=your-email@gmail.com \
  -e SPRING_MAIL_PASSWORD=your-app-password \
  ecommerce-notification-service:latest
```

### Using Docker Compose

See the main [ecommerce-ecosystem](https://github.com/DanLearnings/ecommerce-ecosystem) repository for the complete `docker-compose.yml` configuration.

---

## 🧪 Testing the Service

### 1. Health Check

```bash
curl http://localhost:8083/actuator/health
```

**Expected Response:**
```json
{
  "status": "UP"
}
```

---

### 2. Send Test Email (Direct REST Call)

```bash
curl -X POST http://localhost:8083/notifications/test \
  -H "Content-Type: application/json" \
  -d '{
    "recipient": "your-email@gmail.com",
    "name": "Test User"
  }'
```

**Expected:** You receive an email with subject "🎉 Test Email - E-commerce Notification Service"

---

### 3. Send Email via RabbitMQ (Event-Driven)

**Option A: Via RabbitMQ Management UI**

1. Open http://localhost:15672 (admin/admin)
2. Go to **Queues** → `notification-queue`
3. Click **Publish message**
4. Paste this JSON:

```json
{
  "eventType": "ORDER_CREATED",
  "recipient": "your-email@gmail.com",
  "subject": "Your Order Has Been Created!",
  "body": "Thank you for your order. Your order has been successfully created and is being processed.",
  "relatedEntityType": "ORDER",
  "relatedEntityId": "12345"
}
```

5. Click **Publish message**
6. **Check your email inbox!** 📧

**Option B: Publish Event from Another Service**

```java
@Autowired
private RabbitTemplate rabbitTemplate;

public void notifyOrderCreated(Order order) {
    NotificationEvent event = NotificationEvent.builder()
        .eventType("ORDER_CREATED")
        .recipient(order.getCustomerEmail())
        .subject("Order Created: #" + order.getId())
        .body("Your order has been created successfully!")
        .relatedEntityType("ORDER")
        .relatedEntityId(order.getId().toString())
        .build();
    
    rabbitTemplate.convertAndSend(
        "notification-exchange",
        "notification.email",
        event
    );
}
```

---

### 4. Query Notification History

```bash
# Get all notifications
curl http://localhost:8083/notifications

# Get notifications by recipient
curl http://localhost:8083/notifications/recipient/your-email@gmail.com

# Get notifications by status
curl http://localhost:8083/notifications/status/SENT

# Get specific notification
curl http://localhost:8083/notifications/1
```

---

### 5. Access H2 Console (Development)

```
URL: http://localhost:8083/h2-console
JDBC URL: jdbc:h2:mem:notificationdb
Username: sa
Password: (leave blank)
```

**Query notifications:**
```sql
SELECT * FROM NOTIFICATIONS ORDER BY CREATED_AT DESC;
```

---

## 📧 Email Templates

The service includes pre-defined templates for common e-commerce events:

### Available Templates:

1. **Test Email** - For testing purposes
2. **Order Created** - Sent when a new order is created
3. **Order Paid** - Sent when payment is confirmed
4. **Order Expired** - Sent when order payment timeout occurs
5. **Payment Failed** - Sent when payment is declined
6. **Stock Available** - Sent to waitlist when product is back in stock

### Adding New Templates

Edit `EmailTemplateService.java` and add a new method:

```java
public String getOrderShippedEmailBody(String orderId, String trackingNumber) {
    return String.format("""
        Your Order Has Been Shipped!
        
        Order ID: %s
        Tracking Number: %s
        
        You can track your order using the link below.
        
        Best regards,
        E-commerce Team
        """, orderId, trackingNumber);
}
```

---

## 🔧 Configuration

### Application Properties (application.yml)

```yaml
server:
  port: 8083

spring:
  application:
    name: notification-service
  
  # RabbitMQ
  rabbitmq:
    host: localhost  # Use 'rabbitmq' in Docker
    port: 5672
    username: admin
    password: admin
  
  # Gmail SMTP
  mail:
    host: smtp.gmail.com
    port: 587
    username: ${SPRING_MAIL_USERNAME}
    password: ${SPRING_MAIL_PASSWORD}
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
            required: true

# Eureka Client
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/  # Use 'service-registry' in Docker
```

---

## 📊 RabbitMQ Configuration

### Exchanges

- **Name:** `notification-exchange`
- **Type:** Topic
- **Durable:** Yes

### Queues

- **Name:** `notification-queue`
- **Durable:** Yes
- **Auto-delete:** No

### Bindings

- **Exchange:** `notification-exchange`
- **Queue:** `notification-queue`
- **Routing Key:** `notification.*`

### Routing Key Patterns

- `notification.email` → Email notifications
- `notification.sms` → SMS notifications (future)
- `notification.push` → Push notifications (future)

---

## 🚨 Troubleshooting

### Issue: Emails not sending

**Possible Causes:**
1. Invalid Gmail credentials
2. App Password not configured
3. SMTP port blocked by firewall
4. 2-Step Verification not enabled

**Solution:**
1. Verify Gmail App Password is correct (16 characters, no spaces)
2. Check logs: `docker logs notification-service` or console output
3. Test Gmail connection manually using telnet: `telnet smtp.gmail.com 587`
4. Ensure 2-Step Verification is enabled in your Google Account

---

### Issue: RabbitMQ connection refused

**Possible Causes:**
1. RabbitMQ not running
2. Wrong host/port configuration
3. Network isolation (Docker)

**Solution:**
1. Verify RabbitMQ is running: `docker ps | grep rabbitmq`
2. Check RabbitMQ logs: `docker logs rabbitmq`
3. Access RabbitMQ UI: http://localhost:15672 (admin/admin)
4. If using Docker, ensure containers are on the same network

---

### Issue: Service not registering with Eureka

**Possible Causes:**
1. Eureka Server not running
2. Wrong Eureka URL
3. Network issues

**Solution:**
1. Verify Eureka is running: http://localhost:8761
2. Check service logs for registration errors
3. Ensure `eureka.client.service-url.defaultZone` is correct
4. Wait 30-60 seconds for registration to complete

---

### Issue: Messages not being consumed from RabbitMQ

**Possible Causes:**
1. Queue not created
2. Binding not configured
3. Listener not active

**Solution:**
1. Check RabbitMQ UI for queue existence
2. Verify `@RabbitListener` annotation is present
3. Check application logs for consumer startup messages
4. Publish a test message and monitor logs

---

## 🔮 Future Enhancements

- [ ] **SMS Integration** - Twilio or AWS SNS
- [ ] **Push Notifications** - Firebase Cloud Messaging
- [ ] **Email Templates** - HTML templates with Thymeleaf
- [ ] **Retry Mechanism** - Exponential backoff for failed deliveries
- [ ] **Dead Letter Queue** - Handle permanently failed notifications
- [ ] **Rate Limiting** - Prevent spam and respect provider limits
- [ ] **Notification Preferences** - User opt-in/opt-out management
- [ ] **Batch Processing** - Send multiple notifications efficiently
- [ ] **Analytics Dashboard** - Track delivery rates and failures
- [ ] **Multi-language Support** - Localized templates

---

## 📚 API Reference

### Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/actuator/health` | Health check |
| POST | `/notifications/test` | Send test email |
| POST | `/notifications/send` | Send notification (direct) |
| GET | `/notifications` | Get all notifications |
| GET | `/notifications/{id}` | Get notification by ID |
| GET | `/notifications/recipient/{email}` | Get notifications by recipient |
| GET | `/notifications/status/{status}` | Get notifications by status |

### DTOs

**NotificationEvent** (RabbitMQ payload):
```json
{
  "eventType": "ORDER_CREATED",
  "recipient": "customer@example.com",
  "subject": "Your Order Has Been Created",
  "body": "Thank you for your order...",
  "relatedEntityType": "ORDER",
  "relatedEntityId": "12345"
}
```

**TestEmailRequest** (REST):
```json
{
  "recipient": "test@example.com",
  "name": "Test User"
}
```

---

## 🤝 Contributing

This service is part of the [E-commerce Microservices Ecosystem](https://github.com/DanLearnings/ecommerce-ecosystem). Contributions and suggestions are welcome!

---

## 📄 License

This project is for educational purposes.

---

## 👨‍💻 Author

**Danrley Brasil**
- GitHub: [@DanrleyBrasil](https://github.com/DanrleyBrasil)
- Organization: [DanLearnings](https://github.com/DanLearnings)

---

## 🔗 Related Repositories

- [ecommerce-ecosystem](https://github.com/DanLearnings/ecommerce-ecosystem) - Main hub
- [ecommerce-service-registry](https://github.com/DanLearnings/ecommerce-service-registry) - Eureka Server
- [ecommerce-config-server](https://github.com/DanLearnings/ecommerce-config-server) - Configuration Server
- [ecommerce-api-gateway](https://github.com/DanLearnings/ecommerce-api-gateway) - API Gateway
- [ecommerce-inventory-service](https://github.com/DanLearnings/ecommerce-inventory-service) - Inventory Service
- [ecommerce-config-repo](https://github.com/DanLearnings/ecommerce-config-repo) - Configuration Repository

---

**Last Updated:** November 2025
