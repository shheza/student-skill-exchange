# ☕ SkillX Java Backend
### Object-Oriented Programming (OOP) REST Server

This backend powers the **Student Skill Exchange System** using pure Java Standard Library (`com.sun.net.httpserver`). No external frameworks (like Spring Boot) are required, keeping the code clean, readable, and focused on core OOP principles for your college viva.

---

## 🏛️ Java OOP Concepts Mapping

| OOP Concept | Java Implementation | Explanation for Viva |
| :--- | :--- | :--- |
| **Abstraction** | `models.User` (Abstract Class) | `User` defines core identity attributes (`id`, `name`) and declares abstract method `displayProfile()`, enforcing an interface contract without detailing student-specific implementations. |
| **Inheritance** | `models.Student extends User` | `Student` inherits common fields from `User` and invokes `super(id, name)` in its constructor for constructor chaining. |
| **Polymorphism & Method Overriding** | `@Override public String displayProfile()` | `Student` overrides `User.displayProfile()` to output roll number, department, year, and counts of skills taught and to learn. |
| **Encapsulation** | `Student`, `Skill`, `SkillRequest` | Fields are declared `private` or `protected`. Collections like `skillsToTeach` and `skillsToLearn` can only be modified through validated methods (`addSkill`, `removeSkill`). |
| **Constructors** | All model classes | Explicit parameterised constructors initialize state, timestamps, and empty collections. |
| **Collections** | `java.util.List`, `ArrayList` | Dynamic collections in `SkillExchangeSystem` manage students, skills, requests, and active exchanges. |

---

## 🚀 How to Run the Java Backend

### Method 1: One-Click Script
Double-click **`run.bat`** in the `backend/` folder. It will automatically compile all `.java` files and start the server at `http://localhost:8080`.

### Method 2: Command Line
```bash
cd backend
javac -encoding UTF-8 -d bin src/models/*.java src/service/*.java src/server/*.java src/Main.java
java -cp bin Main 8080
```

---

## 📡 REST API Endpoints

- `GET /api/health` — Checks server health.
- `GET /api/state` — Retrieves the complete system state (students, skills, requests, exchanges, active user).
- `GET /api/students` — Lists all registered students.
- `GET /api/skills` — Lists all skills.
- `POST /api/skills` — Adds a new skill.
- `DELETE /api/skills?id={id}` — Removes a skill from the catalog.
- `GET /api/requests` — Lists all skill exchange requests.
- `POST /api/requests` — Submits a new skill request.
- `POST /api/requests/accept?id={id}` — Accepts a request and creates an active exchange.
- `POST /api/requests/reject?id={id}` — Rejects a request.
- `GET /api/exchanges` — Lists active and completed exchanges.
- `POST /api/exchanges/complete?id={id}` — Marks exchange as completed.
- `POST /api/reset` — Restores initial seeded demo data (Sheza, Rahul, Anu).