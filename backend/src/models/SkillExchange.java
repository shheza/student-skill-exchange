package models;

/**
 * Entity Class: SkillExchange
 * Represents an ongoing, completed, or cancelled peer-to-peer exchange session.
 */
public class SkillExchange {
    private String id;
    private String student1Id;
    private String student2Id;
    private String skillId;
    private String status; // "active", "completed", "cancelled"
    private String startedAt;

    public SkillExchange(String id, String student1Id, String student2Id, String skillId, String status, String startedAt) {
        this.id = id;
        this.student1Id = student1Id;
        this.student2Id = student2Id;
        this.skillId = skillId;
        this.status = status != null ? status : "active";
        this.startedAt = startedAt != null ? startedAt : "Recently";
    }

    public void activate() { this.status = "active"; }
    public void complete() { this.status = "completed"; }
    public void cancel() { this.status = "cancelled"; }

    public String getId() { return id; }
    public String getStudent1Id() { return student1Id; }
    public String getStudent2Id() { return student2Id; }
    public String getSkillId() { return skillId; }
    public String getStatus() { return status; }
    public String getStartedAt() { return startedAt; }

    public String toJson() {
        return "{"
                + "\"id\":\"" + escape(id) + "\","
                + "\"student1Id\":\"" + escape(student1Id) + "\","
                + "\"student2Id\":\"" + escape(student2Id) + "\","
                + "\"skillId\":\"" + escape(skillId) + "\","
                + "\"status\":\"" + escape(status) + "\","
                + "\"startedAt\":\"" + escape(startedAt) + "\""
                + "}";
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}