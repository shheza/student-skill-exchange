# 🎓 SkillX — Student Skill Exchange System
### College Object-Oriented Programming (OOP) Mini Project
> Full-Stack OOP Mini Project featuring a pure **HTML5/CSS3/Vanilla JS** frontend connected to a native **Java REST API Server** (`com.sun.net.httpserver`), with automatic browser `LocalStorage` fallback.

---

## 📁 1. Project Folder Structure

```text
student-skill-exchange/
│
├── index.html                 # Clean, responsive single-page campus UI
├── style.css                  # Modern CSS design matching campus UI mockup
├── app.js                     # Frontend OOP Classes, StorageManager, BackendBridge & UI
├── run.bat                    # One-click script to compile Java backend & launch website
├── README.md                  # Complete Viva Voce guide and documentation
│
└── backend/                   # Pure Java OOP Backend with SQLite Database
    ├── run.bat                # Direct backend compile & run script
    ├── README.md              # Java OOP viva guide & REST endpoint specifications
    ├── skillexchange.db       # Relational SQLite Database file (persistent storage)
    ├── bin/                   # Compiled .class bytecode
    ├── lib/                   # Bundled SQLite JDBC & logging libraries
    ├── jdk/                   # Portable OpenJDK 17 (self-contained, no system installation required)
    └── src/
        ├── Main.java          # Backend entry point
        ├── models/
        │   ├── User.java          # Abstract base class (Abstraction)
        │   ├── Student.java       # Subclass extending User (Inheritance, Polymorphism, Encapsulation)
        │   ├── Skill.java         # Skill domain model
        │   ├── SkillRequest.java  # Request model with accept()/reject() state machine
        │   └── SkillExchange.java # Active exchange model with complete()/cancel()
        ├── database/
        │   └── DatabaseManager.java # SQLite JDBC Data Access Layer (Connection, CRUD, Schema)
        ├── service/
        │   └── SkillExchangeSystem.java # Core business logic managing database synchronization
        └── server/
            ├── JsonUtils.java     # Zero-dependency JSON serializer and regex parser
            └── SkillServer.java   # Multi-threaded HTTP REST API & static web server (port 8080)
```

---

## 🚀 2. How to Run the Project

### Option A: Complete Full-Stack Mode (Recommended for Viva)
1. Double-click **`run.bat`** in the `student-skill-exchange` folder (or run `run.bat` from terminal).
2. The script compiles the Java classes and automatically starts the Java server on `http://localhost:8080`.
3. Your default web browser will open **`http://localhost:8080`**.
4. The top header displays:
   `🟢 Java Backend: Connected (8080)`
5. All actions (adding skills, sending requests, accepting exchanges, updating profiles) synchronize live with the Java backend!

### Option B: Standalone Browser Mode (Zero Backend)
1. Simply double-click **`index.html`** in any web browser.
2. The application automatically detects that the backend is offline and switches to:
   `⚪ Browser Storage (Offline Java)`
3. All operations run directly in the browser via `LocalStorage`.

---

## 🏛️ 3. OOP Concepts Mapping (Viva Voce Cheat Sheet)

The system demonstrates OOP concepts in **both JavaScript and Java**:

| OOP Concept | JavaScript (`app.js`) | Java Backend (`backend/src/`) | Viva Voce Explanation |
| :--- | :--- | :--- | :--- |
| **Abstraction** | `class User` | `public abstract class User` | Base class defines common attributes (`id`, `name`) and abstract contract `displayProfile()`, hiding role details. |
| **Inheritance** | `class Student extends User` | `public class Student extends User` | `Student` inherits identity properties using `super(...)` and extends them with academic fields (`studentId`, `department`, `year`, `bio`, skills). |
| **Polymorphism** | `Student.prototype.displayProfile()` | `@Override public String displayProfile()` | Both define `displayProfile()`. `Student` overrides it to provide a rich academic summary. Dynamic dispatch invokes the subclass method at runtime. |
| **Encapsulation** | Methods manage `skillsToTeach`, `skillsToLearn` | Private fields with getters, setters, and guarded mutators (`addSkill()`, `accept()`, `complete()`) | State cannot be corrupted by external code; changes pass through validated member methods. |
| **Constructors** | `constructor(...)` in all classes | Explicit parameterized constructors | Ensure every object is created in a valid, well-defined state with default timestamps and status. |
| **Objects** | Runtime instances of all classes | Real Java instances instantiated in memory | Plain serialized JSON is transformed into actual class instances with full method accessibility. |
| **Collections** | Array operations (`filter`, `map`, `find`) | `List<Student>`, `List<Skill>`, `List<SkillRequest>` using `ArrayList` | Dynamic in-memory collections of domain entities. |
| **Design Pattern** | **Bridge Pattern** via `BackendBridge` | **Facade Pattern** via `SkillExchangeSystem` | Decouples UI presentation and data persistence from network communication. |

---

## 🔄 4. Pages and Modules

The navigation bar has been kept clean and focused:

1. **Dashboard**: Summary metrics (Total Students, Skills in Catalog, Pending Requests, Active Exchanges), Quick Action shortcuts, and Recently Added Skills.
2. **Skills Catalog**: Interactive catalog of all offered skills with category filters, skill addition, and deletion.
3. **Search Skills**: Instant search by keyword or category to discover peer students who can teach a specific skill, with a 1-click **Request Exchange** modal.
4. **Exchanges (Consolidated Requests & Exchanges)**:
   - **Tab 1: Active Exchanges** — Ongoing peer skill exchanges with `Mark Complete` and `Cancel` buttons.
   - **Tab 2: Received Requests** — Incoming exchange requests with `Accept` and `Reject` buttons.
   - **Tab 3: Sent Requests** — Outgoing requests with status indicators (`Pending`, `Accepted`, `Rejected`) and `Cancel` option.
5. **My Profile**: View and edit student details, academic year, department, bio, and manage skills to teach and skills to learn.
6. **Student Switcher**: Header dropdown allowing instant role-switching between demo students (Alex, Rahul, Anu) to test end-to-end multi-user interactions.

---

## ⚡ 5. End-to-End Demonstration Walkthrough

1. **View Catalog as Alex**:
   - Open `http://localhost:8080`. Alex (Year 2 CS) is logged in.
   - Alex wants to learn **Java**.
2. **Search for Skill**:
   - Navigate to **Search Skills** and type `Java`.
   - **Rahul** appears as a student who teaches Java.
3. **Send Exchange Request**:
   - Click **Request Exchange** on Rahul's card.
   - Send: *"Hi Rahul, I can teach you Figma if you help me with Java!"*
4. **Switch User to Rahul**:
   - In the header dropdown, switch the active user to **Rahul**.
5. **Accept the Request**:
   - Open **Exchanges** -> click the **Received Requests** tab.
   - See the request from Alex and click **Accept**.
6. **Active Exchange Formed**:
   - The exchange **Alex ↔ Rahul** for **Java** is created and listed under **Active Exchanges**.
7. **Verify Persistence**:
   - Refresh the browser (`F5`).
   - The active exchange and state remain fully intact!