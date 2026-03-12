# Spring Boot WebSocket Chat

A simple real-time chat application built with Spring Boot and STOMP over WebSocket (with SockJS fallback). It includes a minimal front-end served from `src/main/resources/static` and a backend that broadcasts messages to all connected users.

## Features
- Real-time messaging with STOMP over WebSocket
- Automatic JOIN/LEAVE notifications
- SockJS fallback for broader browser compatibility
- Zero external dependencies for the broker (uses Spring’s simple in-memory broker)

## Tech Stack
- Java 25
- Spring Boot 4.0.3
  - spring-boot-starter-webmvc
  - spring-boot-starter-websocket
- Lombok (compile-time)
- Front-end: Vanilla HTML/CSS/JS + SockJS + STOMP.js

## How it works
- STOMP endpoint: `/ws` (SockJS enabled)
- Application destination prefix: `/app`
- Simple broker destinations: `/topic`
- Public topic used by the app: `/topic/public`
- Message mappings:
  - `@MessageMapping("chat.sendMessage")` → broadcast to `/topic/public`
  - `@MessageMapping("chat.addUser")` → store username in session; broadcast JOIN event to `/topic/public`
- Message payload (`ChatMessages` record):
  ```json
  {
    "content": "Hello world!",
    "sender": "alice",
    "type": "CHAT"
  }
  ```
  Types: `CHAT`, `JOIN`, `LEAVE`.

Key files:
- Backend
  - `src/main/java/com/example/chat/config/WebSocketConfig.java` — STOMP/WebSocket config
  - `src/main/java/com/example/chat/chat/ChatController.java` — message handlers
  - `src/main/java/com/example/chat/config/WebSocketEventListener.java` — disconnect listener (LEAVE event)
  - `src/main/java/com/example/chat/chat/ChatMessages.java` — message DTO
  - `src/main/java/com/example/chat/chat/MessageType.java` — enum for CHAT/JOIN/LEAVE
- Frontend
  - `src/main/resources/static/index.html` — UI served by Spring Boot
  - `src/main/resources/static/js/main.js` — STOMP client logic
  - `src/main/resources/static/css/main.css` — styles

## Getting started

### Prerequisites
- Java 25 (as configured in `pom.xml`)
- Maven Wrapper is included; no need to install Maven

### Run the app
1. Clone this repository
2. From the project root, start the application:
   ```bash
   ./mvnw spring-boot:run
   # On Windows
   mvnw.cmd spring-boot:run
   ```
3. Open your browser at:
   - http://localhost:8080
4. Open the page in two or more tabs/browsers, enter different usernames, and start chatting.

### Build a jar
```bash
./mvnw clean package
```
The jar will be in `target/`. Run it with:
```bash
java -jar target/chat-0.0.1-SNAPSHOT.jar
```

### Run tests
```bash
./mvnw test
```

## Configuration
- Application name: set in `src/main/resources/application.properties` via `spring.application.name=chat`.
- Change server port by adding (or exporting) the following:
  - In `application.properties`:
    ```properties
    server.port=8081
    ```
  - Or via environment variable:
    ```bash
    SERVER_PORT=8081 ./mvnw spring-boot:run
    ```

## API details (for custom clients)
- Connect SockJS/STOMP to `/ws`
- Subscribe to `/topic/public`
- Send messages to application destinations:
  - `/app/chat.addUser` with payload: `{ "sender": "alice", "type": "JOIN" }`
  - `/app/chat.sendMessage` with payload: `{ "content": "hi", "sender": "alice", "type": "CHAT" }`
- Server broadcasts to `/topic/public` with the same payload model. On disconnect, a `LEAVE` message is sent by the backend listener.

## Project structure
```
chat/
├─ pom.xml
├─ src/
│  ├─ main/
│  │  ├─ java/com/example/chat/
│  │  │  ├─ ChatApplication.java
│  │  │  ├─ chat/
│  │  │  │  ├─ ChatController.java
│  │  │  │  ├─ ChatMessages.java
│  │  │  │  └─ MessageType.java
│  │  │  └─ config/
│  │  │     ├─ WebSocketConfig.java
│  │  │     └─ WebSocketEventListener.java
│  │  └─ resources/
│  │     ├─ application.properties
│  │     └─ static/
│  │        ├─ index.html
│  │        ├─ css/main.css
│  │        └─ js/main.js
│  └─ test/java/com/example/chat/ChatApplicationTests.java
└─ README.md
```

## Troubleshooting
- Cannot connect to WebSocket:
  - Check that the app is running on `http://localhost:8080`
  - Verify browser console for network errors (SockJS/STOMP)
  - Ensure no corporate proxy/firewall is blocking WebSocket
- Messages not appearing:
  - Open multiple tabs and ensure each user has a unique username
  - Watch server logs for JOIN/LEAVE events

## License
This project is provided as-is without a specific license. Add your preferred license file if needed.
