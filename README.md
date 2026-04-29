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
