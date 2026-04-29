# 🛒 E-Commerce Automation Framework: The Virtual Workforce Guide

## 🌟 Overview: Beyond "Simple Scripts"
This framework is not just a collection of tests; it is a **simulated workforce**. In a modern retail environment dealing with complex Order Management Systems (OMS) and high-traffic environments manual testing cannot keep up with the pace of deployment. 

This framework utilizes **Parallel Execution** and **Spring Boot Dependency Injection** to deploy an army of "Virtual Testers" who can validate the entire checkout journey in minutes instead of days.

---

## 🧠 The Analogy: How It Works for Retail Stakeholders

To explain this to the wider e-commerce team, use the **Virtual Tester** comparison:

*   **The Thread (The Virtual Tester):** Each thread is a distinct "tester" who has their own computer and browser. If we run 10 threads, it’s exactly like hiring 10 people to sit in a room and test the site simultaneously.
*   **Scenario Scope (The Private Desk):** In a physical store, you wouldn't want two customers sharing one shopping cart. We use `@ScenarioScope` to ensure every virtual tester has their own private "desk" (browser, cookies, and cache) that remains completely invisible to other testers.
*   **The Identity Pool (The Tester's Wallet):** Every tester is handed a specific "Customer Profile" (username, password, etc.). We use a pool to ensure no two testers try to log into the same account at the same time, preventing session collisions.

---
## 🏗️ Technical Architecture: Start-to-End Flow

### 1. The Briefing (Configuration Phase)
The framework starts by initializing the environment using Spring Boot to define how the browser should behave for *every* individual tester. We use the `@ScenarioScope` annotation to act as a "Privacy Wall".
```java
// Logic from PlayWrightBrowserConfig.java
@Bean
@ScenarioScope // Ensures every virtual tester has their own isolated session
public BrowserContext browserContext(Browser browser) {
    return browser.newContext(new Browser.NewContextOptions()
            .setViewportSize(1920, 1080)
            .setRecordVideoDir(Paths.get("target/videos/"))); 
}
// Conceptual flow of the CustomerIdentityPool management
public Customer claimCustomer() {
    return customerList.stream()
        .filter(c -> !c.isBusy()) // Find a tester who is currently "available"
        .findFirst()
        .map(c -> { 
            c.setBusy(true); 
            return c; 
        })
        .orElseThrow(() -> new RuntimeException("No available Virtual Testers in the pool!"));
}
// Snippet from Hooks.java
@After
public void teardown(Scenario scenario) {
    if (scenario.isFailed()) {
        // Capture exactly what the tester saw at the moment of failure
        byte[] screenshot = page.screenshot();
        scenario.attach(screenshot, "image/png", "Failure_Screen");
    }
    // The Virtual Tester releases their customer ID back to the pool
    customerIdentityPool.release(currentCustomer);
    browserContext.close();
}
```
---

### Block 3: Local Setup & Cloud Pipelines


```markdown
## 💻 Local Setup: Preparing Your Workstation (Windows)

To run these "Virtual Testers" on your local machine, you need to configure the Java and Maven engines correctly.

### 1. Environment Variables
Windows needs to know where your "engines" are located to run commands from the terminal.
1.  Search for **"Environment Variables"** in your Start Menu and select **Edit the system environment variables**.
2.  Add a **New System Variable**:
    *   **Variable Name:** `JAVA_HOME` | **Value:** `C:\Program Files\Java\jdk-17`
    *   **Variable Name:** `MAVEN_HOME` | **Value:** `C:\apache-maven-3.x.x`
3.  Edit the **Path** variable in the same menu and add these two entries:
    *   `%JAVA_HOME%\bin`
    *   `%MAVEN_HOME%\bin`

### 2. Running in IntelliJ IDEA
1.  **Open Project:** Select the `pom.xml` file to import the project as a Maven project.
2.  **SDK Setup:** Go to `File > Project Structure` and ensure the Project SDK is set to **Java 17**.
3.  **Run Configurations:** To target a specific environment (like D1 or QA), add this to the **VM Options**:
    *   `-Dspring.profiles.active=D1`

---

## 🚀 Continuous Integration: Running at Scale

### Jenkins Pipeline
The "Foreman" (Jenkins) coordinates the workforce using a script like this:
```groovy
pipeline {
    agent any
    stages {
        stage('Execute Workforce') {
            steps {
                // Runs 5 virtual testers in parallel on a Windows node
                bat 'mvn clean test -Dspring.profiles.active=QA -DthreadCount=5'
            }
        }
    }
}
```
## Azure DevOps
Azure allows us to spin up temporary cloud machines to run our testers:
```
pool:
  vmImage: 'windows-latest'

steps:
- task: Maven@3
  inputs:
    mavenPomFile: 'pom.xml'
    goals: 'test'
    options: '-Dspring.profiles.active=release -DthreadCount=3 -Dbrowser=chrome'
```
---

### Block 4: Project Map & Stakeholder Summary


```markdown
## 📁 Project Structure: Finding Your Way Around

*   **`src/test/resources/features/`**: 📖 **The Stories.** The human-readable customer journeys.
*   **`src/main/java/pages/`**: ⚙️ **The Action Logic.** Technical instructions for website elements.
*   **`src/main/java/config/`**: 🧠 **The Brain.** Multi-tenant architecture and environment settings.
*   **`src/test/java/hooks/`**: ⚓ **The Setup/Cleanup.** Management of the identity pool.

---

## 📊 Stakeholder Quick-Reference

| Technical Term | Retail Analogy | Business Value |
| :--- | :--- | :--- |
| **Threads** | Virtual Testers | Test 100 shopping paths in the time it takes to test 1. |
| **ScenarioScope** | Private Shopping | Ensures one customer's data never leaks to another. |
| **Identity Pool** | Shared Login Roster | Ensures every tester has a valid, unique account to use. |
| **Traces** | CCTV Evidence | Provides visual proof of exactly why a checkout failed. |

---

## 💰 Quota Savings & Performance Efficiency

In a large-scale retail environment, automation can become expensive if not optimized. This framework includes specialized logic to minimize costs and maximize speed.

### 1. SMS & OTP Quota Protection
Many retail platforms use SMS OTP for login. Running hundreds of tests daily can quickly exhaust SMS budgets. 
* **The Solution:** We use Playwright’s `storageState` to capture the authenticated browser state (cookies and local storage) after the first successful login.
* **The Result:** Subsequent virtual testers "resume" the session, bypassing the login screen and the need for a new OTP.

### 2. Google Maps & Regional API Savings
Loading location pickers or Google Maps APIs on every test execution incurs significant API costs.
* **The Solution:** By saving the "Region" or "Preferred Store" in the browser cookies, the virtual tester starts the journey with the region already selected.
* **The Result:** This avoids repeated calls to Google APIs and reduces the number of network requests per scenario.

### 3. Execution Time Efficiency (Parallelism)
Time is the most valuable quota in a Development (D1) or OMS environment.
* **Persistent Login:** Saving 15–30 seconds per login across 100 tests saves nearly an hour of total execution time.
* **Headless Execution:** By default, the workforce runs in "Headless" mode (no UI), which consumes significantly less CPU and RAM on Jenkins/Azure nodes, allowing for higher thread counts on the same hardware.

### 4. Code Implementation Snippet
This logic is integrated into the `BrowserContext` configuration to ensure it is handled automatically:
```java
// Saving and Loading the Browser State to save quotas

public BrowserContext createPersistentContext(Browser browser) {
    return browser.newContext(new Browser.NewContextOptions()
            .setStorageStatePath(Paths.get("config/cookies/customer...json")) // Saves login/cookies
            .setGeolocation(new Geolocation(-34.11, 18.82)) // Static region to save API calls
            .setPermissions(Arrays.asList("geolocation")));
}
```
### Why this section matters for your project:
* **Cost Reduction:** It proves to management that your automation is "budget-aware" by not spamming OTP services.
* **Speed:** It targets the **D1 (Development)** and **OMS (Order Management System)** environments specifically, where fast feedback is critical for the engineering team. 
* **Stability:** Bypassing the login flow reduces "flaky" tests caused by third-party SMS providers failing to deliver codes on time.
---
*Maintained by Thapelo Daniel Mohotsi - QA Engineering*



```

``
