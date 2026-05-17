# Metrics Report 📊

!!!note "Metrics reports available >= 3.2.0" 
    Starting from version `3.2.0`, you can enable error report generation in your project.


The `popcornModuleMetrics` Gradle task analyzes your multi-module project's internal dependency graph and generates a CSV report with three structural metrics for each module: **FanIn**, **FanOut**, and **Instability**.

## Running the Metrics

Run the following command from the project root:

```bash
./gradlew popcornModuleMetrics
```

> **Prerequisite:** The Popcorn Gradle Plugin must be applied with a `type` configured in your root `build.gradle.kts`. The task relies on the same project group name used by `internalProjectDependencies()` to discover inter-module dependencies.

The report is generated at:

```
<buildDir>/reports/metrics/metrics.csv
```

## Report Format

The CSV file uses the following schema:

```
name,fanIn,fanOut,instability
core,2,0,0.0
data,1,1,0.5
presentation,0,2,1.0
```

Each row corresponds to one Gradle subproject in your build.

## Metrics

### FanIn

**Definition:** The number of modules within the project that depend on this module.

**Purpose:** FanIn measures how many other modules rely on a given module. A high FanIn value identifies **core or shared modules** that many parts of the system depend on. Changes to these modules have a broad impact across the project.

### FanOut

**Definition:** The number of internal dependencies this module has — i.e., how many other project modules it depends on.

**Purpose:** FanOut measures how much a module relies on the rest of the project. A high FanOut value indicates a module with many outward connections, making it more coupled to the overall system structure.

### Instability

**Definition:** The ratio of outgoing dependencies to total dependencies (fanIn + fanOut).

```
Instability = FanOut / (FanIn + FanOut)
```

**Range:** `0.0` (maximally stable) to `1.0` (maximally unstable).

- **0.0** — The module has zero outgoing dependencies. It depends on nothing, but other modules depend on it. Changing it is **hard** because of the broad impact.
- **1.0** — The module has zero incoming dependencies. It depends on other modules, but nothing depends on it. Changing it is **easy** because there are no dependents.
- **0.5** — The module has an equal number of incoming and outgoing dependencies.

#### Insights from Instability

Instability helps you understand the architectural role and change resilience of each module:

- **Stable modules (Instability ≈ 0.0)** are foundational layers (e.g., `core`, `domain`, `utils`). They define interfaces, models, or shared logic that the rest of the project builds upon. Because many modules depend on them, modifications require careful planning and thorough testing across the whole project.

- **Unstable modules (Instability ≈ 1.0)** are leaf modules (e.g., `presentation`, `feature-ui`, `app`). They orchestrate behavior by depending on more stable layers below. Since nothing depends on them, they can be changed freely and safely — making them ideal locations for volatile, frequently changing code.

- **Mid-range modules (Instability ~ 0.3–0.7)** deserve closer inspection. A module that both depends on many others and is depended upon by many others may be:
  - Responsible for too many concerns (violating the Single Responsibility Principle)
  - Sitting in the wrong architectural layer
  - Part of an implicit or explicit dependency cycle

- **Dependency cycles** can be detected by examining pairs of modules where both depend on each other. In a healthy layered architecture, instability should decrease as you move from the outer layers toward the core — the Dependency Inversion Principle states that modules in higher (more unstable) layers should depend on abstractions in lower (more stable) layers, not the other way around.

- **The Main Sequence:** In Robert C. Martin's metric system, an ideal module sits close to the main sequence line drawn from `(Instability=0, Abstractness=1)` to `(Instability=1, Abstractness=0)`. Modules far from this line suffer from either "uselessness" (too abstract with no dependents) or "pain" (too concrete with too many dependents). While Popcorn does not currently measure abstractness, you can use instability alone to identify modules that are disproportionately rigid or fragile relative to their role.
