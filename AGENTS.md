# Agents of Gamer Collection

This document describes the main agents and their responsibilities within the Gamer Collection project, following a Clean Architecture approach.

## 1. Presentation Agent (UI Layer)
- **Responsibility**: Manages user interaction and data visualization.
- **Key Components**:
    - **Activities & Fragments**: Handle the UI lifecycle and navigation.
    - **ViewModels**: Maintain the UI state and communicate with the Domain layer using LiveData.
    - **Binding**: Uses both ViewBinding and DataBinding for efficient UI updates.
- **Location**: `app/src/main/java/es/upsa/mimo/gamercollection/presentation/`

## 2. Domain Agent (Business Logic Layer)
- **Responsibility**: Contains the core business logic and definitions that are independent of any framework.
- **Key Components**:
    - **Models**: Core entities like `Game`, `Saga`, `Song`, and `User`.
    - **Repository Interfaces**: Define the contracts for data operations.
    - **Mappers**: Ensure data transformation between different layers.
- **Location**: `app/src/main/java/es/upsa/mimo/gamercollection/domain/`

## 3. Data Agent (Data Source Layer)
- **Responsibility**: Orchestrates data flow from various sources (Local and Remote).
- **Key Components**:
    - **Repositories**: Implementation of domain interfaces.
    - **Local Source**: Powered by **Room** for persistent storage of the videogame collection.
    - **Remote Source**: Powered by **Retrofit** for external game searches (using RAWG API).
    - **Firebase**: Integration for Analytics, Crashlytics, and Remote Config.
- **Location**: `app/src/main/java/es/upsa/mimo/gamercollection/data/`

## 4. Infrastructure Agent (Cross-cutting)
- **Responsibility**: Provides tools and utilities that support all other agents.
- **Key Components**:
    - **Dependency Injection**: Managed by **Hilt** to decouple components.
    - **Asynchronous Operations**: Managed by **Kotlin Coroutines**.
    - **Image Loading**: Handled by **Picasso**.
    - **Security**: Encrypted storage for sensitive data like user credentials.
- **Location**: Managed via `di` packages and `utils`/`extensions`.

---
*This file summarizes the project's architectural agents to facilitate onboarding and maintenance.*
