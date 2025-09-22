# Telco Customer Service Chatbot

Scalable chatbot using Temporal workflows for session persistence and Google Gemini for natural language processing.

## Features

- Customer service scenarios: mobile devices, stock, subscriptions, consents, account details
- Temporal workflow orchestration for session management
- GitHub OAuth authentication
- WebSocket real-time communication
- Scenario-based modular design

## Quick Start

### Prerequisites
- Java 21+
- Docker & Docker Compose

### Setup

1. **Environment variables:**
```bash
export GEMINI_DEV_KEY="your-gemini-api-key"
export GITHUB_SECRET_KEY="your-github-client-secret"
```

2. **Start services:**
```bash
docker compose up -d
./gradlew quarkusDev
```

3. **Access:** http://localhost:8080

### Configuration

**Gemini API Key:** Get from [Google AI Studio](https://aistudio.google.com/app/apikey)

**GitHub OAuth:** Create app at [GitHub Developer Settings](https://github.com/settings/developers)
- Homepage URL: `http://localhost:8080`
- Callback URL: `http://localhost:8080/login`

## Architecture

Built with:
- **Quarkus** - Application framework
- **Temporal** - Workflow orchestration and session persistence
- **Google Gemini** - Language model
- **WebSocket** - Real-time communication
- **WireMock** - API mocking

### Why Temporal?

- **Session persistence** - Conversation history survives restarts
- **Stateless scaling** - Any pod can handle any user
- **Fault tolerance** - Automatic retries and state recovery
- **Observability** - Complete execution history in Temporal Web UI

## Development

### Project Structure
```
src/
├── main/java/com/inorganic/
│   ├── ui/                    # WebSocket endpoints
│   ├── workflows/             # Temporal workflows & activities
│   │   ├── activities/        # Business logic implementations
│   │   └── ChatbotService.java # Main orchestration service
│   └── tools/                 # External API integrations
├── main/resources/
│   ├── META-INF/resources/    # Frontend assets
│   ├── wiremock/              # Mock API responses
│   └── application.properties # Configuration
```