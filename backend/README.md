# ☕ SkillX Java Backend
### Object-Oriented Programming (OOP) REST Server with SQLite Database

This backend powers the **Student Skill Exchange System** using pure Java Standard Library (`com.sun.net.httpserver`) and a relational **SQLite Database** (`skillexchange.db`) via JDBC (`java.sql.*`). No heavyweight frameworks (like Spring Boot) are required, keeping the code clean, readable, and focused on core OOP principles for your college viva.

---

## 🗄️ Database Architecture (SQLite JDBC)

The backend includes a dedicated **Data Access Layer** (`database.DatabaseManager`) that connects to SQLite via JDBC.

### Relational Schema Tables:
1. **`students`**: Stores student academic identity (`id`, `name`, `student_id`, `department`, `year`, `bio`, `avatar`).
2. **`skills`**: Stores skills catalog (`id`, `name`, `category`, `description`, `icon`, `added_by`, `added_at`).
3. **`student_skills`**: Junction table for the Many-to-Many relationship between students and skills (`student_id`, `skill_id`, `type`: `'teach'` or `'learn'`).
4. **`skill_requests`**: Stores exchange requests (`id`, `requester_id`, `receiver_id`, `skill_id`, `message`, `status`, `created_at`).
5. **`skill_exchanges`**: Stores ongoing/completed exchanges (`id`, `student1_id`, `student2_id`, `skill_id`, `status`, `started_at`, `completed_at`).
6. **`app_settings`**: Key-value settings table preserving the currently logged-in student.

### Database File:
- Stored on disk at: `backend/skillexchange.db`
- Data persists permanently across server restarts.

---

## 🏛️ Java OOP Concepts Mapping

| OOP Concept | Java Implementation | Explanation for Viva |
| :--- | :--- | :--- |
| **Abstraction** | `models.User` (Abstract Class) | `User` defines core identity attributes (`id`, `name`) and declares abstract method `displayProfile()`, enforcing an interface contract without detailing student-specific implementations. |
| **Inheritance** | `models.Student extends User` | `Student` inherits common fields from `User` and invokes `super(id, name)` in its constructor for constructor chaining. |
| **Polymorphism & Method Overriding** | `@Override public String displayProfile()` | `Student` overrides `User.displayProfile()` to output roll number, department, year, and counts of skills taught and to learn. |
| **Encapsulation & Data Hiding** | `Student`, `Skill`, `SkillRequest`, `DatabaseManager` | Fields are declared `private` or `protected`. Collections like `skillsToTeach` and `skillsToLearn` can only be modified through guarded methods (`addSkill`, `removeSkill`). `DatabaseManager` encapsulates raw SQL connections and transactions. |
| **Constructors** | All model classes | Explicit parameterised constructors initialize state, timestamps, and empty collections. |
| **Collections** | `java.util.List`, `ArrayList` | Dynamic in-memory collections in `SkillExchangeSystem` synchronize directly with the SQLite database. |
| **Design Patterns** | **DAO / Repository Pattern** & **Facade Pattern** | `DatabaseManager` handles data access (DAO). `SkillExchangeSystem` acts as a Facade providing a clean service API for all frontend requests. |

---

## 🚀 How to Run the Java Backend

### Method 1: One-Click Script (Recommended)
Double-click **`run.bat`** in the `backend/` folder (or `run.bat` in the project root). It will compile all `.java` files with the SQLite JDBC library and start the server at `http://localhost:8080`.

### Method 2: Command Line
```cmd
cd backend
javac -encoding UTF-8 -cp "lib/*" -d bin src/models/*.java src/database/*.java src/service/*.java src/server/*.java src/Main.java
java -cp "lib/*;bin" Main 8080
```

---

## 📡 REST API Endpoints

- `GET /api/health` — Checks server health.
- `GET /api/state` — Retrieves the complete system state from SQLite (students, skills, requests, exchanges, active user).
- `GET /api/students` — Lists all registered students.
- `GET /api/skills` — Lists all skills from SQLite.
- `POST /api/skills` — Adds a new skill and persists to SQLite.
- `DELETE /api/skills?id={id}` — Removes a skill from SQLite and junction tables.
- `GET /api/requests` — Lists all skill exchange requests.
- `POST /api/requests` — Submits a new skill request into SQLite.
- `POST /api/requests/accept?id={id}` — Accepts a request and creates an active exchange in SQLite.
- `POST /api/requests/reject?id={id}` — Rejects a request in SQLite.
- `POST /api/requests/cancel?id={id}` — Cancels/deletes a request in SQLite.
- `GET /api/exchanges` — Lists active and completed exchanges.
- `POST /api/exchanges/complete?id={id}` — Marks exchange as completed in SQLite.
- `POST /api/exchanges/cancel?id={id}` — Cancels an exchange in SQLite.
- `POST /api/profile` — Updates student profile in SQLite.
- `POST /api/user/switch` — Switches active student in SQLite.
- `POST /api/reset` — Restores initial seeded demo data (Alex, Rahul, Anu) into SQLite.