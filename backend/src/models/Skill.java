package models;

/**
 * Entity Class: Skill
 * Represents an academic or practical skill in the system.
 */
public class Skill {
    private String id;
    private String name;
    private String category;
    private String description;
    private String icon;
    private String addedBy;
    private String addedAt;

    public Skill(String id, String name, String category, String description, String icon, String addedBy, String addedAt) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description != null ? description : "";
        this.icon = (icon != null && !icon.isEmpty()) ? icon : getDefaultIcon(category);
        this.addedBy = addedBy != null ? addedBy : "Student";
        this.addedAt = addedAt != null ? addedAt : "Recently";
    }

    public static String getDefaultIcon(String category) {
        if (category == null) return "💡";
        switch (category) {
            case "Programming": return "💻";
            case "Design": return "🎨";
            case "Languages": return "🗣️";
            case "Marketing": return "📈";
            case "Photography": return "📷";
            case "Video Editing": return "🎬";
            case "Academic": return "📚";
            default: return "💡";
        }
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getIcon() { return icon; }
    public String getAddedBy() { return addedBy; }
    public String getAddedAt() { return addedAt; }

    public String toJson() {
        return "{"
                + "\"id\":\"" + escape(id) + "\","
                + "\"name\":\"" + escape(name) + "\","
                + "\"category\":\"" + escape(category) + "\","
                + "\"description\":\"" + escape(description) + "\","
                + "\"icon\":\"" + escape(icon) + "\","
                + "\"addedBy\":\"" + escape(addedBy) + "\","
                + "\"addedAt\":\"" + escape(addedAt) + "\""
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