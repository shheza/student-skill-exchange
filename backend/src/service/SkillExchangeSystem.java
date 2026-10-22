package service;

import database.DatabaseManager;
import models.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service Layer: SkillExchangeSystem
 * Orchestrates business operations and persists state via DatabaseManager (SQLite JDBC).
 */
public class SkillExchangeSystem {
    private final DatabaseManager db;
    private final List<Student> students;
    private final List<Skill> skills;
    private final List<SkillRequest> requests;
    private final List<SkillExchange> exchanges;
    private String currentUserId;

    public SkillExchangeSystem() {
        this.db = new DatabaseManager();
        this.students = new ArrayList<>();
        this.skills = new ArrayList<>();
        this.requests = new ArrayList<>();
        this.exchanges = new ArrayList<>();

        // Initialize SQLite Database tables and load state
        this.db.init();
        refreshFromDatabase();
    }

    /**
     * Synchronizes in-memory collections with the SQLite database.
     */
    public synchronized void refreshFromDatabase() {
        students.clear();
        students.addAll(db.getAllStudents());

        skills.clear();
        skills.addAll(db.getAllSkills());

        requests.clear();
        requests.addAll(db.getAllRequests());

        exchanges.clear();
        exchanges.addAll(db.getAllExchanges());

        this.currentUserId = db.getCurrentUserId();
        if (this.currentUserId == null || this.currentUserId.isEmpty()) {
            this.currentUserId = !students.isEmpty() ? students.get(0).getId() : "s1";
        }
    }

    public void seedDemoData() {
        db.seedDemoData();
        refreshFromDatabase();
    }

    public List<Student> getStudents() { return students; }
    public List<Skill> getSkills() { return skills; }
    public List<SkillRequest> getRequests() { return requests; }
    public List<SkillExchange> getExchanges() { return exchanges; }
    public String getCurrentUserId() { return currentUserId; }

    public void setCurrentUserId(String id) {
        this.currentUserId = id;
        db.setCurrentUserId(id);
    }

    public Student getStudentById(String id) {
        for (Student s : students) {
            if (s.getId().equals(id)) return s;
        }
        return null;
    }

    public Skill getSkillById(String id) {
        for (Skill s : skills) {
            if (s.getId().equals(id)) return s;
        }
        return null;
    }

    public SkillRequest getRequestById(String id) {
        for (SkillRequest r : requests) {
            if (r.getId().equals(id)) return r;
        }
        return null;
    }

    public SkillExchange getExchangeById(String id) {
        for (SkillExchange e : exchanges) {
            if (e.getId().equals(id)) return e;
        }
        return null;
    }

    public Student getCurrentStudent() {
        Student s = getStudentById(currentUserId);
        return s != null ? s : (students.isEmpty() ? null : students.get(0));
    }

    public Skill addSkill(String name, String category, String description, String intent) {
        String id = "sk" + System.currentTimeMillis();
        Student cur = getCurrentStudent();
        String addedBy = cur != null ? cur.getName() : "Student";
        Skill skill = new Skill(id, name, category, description, "", addedBy, "Just now");

        // Save to SQLite database
        db.addSkill(skill, (cur != null ? cur.getId() : null), intent);

        // Refresh state
        refreshFromDatabase();
        return skill;
    }

    public boolean removeSkill(String skillId) {
        db.deleteSkill(skillId);
        refreshFromDatabase();
        return true;
    }

    public SkillRequest sendRequest(String receiverId, String skillId, String message) throws Exception {
        Student cur = getCurrentStudent();
        if (cur == null) throw new Exception("No active student selected");
        if (cur.getId().equals(receiverId)) throw new Exception("Cannot send exchange request to yourself");

        // Check for existing pending request
        for (SkillRequest r : requests) {
            if (r.getRequesterId().equals(cur.getId()) &&
                r.getReceiverId().equals(receiverId) &&
                r.getSkillId().equals(skillId) &&
                "pending".equalsIgnoreCase(r.getStatus())) {
                throw new Exception("A pending request for this skill already exists with this student");
            }
        }

        String id = "req" + System.currentTimeMillis();
        SkillRequest req = new SkillRequest(id, cur.getId(), receiverId, skillId, message, "pending", "Just now");

        // Save to SQLite database
        db.addRequest(req);
        refreshFromDatabase();
        return req;
    }

    public SkillExchange acceptRequest(String requestId) {
        SkillRequest req = getRequestById(requestId);
        if (req != null) {
            String exId = "ex" + System.currentTimeMillis();
            SkillExchange ex = new SkillExchange(exId, req.getRequesterId(), req.getReceiverId(), req.getSkillId(), "active", "Just now");

            // Update in SQLite database
            db.updateRequestStatus(requestId, "accepted");
            db.addExchange(ex);

            refreshFromDatabase();
            return ex;
        }
        return null;
    }

    public boolean rejectRequest(String requestId) {
        SkillRequest req = getRequestById(requestId);
        if (req != null) {
            db.updateRequestStatus(requestId, "rejected");
            refreshFromDatabase();
            return true;
        }
        return false;
    }

    public boolean cancelRequest(String requestId) {
        db.deleteRequest(requestId);
        refreshFromDatabase();
        return true;
    }

    public boolean completeExchange(String exchangeId) {
        SkillExchange ex = getExchangeById(exchangeId);
        if (ex != null) {
            db.updateExchangeStatus(exchangeId, "completed");
            refreshFromDatabase();
            return true;
        }
        return false;
    }

    public boolean cancelExchange(String exchangeId) {
        SkillExchange ex = getExchangeById(exchangeId);
        if (ex != null) {
            db.updateExchangeStatus(exchangeId, "cancelled");
            refreshFromDatabase();
            return true;
        }
        return false;
    }

    public boolean updateProfile(String id, String name, String department, String year, String bio) {
        db.updateStudentProfile(id, name, department, year, bio);
        refreshFromDatabase();
        return true;
    }
}