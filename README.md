# Reactive Ticket Booking System

Este es un sistema reactivo de reserva de tickets construido con **Java 25**, **Spring Boot 4 (WebFlux)** y servicios de AWS (**DynamoDB**, **SQS**). La solución está diseñada siguiendo principios de **Arquitectura Limpia (Clean Architecture)** basada en el patrón **Hexagonal** y **Domain-Driven Design (DDD)**.

---

## 🚀 Características
- **Arquitectura Limpia**: Separación clara entre dominio, aplicación e infraestructura.
- **Programación Reactiva**: Uso de Project Reactor para un manejo eficiente de recursos y alta concurrencia.
- **Resiliencia**: Integración asíncrona mediante colas SQS para el procesamiento de compras.
- **Persistencia NoSQL**: Uso de DynamoDB para escalabilidad.
- **Entorno Local**: Configuración completa con Docker y LocalStack.

---

## 🛠️ Stack Tecnológico
- **Lenguaje**: Java 25
- **Framework**: Spring Boot 4.0.2 (WebFlux)
- **Base de Datos**: AWS DynamoDB
- **Mensajería**: AWS SQS
- **Infraestructura**: Docker & LocalStack
- **Pruebas**: JUnit 5, Mockito, StepVerifier

---

## ⚙️ Instalación y Configuración

### Prerrequisitos
- **Java 25** o superior.
- **Maven 3.9+**.
- **Docker** y **Docker Compose**.

### Configuración Local

1. **Clonar el repositorio**:
   ```bash
   git clone https://github.com/julianjjo/event_tickets.git
   cd event_tickets
   ```

2. **Ejecutar todo con Docker**:
   Este comando construye la aplicación y levanta todos los servicios (App, LocalStack).
   ```bash
   docker compose up --build -d
   ```
   *Esto levantará la aplicación en `http://localhost:8080` y LocalStack para emular DynamoDB y SQS.*

3. **Alternativa: Desarrollo Local (Hybrid)**:
   Si prefieres ejecutar solo los servicios de AWS en Docker y la aplicación localmente:
   ```bash
   # Paso 1: Levantar servicios (LocalStack)
   docker compose up localstack -d

   # Paso 2: Ejecutar la aplicación
   ./mvnw spring-boot:run
   ```

---

## 🏗️ Decisiones Arquitectónicas

- **Arquitectura Limpia (Hexagonal)**: Se implementa una separación clara de responsabilidades mediante el patrón de Puertos y Adaptadores, permitiendo desacoplar la lógica de negocio de las tecnologías externas (bases de datos, frameworks, brokers de mensajería).
- **Patrón Use Case**: La lógica de aplicación se encapsula en casos de uso específicos (`ConfirmPurchaseUseCase`, `ReserveSeatUseCase`, etc.), siguiendo las reglas de dependencia de Clean Architecture.
- **Procesamiento Asíncrono**: Las reservas se confirman y se envían a una cola SQS. Un consumidor procesa estas compras de forma asíncrona para no bloquear el hilo de respuesta del usuario.
- **Manejo de Errores Global**: Implementado mediante `@RestControllerAdvice` para estandarizar las respuestas de error en formato JSON.

---

## 📂 Estructura del Proyecto
```text
src/main/java/com/nequi/ticket/booking/
├── application/         # Casos de uso (Lógica de aplicación)
├── domain/              # Modelos, Excepciones y Puertos (Lógica de negocio)
├── infrastructure/      # Adaptadores, Configuración y Entrypoints (Detalles técnicos)
│   ├── adapter/         # Implementaciones de persistencia y mensajería
│   ├── config/          # Configuración de AWS y Beans
│   └── entrypoint/      # Controladores REST y DTOs
└── BookingSystemApplication.java
```

---

## 🔌 API Endpoints

### Eventos
| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `/api/events` | Lista todos los eventos disponibles. |
| `POST` | `/api/events` | Crea un nuevo evento. |

**Ejemplo Crear Evento:**
```json
POST /api/events
{
    "name": "Concierto de Rock",
    "date": "2026-12-31T20:00:00",
    "location": "Estadio Nacional",
    "totalTickets": 100,
    "availableTickets": 100
}
```

### Reservas y Compras
| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `POST` | `/api/purchases/reserve` | Reserva asientos para un evento. |
| `POST` | `/api/purchases/confirm/{orderId}` | Confirma una reserva previa. |

**Ejemplo Reservar:**
```json
POST /api/purchases/reserve
{
    "eventId": "UUID-DEL-EVENTO",
    "userId": "user_123",
    "quantity": 2
}
```

---

## 🧪 Pruebas
Para ejecutar los tests unitarios y de integración:
```bash
mvn test
```

---

## 📄 Colección Postman
Se incluye el archivo `postman_collection.json` en la raíz para facilitar las pruebas de los endpoints.
