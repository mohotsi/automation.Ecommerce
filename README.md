# 🛒 E-Commerce Automation Framework: The Virtual Tester Workforce

## 🌟 Executive Summary
This framework is designed to simulate a high-performance retail environment. Instead of relying on manual testing, we deploy an army of **Virtual Testers (Threads)** that act simultaneously. This allows us to test complex customer journeys—like multi-tenant checkouts and order management—at a scale and speed impossible for humans.

---

## 🧠 Core Concept: The "Virtual Tester" Analogy
To help our retail and business stakeholders understand the technical power of this project:

* **The Thread = The Individual Tester:** Each thread is an isolated worker with their own browser and computer.
* **Scenario Scope = The Private Desk:** Using `@ScenarioScope`, we ensure every tester has their own private "shopping cart" and session. No tester's data ever leaks into another's.
* **The Identity Pool = Loyalty Cards:** We manage a pool of real customer data. When a tester starts, they "claim" a customer identity so they don't try to log into the same account as another tester.

---

## 🛠 Technical Flow: From Start to End

### 1. The Briefing (Configuration & Dependency Injection)
The framework starts by preparing the "store environment." We use Spring Boot to manage our tools. The following snippet shows how we isolate each tester's browser:

```java
// Logic from PlayWrightBrowserConfig.java
@Bean
@ScenarioScope // The "Privacy Wall" for each Virtual Tester
public BrowserContext browserContext(Browser browser) {
    return browser.newContext(new Browser.NewContextOptions()
            .setViewportSize(1920, 1080)
            .setRecordVideoDir(Paths.get("target/videos/"))); 
}
## 2. Identity Assignment (The Customer Pool)
Before a tester visits the site, they must have an identity. The CustomerIdentityPool ensures that in a parallel execution environment, each thread uses a unique customer.

// Snippet of how we manage unique testers
public Customer getAvailableCustomer() {
    return customerList.stream()
        .filter(customer -> !customer.isCurrentlyInUse())
        .findFirst()
        .orElseThrow(() -> new RuntimeException("All Virtual Testers are currently busy!"));
}
3. The Journey (Execution)
The testers follow Gherkin "Stories" (e.g., As a customer, I want to add a product to my cart). The code translates these into clicks.

4. Clocking Out (Reporting & Traces)
If a test fails, the tester doesn't just stop. They leave behind a Playwright Trace—a frame-by-frame recording of the failure for the developers to debug.

💻 Local Developer Setup (Windows)
To run these "Virtual Testers" on your local machine, follow these environment configurations.

1. Environment Variables
You must tell Windows where your "engines" are located.

Open Edit the system environment variables.

Add New System Variables:

JAVA_HOME: C:\Program Files\Java\jdk-17 (Ensure it is JDK 17 or higher)

MAVEN_HOME: C:\Program Files\apache-maven-3.x.x

Edit the Path variable and add:

%JAVA_HOME%\bin

%MAVEN_HOME%\bin

2. IntelliJ IDEA Setup
Import: Open IntelliJ and select the pom.xml file to import the project.

SDK: Go to File > Project Structure and ensure the Project SDK is set to 17.

Run Profiles: To run tests against a specific environment (like D1), add this to your VM Options in the Run Configuration:

-Dspring.profiles.active=D1
🚀 Continuous Integration (CI/CD)
Jenkins Pipeline
Our Jenkins "Foreman" coordinates the workforce using the following pipeline script:

pipeline {
    agent { label 'windows-agent' }
    stages {
        stage('Deploy Virtual Workforce') {
            steps {
                // Runs 5 testers in parallel
                bat 'mvn clean test -Dspring.profiles.active=QA -DthreadCount=5'
            }
        }
    }
    post {
        always {
            publishHTML(target: [reportDir: 'target/cucumber-reports', reportFiles: 'index.html', reportName: 'Retail Execution Report'])
        }
    }
}
pipeline {
    agent { label 'windows-agent' }
    stages {
        stage('Deploy Virtual Workforce') {
            steps {
                // Runs 5 testers in parallel
                bat 'mvn clean test -Dspring.profiles.active=QA -DthreadCount=5'
            }
        }
    }
    post {
        always {
            publishHTML(target: [reportDir: 'target/cucumber-reports', reportFiles: 'index.html', reportName: 'Retail Execution Report'])
        }
    }
}
pool:
  vmImage: 'windows-latest'

steps:
- task: Maven@3
  displayName: 'Run E-Commerce Regression'
  inputs:
    mavenPomFile: 'pom.xml'
    goals: 'test'
    # Switches to the Production or QA profile based on the release
    options: '-Dspring.profiles.active=release -Dbrowser=chrome'
Feature,Technical Term,Retail Benefit
Parallelism,Threads,Simulates hundreds of users shopping at once.
Multi-Tenancy,Spring Profiles,"One framework tests multiple store brands (Shoprite, Checkers, etc.)."
Persistence,ScenarioScope,"Ensures ""Customer A"" never sees ""Customer B's"" personal data."
Debugging,Playwright Traces,"Provides ""CCTV footage"" of any error on the site."
