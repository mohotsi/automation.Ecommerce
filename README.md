Here is the complete, high-level README.md merged into a single, professional document. It is structured specifically to showcase your engineering maturity for the Pick n Pay / Cash & Carry role, focusing on CI/CD, parallel execution, and sophisticated state management.

🛒 Enterprise E-commerce Automation Framework
Order Management System (OMS) & Storefront Integration Suite
📝 Overview
This is a professional-grade automation ecosystem designed to validate the end-to-end retail value chain for Pick n Pay. The framework bridges the gap between the customer-facing storefront (Automation Exercise) and the internal Order Management System (OMS).

It is engineered for Jenkins-first execution, emphasizing parallelization, thread-safe state management, and self-healing resilience.

🏗 High-Performance Execution Architecture
1. Advanced Parallelization (TestNG + Cucumber)
The framework is built for maximum throughput. Driven by regression.xml, the suite leverages a multi-threaded data provider to execute scenarios in parallel.

Thread Control: Configured via data-provider-thread-count="2", allowing for linear scaling in CI/CD environments.

Isolated Contexts: Each thread maintains its own Playwright BrowserContext and APIRequestContext.

2. Spring Boot @ScenarioScope Management
To prevent data leakage between parallel threads, I have implemented Cucumber-Spring Scenario Scope:

Isolation: Every Gherkin scenario receives its own fresh instance of automation beans.

Thread Safety: This ensures that "Order A" in Thread 1 never collides with "Order B" in Thread 2, even when interacting with shared services.

3. Resilience: SpecificErrorRetryAnalyzer
In a distributed CI/CD environment, transient network "blips" are handled automatically.

Self-Healing: The framework analyzes failures; if an error is identified as "retriable" (e.g., a timeout on a external UI element), it reruns the scenario automatically to ensure 100% build integrity in Jenkins.

🧪 Testing Scope & Integration
A. Customer Storefront (UI Automation)
Page Object Model (POM): Decoupled architecture for high maintainability.

Dynamic Search & Cart: Validates complex UI logic including guest-to-member transitions and price calculations.

Regional Tagging: Specifically configured for the Vosloorus region using @Regression tags to target branch-specific business rules.

B. Warehouse Operations (Backend API)
JWT Security Handshake: Automatically authenticates via /api/oms/login to retrieve and inject Bearer tokens into secured headers.

Functional Lifecycle Streams: Uses Java Streams to drive orders from PICKING → PACKING → SHIPPING → DELIVERED.

Dual-Layer Verification: Asserts both the API response messages and the database integrity via the SearchServiceAPI.

🚀 DevOps & CI/CD Instructions
Jenkins Pipeline Execution
To trigger the full regression suite in a headless environment, use the following Maven command:

Bash
mvn clean test -DsuiteXmlFile=regression.xml -Dheadless=true
The Project Structure
Plaintext
src/
├── main/                 # OMS Application (Spring Boot)
│   └── java/.../security # JWT, BCrypt, and Filter logic
└── test/                 # Automation Engineering
    ├── java/.../runner   # FullRegression.java (Entry Point)
    ├── java/.../service  # Thread-safe TaskAutomation & SearchAPI
    └── resources/        # Gherkin .feature files (Vosloorus Region)
📊 Automation Engineering Highlights
ThreadLocal Sessions: Secure management of multi-user JWT tokens across parallel threads.

State Persistence: Direct validation against the H2 In-Memory Database to verify persistence after API updates.

Automated Notifications: Verification of the handleStatusNotifications logic (SMTP/Email triggers) during the Shipping/Delivery phases.

QA Philosophy: "Automating the UI tests the surface; automating the lifecycle tests the business." This framework is designed to provide Pick n Pay with the confidence to deploy rapidly, knowing the core logic is protected by a resilient, high-speed quality gat
