package service;

import models.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service Layer: SkillExchangeSystem
 * Orchestrates business operations, domain collections, and exchange lifecycle.
 */
public class SkillExchangeSystem {
    private List<Student> students;
    private List<Skill> skills;
    private List<SkillRequest> requests;
    private List<SkillExchange> exchanges;
    private String currentUserId;

    public SkillExchangeSystem() {
        this.students = new ArrayList<>();
        this.skills = new ArrayList<>();
        this.requests = new ArrayList<>();
        this.exchanges = new ArrayList<>();
        this.currentUserId = "s1";
        seedDemoData();
    }

    public void seedDemoData() {
        students.clear();
        skills.clear();
        requests.clear();
        exchanges.clear();

        // 1. Initial Skills
        skills.add(new Skill("sk1", "Python", "Programming", "Programming language for data science and web development.", "🐍", "Anu", "2 hours ago"));
        skills.add(new Skill("sk2", "Figma", "Design", "UI/UX design and prototyping tool.", "🎨", "Alex", "1 day ago"));
        skills.add(new Skill("sk3", "Java", "Programming", "Object-oriented programming language.", "☕", "Rahul", "5 hours ago"));
        skills.add(new Skill("sk4", "Video Editing", "Video Editing", "Edit videos using Adobe Premiere Pro.", "🎬", "Anu", "2 hours ago"));
        skills.add(new Skill("sk5", "Photoshop", "Design", "Photo editing and graphic design.", "🖌️", "Anu", "1 day ago"));
        skills.add(new Skill("sk6", "JavaScript", "Programming", "Web development language.", "⚡", "Rahul", "2 days ago"));
        skills.add(new Skill("sk7", "Machine Learning", "Academic", "Fundamentals of ML, neural networks and predictive modeling.", "🤖", "Alex", "3 days ago"));
        skills.add(new Skill("sk8", "Digital Marketing", "Marketing", "SEO, content marketing and social media growth strategies.", "📈", "Rahul", "3 days ago"));

        // 2. Initial Students
        List<String> s1Teach = new ArrayList<>(); s1Teach.add("sk2"); s1Teach.add("sk1");
        List<String> s1Learn = new ArrayList<>(); s1Learn.add("sk3"); s1Learn.add("sk4");
        students.add(new Student("s1", "Alex", "CS2024001", "Computer Science", "2",
                "A passionate learner who loves design, coding and exploring new things. Let's exchange skills and grow together! 🚀",
                s1Teach, s1Learn, "👩‍🎓"));

        List<String> s2Teach = new ArrayList<>(); s2Teach.add("sk3"); s2Teach.add("sk1");
        List<String> s2Learn = new ArrayList<>(); s2Learn.add("sk2");
        students.add(new Student("s2", "Rahul", "CS2024002", "Computer Science", "2",
                "Enthusiastic developer focused on backend architectures and algorithms. Looking to master UI/UX.",
                s2Teach, s2Learn, "👨‍🎓"));

        List<String> s3Teach = new ArrayList<>(); s3Teach.add("sk5"); s3Teach.add("sk4");
        List<String> s3Learn = new ArrayList<>(); s3Learn.add("sk1");
        students.add(new Student("s3", "Anu", "EC2024015", "Electronics", "2",
                "Digital media and multimedia creator exploring automation with Python.",
                s3Teach, s3Learn, "👩‍💼"));

        // 3. Initial Requests
        requests.add(new SkillRequest("req1", "s1", "s2", "sk3", "Hi! I'd love to learn Java. Let me know if you're interested!", "pending", "Apr 26, 2025"));
        requests.add(new SkillRequest("req2", "s3", "s1", "sk1", "Hey Alex, can you teach me Python basics?", "accepted", "Apr 25, 2025"));
        requests.add(new SkillRequest("req3", "s2", "s1", "sk2", "Wants to learn Figma. 'I'd love to learn Figma. Let me know!'", "pending", "Apr 26, 2025"));

        // 4. Initial Exchanges
        exchanges.add(new SkillExchange("ex1", "s1", "s2", "sk3", "active", "Apr 25, 2025"));

        this.currentUserId = "s1";
    }

    public List<Student> getStudents() { return students; }
    public List<Skill> getSkills() { return skills; }
    public List<SkillRequest> getRequests() { return requests; }
    public List<SkillExchange> getExchanges() { return exchanges; }
    public String getCurrentUserId() { return currentUserId; }
    public void setCurrentUserId(String id) { this.currentUserId = id; }

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
        skills.add(0, skill);

        if (cur != null) {
            if ("teach".equalsIgnoreCase(intent)) {
                cur.addSkill(id);
            } else if ("learn".equalsIgnoreCase(intent)) {
                cur.addSkillToLearn(id);
            }
        }
        return skill;
    }

    public boolean removeSkill(String skillId) {
        skills.removeIf(s -> s.getId().equals(skillId));
        for (Student st : students) {
            st.removeSkill(skillId, "teach");
            st.removeSkill(skillId, "learn");
        }
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
        requests.add(0, req);
        return req;
    }

    public SkillExchange acceptRequest(String requestId) {
        SkillRequest req = getRequestById(requestId);
        if (req != null) {
            req.accept();
            String exId = "ex" + System.currentTimeMillis();
            SkillExchange ex = new SkillExchange(exId, req.getRequesterId(), req.getReceiverId(), req.getSkillId(), "active", "Just now");
            exchanges.add(0, ex);
            return ex;
        }
        return null;
    }

    public boolean rejectRequest(String requestId) {
        SkillRequest req = getRequestById(requestId);
        if (req != null) {
            req.reject();
            return true;
        }
        return false;
    }

    public boolean cancelRequest(String requestId) {
        return requests.removeIf(r -> r.getId().equals(requestId));
    }

    public boolean completeExchange(String exchangeId) {
        SkillExchange ex = getExchangeById(exchangeId);
        if (ex != null) {
            ex.complete();
            return true;
        }
        return false;
    }

    public boolean cancelExchange(String exchangeId) {
        SkillExchange ex = getExchangeById(exchangeId);
        if (ex != null) {
            ex.cancel();
            return true;
        }
        return false;
    }

    public boolean updateProfile(String id, String name, String department, String year, String bio) {
        Student s = getStudentById(id);
        if (s != null) {
            s.setName(name);
            s.setDepartment(department);
            s.setYear(year);
            s.setBio(bio);
            return true;
        }
        return false;
    }
}