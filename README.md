# monolith-war

A proof-of-concept monolith: an **Angular** frontend and a **Spring Boot** backend, packaged as **one executable WAR** that runs on Spring Boot's embedded Tomcat. One server on one port serves both the UI and the REST API.

```
java -jar backend.war  →  http://localhost:8080
                           ├── /          Angular UI
                           └── /api/...   Spring Boot REST API
```

## Tech stack

| Part | Version |
|---|---|
| Java | 17 |
| Spring Boot | 4.2.0 (WAR packaging, embedded Tomcat) |
| Angular | 15.2 |
| Node.js | 14.21.3 (downloaded automatically by the Maven build) |
| Build | Maven multi-module + `frontend-maven-plugin` |

## Project structure

```
monolith-war/
├── pom.xml          ← parent POM (modules: frontend, backend)
├── mvnw, mvnw.cmd   ← Maven wrapper (no Maven install needed)
├── frontend/        ← Angular app, built into a jar containing /static
│   ├── pom.xml
│   ├── angular.json ← outputPath: target/classes/static
│   └── proxy.conf.json
└── backend/         ← Spring Boot app, packaged as an executable WAR
    ├── pom.xml      ← depends on the frontend module
    └── src/main/java/com/example/backend/
        ├── HelloController.java      ← GET /api/hello
        └── SpaForwardController.java ← sends Angular routes to index.html
```

Maven builds `frontend` first (installs Node, runs `npm ci` and `npm run build`), packages the Angular files as a jar, and `backend` includes that jar in the WAR.

## Prerequisites

- **Java 17** (`java -version`)
- **Git**
- For dev mode only: **Node.js 14.21.3** and **Angular CLI 15** (`npm install -g @angular/cli@15`)

You don't need to install Maven: use the included wrapper.

## Build

From the project root:

```bash
# Windows
.\mvnw clean package

# macOS / Linux
./mvnw clean package
```

Output: `backend/target/backend-0.0.1-SNAPSHOT.war`

The first build downloads Node and npm packages, so it takes a few minutes.

## Run (production mode, one server)

```bash
java -jar backend/target/backend-0.0.1-SNAPSHOT.war
```

Then open:

| URL | Expected |
|---|---|
| http://localhost:8080 | Angular page showing "Hello from Spring Boot" |
| http://localhost:8080/about | About page (refreshing it also works) |
| http://localhost:8080/api/hello | `{"message":"Hello from Spring Boot"}` |

Stop with `Ctrl+C`.

### Run from IntelliJ IDEA

1. **File → Open**, select the root `monolith-war` folder, and trust the project.
2. **Run → Edit Configurations → + → JAR Application**:
   - Path to JAR: `<project>/backend/target/backend-0.0.1-SNAPSHOT.war`
   - JRE: 17
   - Before launch: **+ → Run Maven Goal** → `clean package` (on the root project)
3. Run it with ▶.

## Dev mode (hot reload, two servers)

Use this while coding. Angular changes show up in the browser immediately.

```bash
# once, from the root
./mvnw clean install

# terminal 1: backend on :8080
./mvnw -pl backend spring-boot:run
#   (or run BackendApplication in IntelliJ)

# terminal 2: frontend on :4200
cd frontend
npm install
npm start
```

Open http://localhost:4200. `/api` calls are proxied to `:8080` through `frontend/proxy.conf.json`.

## Troubleshooting

| Problem | Fix |
|---|---|
| Blank page or 404 at `/` | Check that `frontend/target/classes/static/index.html` exists after the build. |
| `Could not find artifact com.example:frontend` | Run Maven from the **root** folder, not from `backend/`. |
| Refreshing `/about` gives 404 | `SpaForwardController` must be in package `com.example.backend`. |
| IntelliJ: `jakarta.servlet` / Tomcat classes not found when running `BackendApplication` | Run config → Modify options → tick **Add dependencies with "provided" scope to classpath**. |
| Port 8080 already in use | Stop the other process, or run with `--server.port=9090`. |
| Page shows "Loading..." on :4200 | The backend isn't running on :8080, or you used `ng serve` instead of `npm start`. |
