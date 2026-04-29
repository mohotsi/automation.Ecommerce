# 🛒 E-Commerce Automation Framework: The Virtual Workforce Guide

## 🌟 Overview: Beyond "Simple Scripts"
This framework is not just a collection of tests; it is a **simulated workforce**. In a modern retail environment dealing with complex Order Management Systems (OMS) and high-traffic environments manual testing cannot keep up with the pace of deployment. 

This framework utilizes **Parallel Execution** and **Spring Boot Dependency Injection** to deploy an army of "Virtual Testers" who can validate the entire checkout journey in minutes instead of days.

---

## 🧠 The Analogy: How It Works for Retail Stakeholders

To explain this to the wider e-commerce team, use the **Virtual Tester** comparison:

*   **The Thread (The Virtual Tester):** Each thread is a distinct "tester" who has their own computer and browser. If we run 10 threads, it’s exactly like hiring 10 people to sit in a room and test the site simultaneously[cite: 1].
*   **Scenario Scope (The Private Desk):** In a physical store, you wouldn't want two customers sharing one shopping cart[cite: 1]. We use `@ScenarioScope` to ensure every virtual tester has their own private "desk" (browser, cookies, and cache) that remains completely invisible to other testers[cite: 1].
*   **The Identity Pool (The Tester's Wallet):** Every tester is handed a specific "Customer Profile" (username, password, etc.)[cite: 1]. We use a pool to ensure no two testers try to log into the same account at the same time—preventing session collisions[cite: 1].

---
## 🏗️ Technical Architecture: Start-to-End Flow

### 1. The Briefing (Configuration Phase)
The framework starts by initializing the environment using Spring Boot to define how the browser should behave for *every* individual tester[cite: 1]. We use the `@ScenarioScope` annotation to act as a "Privacy Wall"[cite: 1].
```java
// Logic from PlayWrightBrowserConfig.java[cite: 1]
@Bean
@ScenarioScope // Ensures every virtual tester has their own isolated session[cite: 1]
public BrowserContext browserContext(Browser browser) {
    return browser.newContext(new Browser.NewContextOptions()
            .setViewportSize(1920, 1080)
            .setRecordVideoDir(Paths.get("target/videos/"))); 
}
// Conceptual flow of the CustomerIdentityPool management[cite: 1]
public Customer claimCustomer() {
    return customerList.stream()
        .filter(c -> !c.isBusy()) // Find a tester who is currently "available"[cite: 1]
        .findFirst()
        .map(c -> { 
            c.setBusy(true); 
            return c; 
        })
        .orElseThrow(() -> new RuntimeException("No available Virtual Testers in the pool!"));
}
// Snippet from Hooks.java[cite: 1]
@After
public void teardown(Scenario scenario) {
    if (scenario.isFailed()) {
        // Capture exactly what the tester saw at the moment of failure[cite: 1]
        byte[] screenshot = page.screenshot();
        scenario.attach(screenshot, "image/png", "Failure_Screen");
    }
    // The Virtual Tester releases their customer ID back to the pool[cite: 1]
    customerIdentityPool.release(currentCustomer);
    browserContext.close();
}
```
---

### Block 3: Local Setup & Cloud Pipelines


```markdown
## 💻 Local Setup: Preparing Your Workstation (Windows)

To run these "Virtual Testers" on your local machine, you need to configure the Java and Maven engines correctly[cite: 1].

### 1. Environment Variables
Windows needs to know where your "engines" are located to run commands from the terminal[cite: 1].
1.  Search for **"Environment Variables"** in your Start Menu and select **Edit the system environment variables**[cite: 1].
2.  Add a **New System Variable**[cite: 1]:
    *   **Variable Name:** `JAVA_HOME` | **Value:** `C:\Program Files\Java\jdk-17`[cite: 1]
    *   **Variable Name:** `MAVEN_HOME` | **Value:** `C:\apache-maven-3.x.x`[cite: 1]
3.  Edit the **Path** variable in the same menu and add these two entries[cite: 1]:
    *   `%JAVA_HOME%\bin`[cite: 1]
    *   `%MAVEN_HOME%\bin`[cite: 1]

### 2. Running in IntelliJ IDEA
1.  **Open Project:** Select the `pom.xml` file to import the project as a Maven project[cite: 1].
2.  **SDK Setup:** Go to `File > Project Structure` and ensure the Project SDK is set to **Java 17**[cite: 1].
3.  **Run Configurations:** To target a specific environment (like D1 or QA), add this to the **VM Options**[cite: 1]:
    *   `-Dspring.profiles.active=D1`[cite: 1]

---

## 🚀 Continuous Integration: Running at Scale

### Jenkins Pipeline
The "Foreman" (Jenkins) coordinates the workforce using a script like this[cite: 1]:
```groovy
pipeline {
    agent any
    stages {
        stage('Execute Workforce') {
            steps {
                // Runs 5 virtual testers in parallel on a Windows node[cite: 1]
                bat 'mvn clean test -Dspring.profiles.active=QA -DthreadCount=5'
            }
        }
    }
}
```
## Azure DevOps
Azure allows us to spin up temporary cloud machines to run our testers[cite: 1]:
```
pool:
  vmImage: 'windows-latest'

steps:
- task: Maven@3
  inputs:
    mavenPomFile: 'pom.xml'
    goals: 'test'
    options: '-Dspring.profiles.active=release -DthreadCount=3 -Dbrowser=chrome'[cite: 1]
```
---

### Block 4: Project Map & Stakeholder Summary


```markdown
## 📁 Project Structure: Finding Your Way Around

*   **`src/test/resources/features/`**: 📖 **The Stories.** The human-readable customer journeys[cite: 1].
*   **`src/main/java/pages/`**: ⚙️ **The Action Logic.** Technical instructions for website elements[cite: 1].
*   **`src/main/java/config/`**: 🧠 **The Brain.** Multi-tenant architecture and environment settings[cite: 1].
*   **`src/test/java/hooks/`**: ⚓ **The Setup/Cleanup.** Management of the identity pool[cite: 1].

---

## 📊 Stakeholder Quick-Reference

| Technical Term | Retail Analogy | Business Value |
| :--- | :--- | :--- |
| **Threads** | Virtual Testers | Test 100 shopping paths in the time it takes to test 1[cite: 1]. |
| **ScenarioScope** | Private Shopping | Ensures one customer's data never leaks to another[cite: 1]. |
| **Identity Pool** | Shared Login Roster | Ensures every tester has a valid, unique account to use[cite: 1]. |
| **Traces** | CCTV Evidence | Provides visual proof of exactly why a checkout failed[cite: 1]. |

---
*Maintained by Thapelo Daniel Mohotsi - QA Engineering*



```

``
