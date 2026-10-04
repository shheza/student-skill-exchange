package models;

/**
 * Entity Class: SkillRequest
 * Represents a skill exchange request between two students.
 */
public class SkillRequest {
    private String id;
    private String requesterId;
    private String receiverId;
    private String skillId;
    private String message;
    private String status; // "pending", "accepted", "rejected"
    private String createdAt;

    public SkillRequest(String id, String requesterId, String receiverId, String skillId, String message, String status, String createdAt) {
        this.id = id;
        this.requesterId = requesterId;
        this.receiverId = receiverId;
        this.skillId = skillId;
        this.message = message != null ? message : "";
        this.status = status != null ? status : "pending";
        this.createdAt = createdAt != null ? createdAt : "Recently";
    }

    public void accept() {
        this.status = "accepted";
    }

    public void reject() {
        this.status = "rejected";
    }

    public String getId() { return id; }
    public String getRequesterId() { return requesterId; }
    public String getReceiverId() { return receiverId; }
    public String getSkillId() { return skillId; }
    public String getMessage() { return message; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }

    public String toJson() {
        return "{"
                + "\"id\":\"" + escape(id) + "\","
                + "\"requesterId\":\"" + escape(requesterId) + "\","
                + "\"receiverId\":\"" + escape(receiverId) + "\","
                + "\"skillId\":\"" + escape(skillId) + "\","
                + "\"message\":\"" + escape(message) + "\","
                + "\"status\":\"" + escape(status) + "\","
                + "\"createdAt\":\"" + escape(createdAt) + "\""
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