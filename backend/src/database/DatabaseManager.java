package database;

import models.*;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseManager: Handles SQLite JDBC Database Connection and CRUD operations.
 * Demonstrates standard Java JDBC (DriverManager, Connection, PreparedStatement, ResultSet).
 */
public class DatabaseManager {
    private final String dbUrl;

    public DatabaseManager() {
        this("skillexchange.db");
    }

    public DatabaseManager(String dbFileName) {
        File dbFile;
        // Check if running from project root or inside backend/
        if (new File("backend").isDirectory()) {
            File backendDir = new File("backend");
            dbFile = new File(backendDir, dbFileName);
        } else {
            dbFile = new File(dbFileName);
        }
        this.dbUrl = "jdbc:sqlite:" + dbFile.getAbsolutePath();
    }

    public Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC Driver not found in classpath!", e);
        }
        Connection conn = DriverManager.getConnection(dbUrl);
        try (Statement s = conn.createStatement()) {
            s.execute("PRAGMA busy_timeout = 5000");
        }
        return conn;
    }

    /**
     * Initializes database tables if they do not exist.
     * Seeds initial demo data if the students table is empty.
     */
    public synchronized void init() {
        boolean needsSeed = false;
        try (Connection conn = getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA journal_mode = WAL");
                stmt.execute("PRAGMA busy_timeout = 5000");

                // 1. Students table
                stmt.execute("CREATE TABLE IF NOT EXISTS students (" +
                        "id TEXT PRIMARY KEY, " +
                        "name TEXT NOT NULL, " +
                        "student_id TEXT NOT NULL, " +
                        "department TEXT NOT NULL, " +
                        "year TEXT NOT NULL, " +
                        "bio TEXT, " +
                        "avatar TEXT" +
                        ")");

                // 2. Skills table
                stmt.execute("CREATE TABLE IF NOT EXISTS skills (" +
                        "id TEXT PRIMARY KEY, " +
                        "name TEXT NOT NULL, " +
                        "category TEXT NOT NULL, " +
                        "description TEXT, " +
                        "icon TEXT, " +
                        "added_by TEXT, " +
                        "added_at TEXT" +
                        ")");

                // 3. Student Skills junction table (many-to-many relationship)
                stmt.execute("CREATE TABLE IF NOT EXISTS student_skills (" +
                        "student_id TEXT NOT NULL, " +
                        "skill_id TEXT NOT NULL, " +
                        "type TEXT NOT NULL, " + // 'teach' or 'learn'
                        "PRIMARY KEY (student_id, skill_id, type)" +
                        ")");

                // 4. Skill Requests table
                stmt.execute("CREATE TABLE IF NOT EXISTS skill_requests (" +
                        "id TEXT PRIMARY KEY, " +
                        "requester_id TEXT NOT NULL, " +
                        "receiver_id TEXT NOT NULL, " +
                        "skill_id TEXT NOT NULL, " +
                        "message TEXT, " +
                        "status TEXT NOT NULL, " +
                        "created_at TEXT" +
                        ")");

                // 5. Skill Exchanges table
                stmt.execute("CREATE TABLE IF NOT EXISTS skill_exchanges (" +
                        "id TEXT PRIMARY KEY, " +
                        "student1_id TEXT NOT NULL, " +
                        "student2_id TEXT NOT NULL, " +
                        "skill_id TEXT NOT NULL, " +
                        "status TEXT NOT NULL, " +
                        "started_at TEXT, " +
                        "completed_at TEXT" +
                        ")");

                // 6. App settings table
                stmt.execute("CREATE TABLE IF NOT EXISTS app_settings (" +
                        "setting_key TEXT PRIMARY KEY, " +
                        "setting_value TEXT NOT NULL" +
                        ")");

                // Check if empty
                try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS count FROM students")) {
                    if (rs.next() && rs.getInt("count") == 0) {
                        needsSeed = true;
                    }
                }
            }

            if (needsSeed) {
                seedDemoData(conn);
            }
            System.out.println("💾 SQLite Database connected at: " + dbUrl);
        } catch (SQLException e) {
            System.err.println("❌ Database initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Seeds initial demo records into the SQLite database.
     */
    public synchronized void seedDemoData() {
        try (Connection conn = getConnection()) {
            seedDemoData(conn);
        } catch (SQLException e) {
            System.err.println("❌ Error seeding database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public synchronized void seedDemoData(Connection conn) throws SQLException {
        boolean originalAutoCommit = conn.getAutoCommit();
        conn.setAutoCommit(false);
        try {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("DELETE FROM student_skills");
                stmt.execute("DELETE FROM skill_exchanges");
                stmt.execute("DELETE FROM skill_requests");
                stmt.execute("DELETE FROM skills");
                stmt.execute("DELETE FROM students");
                stmt.execute("DELETE FROM app_settings");
            }

            // Insert initial Skills
            String sqlSkill = "INSERT INTO skills (id, name, category, description, icon, added_by, added_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlSkill)) {
                insertSkillRecord(ps, "sk1", "Python", "Programming", "Programming language for data science and web development.", "🐍", "Anu", "2 hours ago");
                insertSkillRecord(ps, "sk2", "Figma", "Design", "UI/UX design and prototyping tool.", "🎨", "Alex", "1 day ago");
                insertSkillRecord(ps, "sk3", "Java", "Programming", "Object-oriented programming language.", "☕", "Rahul", "5 hours ago");
                insertSkillRecord(ps, "sk4", "Video Editing", "Video Editing", "Edit videos using Adobe Premiere Pro.", "🎬", "Anu", "2 hours ago");
                insertSkillRecord(ps, "sk5", "Photoshop", "Design", "Photo editing and graphic design.", "🖌️", "Anu", "1 day ago");
                insertSkillRecord(ps, "sk6", "JavaScript", "Programming", "Web development language.", "⚡", "Rahul", "2 days ago");
                insertSkillRecord(ps, "sk7", "Machine Learning", "Academic", "Fundamentals of ML, neural networks and predictive modeling.", "🤖", "Alex", "3 days ago");
                insertSkillRecord(ps, "sk8", "Digital Marketing", "Marketing", "SEO, content marketing and social media growth strategies.", "📈", "Rahul", "3 days ago");
            }

            // Insert initial Students
            String sqlStudent = "INSERT INTO students (id, name, student_id, department, year, bio, avatar) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlStudent)) {
                insertStudentRecord(ps, "s1", "Alex", "CS2024001", "Computer Science", "2",
                        "A passionate learner who loves design, coding and exploring new things. Let's exchange skills and grow together! 🚀", "👩‍🎓");
                insertStudentRecord(ps, "s2", "Rahul", "CS2024002", "Computer Science", "2",
                        "Enthusiastic developer focused on backend architectures and algorithms. Looking to master UI/UX.", "👨‍🎓");
                insertStudentRecord(ps, "s3", "Anu", "EC2024015", "Electronics", "2",
                        "Digital media and multimedia creator exploring automation with Python.", "👩‍💼");
            }

            // Insert Student Skills
            String sqlJunction = "INSERT INTO student_skills (student_id, skill_id, type) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlJunction)) {
                // Alex (s1)
                insertStudentSkillRecord(ps, "s1", "sk2", "teach");
                insertStudentSkillRecord(ps, "s1", "sk1", "teach");
                insertStudentSkillRecord(ps, "s1", "sk3", "learn");
                insertStudentSkillRecord(ps, "s1", "sk4", "learn");
                // Rahul (s2)
                insertStudentSkillRecord(ps, "s2", "sk3", "teach");
                insertStudentSkillRecord(ps, "s2", "sk1", "teach");
                insertStudentSkillRecord(ps, "s2", "sk2", "learn");
                // Anu (s3)
                insertStudentSkillRecord(ps, "s3", "sk5", "teach");
                insertStudentSkillRecord(ps, "s3", "sk4", "teach");
                insertStudentSkillRecord(ps, "s3", "sk1", "learn");
            }

            // Insert Requests
            String sqlReq = "INSERT INTO skill_requests (id, requester_id, receiver_id, skill_id, message, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlReq)) {
                insertRequestRecord(ps, "req1", "s1", "s2", "sk3", "Hi! I'd love to learn Java. Let me know if you're interested!", "pending", "Apr 26, 2025");
                insertRequestRecord(ps, "req2", "s3", "s1", "sk1", "Hey Alex, can you teach me Python basics?", "accepted", "Apr 25, 2025");
                insertRequestRecord(ps, "req3", "s2", "s1", "sk2", "Wants to learn Figma. 'I'd love to learn Figma. Let me know!'", "pending", "Apr 26, 2025");
            }

            // Insert Exchanges
            String sqlEx = "INSERT INTO skill_exchanges (id, student1_id, student2_id, skill_id, status, started_at, completed_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlEx)) {
                insertExchangeRecord(ps, "ex1", "s1", "s2", "sk3", "active", "Apr 25, 2025", null);
            }

            // App Settings
            String sqlSetting = "INSERT INTO app_settings (setting_key, setting_value) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlSetting)) {
                ps.setString(1, "current_user_id");
                ps.setString(2, "s1");
                ps.executeUpdate();
            }

            conn.commit();
            System.out.println("🌱 Demo data seeded into SQLite database successfully.");
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(originalAutoCommit);
        }
    }

    private void insertSkillRecord(PreparedStatement ps, String id, String name, String category, String description, String icon, String addedBy, String addedAt) throws SQLException {
        ps.setString(1, id);
        ps.setString(2, name);
        ps.setString(3, category);
        ps.setString(4, description);
        ps.setString(5, icon);
        ps.setString(6, addedBy);
        ps.setString(7, addedAt);
        ps.executeUpdate();
    }

    private void insertStudentRecord(PreparedStatement ps, String id, String name, String studentId, String dept, String year, String bio, String avatar) throws SQLException {
        ps.setString(1, id);
        ps.setString(2, name);
        ps.setString(3, studentId);
        ps.setString(4, dept);
        ps.setString(5, year);
        ps.setString(6, bio);
        ps.setString(7, avatar);
        ps.executeUpdate();
    }

    private void insertStudentSkillRecord(PreparedStatement ps, String studentId, String skillId, String type) throws SQLException {
        ps.setString(1, studentId);
        ps.setString(2, skillId);
        ps.setString(3, type);
        ps.executeUpdate();
    }

    private void insertRequestRecord(PreparedStatement ps, String id, String reqId, String recId, String skillId, String msg, String status, String created) throws SQLException {
        ps.setString(1, id);
        ps.setString(2, reqId);
        ps.setString(3, recId);
        ps.setString(4, skillId);
        ps.setString(5, msg);
        ps.setString(6, status);
        ps.setString(7, created);
        ps.executeUpdate();
    }

    private void insertExchangeRecord(PreparedStatement ps, String id, String s1, String s2, String skillId, String status, String started, String completed) throws SQLException {
        ps.setString(1, id);
        ps.setString(2, s1);
        ps.setString(3, s2);
        ps.setString(4, skillId);
        ps.setString(5, status);
        ps.setString(6, started);
        ps.setString(7, completed);
        ps.executeUpdate();
    }

    // =========================================================================
    // READ QUERIES
    // =========================================================================

    public synchronized List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT id, name, student_id, department, year, bio, avatar FROM students ORDER BY rowid ASC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String studentId = rs.getString("student_id");
                String department = rs.getString("department");
                String year = rs.getString("year");
                String bio = rs.getString("bio");
                String avatar = rs.getString("avatar");

                List<String> teach = getStudentSkills(conn, id, "teach");
                List<String> learn = getStudentSkills(conn, id, "learn");

                list.add(new Student(id, name, studentId, department, year, bio, teach, learn, avatar));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error loading students: " + e.getMessage());
        }
        return list;
    }

    private List<String> getStudentSkills(Connection conn, String studentId, String type) throws SQLException {
        List<String> skills = new ArrayList<>();
        String sql = "SELECT skill_id FROM student_skills WHERE student_id = ? AND type = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, type);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    skills.add(rs.getString("skill_id"));
                }
            }
        }
        return skills;
    }

    public synchronized List<Skill> getAllSkills() {
        List<Skill> list = new ArrayList<>();
        String sql = "SELECT id, name, category, description, icon, added_by, added_at FROM skills ORDER BY rowid DESC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Skill(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getString("description"),
                        rs.getString("icon"),
                        rs.getString("added_by"),
                        rs.getString("added_at")
                ));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error loading skills: " + e.getMessage());
        }
        return list;
    }

    public synchronized List<SkillRequest> getAllRequests() {
        List<SkillRequest> list = new ArrayList<>();
        String sql = "SELECT id, requester_id, receiver_id, skill_id, message, status, created_at FROM skill_requests ORDER BY rowid DESC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new SkillRequest(
                        rs.getString("id"),
                        rs.getString("requester_id"),
                        rs.getString("receiver_id"),
                        rs.getString("skill_id"),
                        rs.getString("message"),
                        rs.getString("status"),
                        rs.getString("created_at")
                ));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error loading requests: " + e.getMessage());
        }
        return list;
    }

    public synchronized List<SkillExchange> getAllExchanges() {
        List<SkillExchange> list = new ArrayList<>();
        String sql = "SELECT id, student1_id, student2_id, skill_id, status, started_at FROM skill_exchanges ORDER BY rowid DESC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new SkillExchange(
                        rs.getString("id"),
                        rs.getString("student1_id"),
                        rs.getString("student2_id"),
                        rs.getString("skill_id"),
                        rs.getString("status"),
                        rs.getString("started_at")
                ));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error loading exchanges: " + e.getMessage());
        }
        return list;
    }

    public synchronized String getCurrentUserId() {
        String sql = "SELECT setting_value FROM app_settings WHERE setting_key = 'current_user_id'";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getString("setting_value");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error loading current_user_id: " + e.getMessage());
        }
        return "s1";
    }

    // =========================================================================
    // WRITE / MUTATION OPERATIONS
    // =========================================================================

    public synchronized void addSkill(Skill skill, String studentId, String intent) {
        String sql = "INSERT INTO skills (id, name, category, description, icon, added_by, added_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            insertSkillRecord(ps, skill.getId(), skill.getName(), skill.getCategory(), skill.getDescription(), skill.getIcon(), skill.getAddedBy(), skill.getAddedAt());

            if (studentId != null && !studentId.isEmpty()) {
                if ("teach".equalsIgnoreCase(intent) || "learn".equalsIgnoreCase(intent)) {
                    String jSql = "INSERT OR REPLACE INTO student_skills (student_id, skill_id, type) VALUES (?, ?, ?)";
                    try (PreparedStatement jPs = conn.prepareStatement(jSql)) {
                        jPs.setString(1, studentId);
                        jPs.setString(2, skill.getId());
                        jPs.setString(3, intent.toLowerCase());
                        jPs.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error inserting skill: " + e.getMessage());
        }
    }

    public synchronized void deleteSkill(String skillId) {
        try (Connection conn = getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM student_skills WHERE skill_id = ?")) {
                    ps.setString(1, skillId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM skill_requests WHERE skill_id = ?")) {
                    ps.setString(1, skillId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM skill_exchanges WHERE skill_id = ?")) {
                    ps.setString(1, skillId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM skills WHERE id = ?")) {
                    ps.setString(1, skillId);
                    ps.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(originalAutoCommit);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error deleting skill: " + e.getMessage());
        }
    }

    public synchronized void addRequest(SkillRequest req) {
        String sql = "INSERT INTO skill_requests (id, requester_id, receiver_id, skill_id, message, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            insertRequestRecord(ps, req.getId(), req.getRequesterId(), req.getReceiverId(), req.getSkillId(), req.getMessage(), req.getStatus(), req.getCreatedAt());
        } catch (SQLException e) {
            System.err.println("❌ Error inserting request: " + e.getMessage());
        }
    }

    public synchronized void updateRequestStatus(String reqId, String status) {
        String sql = "UPDATE skill_requests SET status = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, reqId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Error updating request status: " + e.getMessage());
        }
    }

    public synchronized void deleteRequest(String reqId) {
        String sql = "DELETE FROM skill_requests WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reqId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Error deleting request: " + e.getMessage());
        }
    }

    public synchronized void addExchange(SkillExchange ex) {
        String sql = "INSERT INTO skill_exchanges (id, student1_id, student2_id, skill_id, status, started_at, completed_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            insertExchangeRecord(ps, ex.getId(), ex.getStudent1Id(), ex.getStudent2Id(), ex.getSkillId(), ex.getStatus(), ex.getStartedAt(), null);
        } catch (SQLException e) {
            System.err.println("❌ Error inserting exchange: " + e.getMessage());
        }
    }

    public synchronized void updateExchangeStatus(String exId, String status) {
        String sql = "UPDATE skill_exchanges SET status = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, exId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Error updating exchange status: " + e.getMessage());
        }
    }

    public synchronized void updateStudentProfile(String studentId, String name, String dept, String year, String bio) {
        String sql = "UPDATE students SET name = ?, department = ?, year = ?, bio = ? WHERE id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, dept);
            ps.setString(3, year);
            ps.setString(4, bio);
            ps.setString(5, studentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Error updating student profile: " + e.getMessage());
        }
    }

    public synchronized void setCurrentUserId(String id) {
        String sql = "INSERT INTO app_settings (setting_key, setting_value) VALUES ('current_user_id', ?) " +
                "ON CONFLICT(setting_key) DO UPDATE SET setting_value = excluded.setting_value";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Error setting current_user_id: " + e.getMessage());
        }
    }
}
