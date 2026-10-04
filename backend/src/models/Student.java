package models;

import java.util.ArrayList;
import java.util.List;

/**
 * Derived Class: Student extends User
 * Demonstrates Inheritance, Polymorphism, and Encapsulation.
 */
public class Student extends User {
    private String studentId;
    private String department;
    private String year;
    private String bio;
    private List<String> skillsToTeach;
    private List<String> skillsToLearn;
    private String avatar;

    public Student(String id, String name, String studentId, String department, String year, String bio,
                   List<String> skillsToTeach, List<String> skillsToLearn, String avatar) {
        super(id, name); // Constructor chaining to parent class
        this.studentId = studentId;
        this.department = department;
        this.year = year;
        this.bio = bio != null ? bio : "";
        this.skillsToTeach = skillsToTeach != null ? new ArrayList<>(skillsToTeach) : new ArrayList<>();
        this.skillsToLearn = skillsToLearn != null ? new ArrayList<>(skillsToLearn) : new ArrayList<>();
        this.avatar = avatar != null ? avatar : "🎓";
    }

    // Encapsulation: Methods to safely mutate internal collections
    public boolean addSkill(String skillId) {
        if (!this.skillsToTeach.contains(skillId)) {
            this.skillsToTeach.add(skillId);
            return true;
        }
        return false;
    }

    public boolean addSkillToLearn(String skillId) {
        if (!this.skillsToLearn.contains(skillId)) {
            this.skillsToLearn.add(skillId);
            return true;
        }
        return false;
    }

    public void removeSkill(String skillId, String type) {
        if ("teach".equalsIgnoreCase(type)) {
            this.skillsToTeach.remove(skillId);
        } else if ("learn".equalsIgnoreCase(type)) {
            this.skillsToLearn.remove(skillId);
        }
    }

    public boolean canTeach(String skillId) {
        return this.skillsToTeach.contains(skillId);
    }

    public boolean wantsToLearn(String skillId) {
        return this.skillsToLearn.contains(skillId);
    }

    // Getters and Setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public List<String> getSkillsToTeach() { return skillsToTeach; }
    public List<String> getSkillsToLearn() { return skillsToLearn; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    /**
     * Overrides User.displayProfile()
     * Demonstrates Polymorphism & Method Overriding.
     */
    @Override
    public String displayProfile() {
        return name + " (" + studentId + ") - " + department + ", Year " + year
                + " | Teaching: " + skillsToTeach.size() + " | Learning: " + skillsToLearn.size();
    }

    @Override
    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"id\":\"").append(escape(id)).append("\",");
        sb.append("\"name\":\"").append(escape(name)).append("\",");
        sb.append("\"studentId\":\"").append(escape(studentId)).append("\",");
        sb.append("\"department\":\"").append(escape(department)).append("\",");
        sb.append("\"year\":\"").append(escape(year)).append("\",");
        sb.append("\"bio\":\"").append(escape(bio)).append("\",");
        sb.append("\"avatar\":\"").append(escape(avatar)).append("\",");

        sb.append("\"skillsToTeach\":[");
        for (int i = 0; i < skillsToTeach.size(); i++) {
            sb.append("\"").append(escape(skillsToTeach.get(i))).append("\"");
            if (i < skillsToTeach.size() - 1) sb.append(",");
        }
        sb.append("],");

        sb.append("\"skillsToLearn\":[");
        for (int i = 0; i < skillsToLearn.size(); i++) {
            sb.append("\"").append(escape(skillsToLearn.get(i))).append("\"");
            if (i < skillsToLearn.size() - 1) sb.append(",");
        }
        sb.append("]");
        sb.append("}");
        return sb.toString();
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}