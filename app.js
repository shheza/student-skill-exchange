/**
 * ============================================================================
 * SkillX — Student Skill Exchange System
 * College OOP Mini Project
 * Pure HTML, CSS, Vanilla JavaScript & Browser LocalStorage
 * ============================================================================
 * 
 * CORE OOP CONCEPTS DEMONSTRATED:
 * 1. Abstraction: Base class `User` defines common attributes and interface.
 * 2. Inheritance: `Student extends User` reuses base attributes with `super()`.
 * 3. Polymorphism / Method Overriding: `Student.displayProfile()` overrides
 *    `User.displayProfile()` to provide academic-specific details.
 * 4. Encapsulation: Skills collections are manipulated via dedicated methods
 *    (`addSkill`, `removeSkill`, `addSkillToLearn`).
 * 5. Constructors: Every class uses explicit constructors with initial state.
 * 6. Objects: Concrete objects are instantiated using `new ClassName()`.
 * 7. Collections: Arrays are used for `students`, `skills`, `requests`, `exchanges`.
 * 8. Object Rehydration: Deserializes LocalStorage JSON back into class instances.
 * ============================================================================
 */

// ============================================================================
// 1. BASE CLASS: User (Demonstrates Abstraction)
// ============================================================================
class User {
    /**
     * Constructor initializing fundamental user properties.
     * @param {string} id - Unique identifier
     * @param {string} name - Full name of the user
     */
    constructor(id, name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Base profile display method.
     * Intended to be overridden by derived classes (Polymorphism).
     * @returns {string} Summary string of user profile
     */
    displayProfile() {
        return `User [${this.id}]: ${this.name}`;
    }
}

// ============================================================================
// 2. SUBCLASS: Student (Demonstrates Inheritance & Method Overriding)
// ============================================================================
class Student extends User {
    /**
     * Constructor for Student extending User.
     * @param {string} id - Unique student ID
     * @param {string} name - Student's name
     * @param {string} studentId - Roll number / Campus ID
     * @param {string} department - Department/Major (e.g. Computer Science)
     * @param {string|number} year - Year of study (e.g. 2)
     * @param {string} bio - Short bio / interests
     * @param {Array<string>} skillsToTeach - Array of skill IDs the student teaches
     * @param {Array<string>} skillsToLearn - Array of skill IDs the student wants to learn
     * @param {string} avatar - Display avatar emoji
     */
    constructor(id, name, studentId, department, year, bio = '', skillsToTeach = [], skillsToLearn = [], avatar = '🎓') {
        super(id, name); // Call parent User constructor
        this.studentId = studentId;
        this.department = department;
        this.year = year;
        this.bio = bio;
        this.skillsToTeach = [...skillsToTeach];
        this.skillsToLearn = [...skillsToLearn];
        this.avatar = avatar;
    }

    /**
     * Adds a skill the student can teach (Encapsulation).
     * @param {string} skillId - ID of the skill
     */
    addSkill(skillId) {
        if (!this.skillsToTeach.includes(skillId)) {
            this.skillsToTeach.push(skillId);
            return true;
        }
        return false;
    }

    /**
     * Adds a skill the student wants to learn (Encapsulation).
     * @param {string} skillId - ID of the skill
     */
    addSkillToLearn(skillId) {
        if (!this.skillsToLearn.includes(skillId)) {
            this.skillsToLearn.push(skillId);
            return true;
        }
        return false;
    }

    /**
     * Removes a skill from either teach or learn list.
     * @param {string} skillId - ID of the skill
     * @param {string} type - 'teach' or 'learn'
     */
    removeSkill(skillId, type = 'teach') {
        if (type === 'teach') {
            this.skillsToTeach = this.skillsToTeach.filter(id => id !== skillId);
        } else if (type === 'learn') {
            this.skillsToLearn = this.skillsToLearn.filter(id => id !== skillId);
        }
    }

    /**
     * Checks if the student can teach a specific skill.
     * @param {string} skillId 
     * @returns {boolean}
     */
    canTeach(skillId) {
        return this.skillsToTeach.includes(skillId);
    }

    /**
     * Checks if the student wants to learn a specific skill.
     * @param {string} skillId 
     * @returns {boolean}
     */
    wantsToLearn(skillId) {
        return this.skillsToLearn.includes(skillId);
    }

    /**
     * Overrides User.prototype.displayProfile()
     * Demonstrates Polymorphism & Method Overriding.
     * @returns {string} Comprehensive student profile string
     */
    displayProfile() {
        return `${this.name} (${this.studentId}) - ${this.department}, Year ${this.year} | Teaches: ${this.skillsToTeach.length} skills | Learning: ${this.skillsToLearn.length} skills`;
    }
}

// ============================================================================
// 3. CLASS: Skill
// ============================================================================
class Skill {
    /**
     * Constructor for a Skill entity.
     * @param {string} id - Unique skill ID
     * @param {string} name - Skill title (e.g. Python, Figma)
     * @param {string} category - Category (e.g. Programming, Design)
     * @param {string} description - Brief summary
     * @param {string} icon - Category icon emoji
     * @param {string} addedBy - Student name who added this skill
     * @param {string} addedAt - Timestamp or relative label
     */
    constructor(id, name, category, description = '', icon = '', addedBy = 'Alex', addedAt = 'Recently') {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.icon = icon || Skill.getDefaultIcon(category);
        this.addedBy = addedBy;
        this.addedAt = addedAt;
    }

    /**
     * Helper to get a recognizable emoji icon based on category.
     * @param {string} category 
     * @returns {string}
     */
    static getDefaultIcon(category) {
        switch (category) {
            case 'Programming': return '💻';
            case 'Design': return '🎨';
            case 'Languages': return '🗣️';
            case 'Marketing': return '📈';
            case 'Photography': return '📷';
            case 'Video Editing': return '🎬';
            case 'Academic': return '📚';
            default: return '💡';
        }
    }
}

// ============================================================================
// 4. CLASS: SkillRequest
// ============================================================================
class SkillRequest {
    /**
     * Constructor for a skill exchange request between two students.
     * @param {string} id - Unique request ID
     * @param {string} requesterId - Student requesting to learn
     * @param {string} receiverId - Student who can teach
     * @param {string} skillId - Skill requested
     * @param {string} message - Proposal message
     * @param {string} status - 'pending' | 'accepted' | 'rejected'
     * @param {string} createdAt - Date formatted string
     */
    constructor(id, requesterId, receiverId, skillId, message = '', status = 'pending', createdAt = null) {
        this.id = id;
        this.requesterId = requesterId;
        this.receiverId = receiverId;
        this.skillId = skillId;
        this.message = message;
        this.status = status;
        this.createdAt = createdAt || new Date().toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
    }

    /**
     * Accepts the request.
     */
    accept() {
        this.status = 'accepted';
    }

    /**
     * Rejects the request.
     */
    reject() {
        this.status = 'rejected';
    }
}

// ============================================================================
// 5. CLASS: SkillExchange
// ============================================================================
class SkillExchange {
    /**
     * Constructor for an active peer-to-peer exchange session.
     * @param {string} id - Unique exchange ID
     * @param {string} student1Id - Initiator or participant 1
     * @param {string} student2Id - Participant 2
     * @param {string} skillId - Skill being exchanged
     * @param {string} status - 'active' | 'completed' | 'cancelled'
     * @param {string} startedAt - Formatted start date
     */
    constructor(id, student1Id, student2Id, skillId, status = 'active', startedAt = null) {
        this.id = id;
        this.student1Id = student1Id;
        this.student2Id = student2Id;
        this.skillId = skillId;
        this.status = status;
        this.startedAt = startedAt || new Date().toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
    }

    /**
     * Activates an exchange.
     */
    activate() {
        this.status = 'active';
    }

    /**
     * Marks exchange as completed.
     */
    complete() {
        this.status = 'completed';
    }

    /**
     * Cancels the exchange.
     */
    cancel() {
        this.status = 'cancelled';
    }
}
// ============================================================================
// 6. CLASS: StorageManager (Local Storage Wrapper)
// ============================================================================
class StorageManager {
    static PREFIX = 'skillx_';

    static save(key, data) {
        try {
            localStorage.setItem(this.PREFIX + key, JSON.stringify(data));
        } catch (e) {
            console.error('StorageManager Save Error:', e);
        }
    }

    static get(key) {
        try {
            const raw = localStorage.getItem(this.PREFIX + key);
            return raw ? JSON.parse(raw) : null;
        } catch (e) {
            console.error('StorageManager Get Error:', e);
            return null;
        }
    }

    static remove(key) {
        localStorage.removeItem(this.PREFIX + key);
    }

    static clear() {
        const keysToRemove = [];
        for (let i = 0; i < localStorage.length; i++) {
            const k = localStorage.key(i);
            if (k && k.startsWith(this.PREFIX)) {
                keysToRemove.push(k);
            }
        }
        keysToRemove.forEach(k => localStorage.removeItem(k));
    }
}

// ============================================================================
// 7. CLASS: SkillExchangeSystem (Main Application Orchestrator)
// ============================================================================
class SkillExchangeSystem {
    constructor() {
        this.students = [];
        this.skills = [];
        this.requests = [];
        this.exchanges = [];
        this.currentUserId = 's1'; // Defaults to Alex
        this.storage = StorageManager;
        this.bridge = null;
    }

    setBridge(bridge) {
        this.bridge = bridge;
    }

    init() {
        const storedStudents = this.storage.get('students');
        if (!storedStudents || storedStudents.length === 0) {
            this.seedDemoData();
        } else {
            this.loadAndRehydrate();
        }
    }

    seedDemoData() {
        this.skills = [
            new Skill('sk1', 'Python', 'Programming', 'Programming language for data science and web development.', '🐍', 'Anu', '2 hours ago'),
            new Skill('sk2', 'Figma', 'Design', 'UI/UX design and prototyping tool.', '🎨', 'Alex', '1 day ago'),
            new Skill('sk3', 'Java', 'Programming', 'Object-oriented programming language.', '☕', 'Rahul', '5 hours ago'),
            new Skill('sk4', 'Video Editing', 'Video Editing', 'Edit videos using Adobe Premiere Pro.', '🎬', 'Anu', '2 hours ago'),
            new Skill('sk5', 'Photoshop', 'Design', 'Photo editing and graphic design.', '🖌️', 'Anu', '1 day ago'),
            new Skill('sk6', 'JavaScript', 'Programming', 'Web development language.', '⚡', 'Rahul', '2 days ago'),
            new Skill('sk7', 'Machine Learning', 'Academic', 'Fundamentals of ML, neural networks and predictive modeling.', '🤖', 'Alex', '3 days ago'),
            new Skill('sk8', 'Digital Marketing', 'Marketing', 'SEO, content marketing and social media growth strategies.', '📈', 'Rahul', '3 days ago')
        ];

        this.students = [
            new Student(
                's1',
                'Alex',
                'CS2024001',
                'Computer Science',
                '2',
                'A passionate learner who loves design, coding and exploring new things. Let\'s exchange skills and grow together! 🚀',
                ['sk2', 'sk1'],
                ['sk3', 'sk4'],
                '👩‍🎓'
            ),
            new Student(
                's2',
                'Rahul',
                'CS2024002',
                'Computer Science',
                '2',
                'Enthusiastic developer focused on backend architectures and algorithms. Looking to master UI/UX.',
                ['sk3', 'sk1'],
                ['sk2'],
                '👨‍🎓'
            ),
            new Student(
                's3',
                'Anu',
                'EC2024015',
                'Electronics',
                '2',
                'Digital media and multimedia creator exploring automation with Python.',
                ['sk5', 'sk4'],
                ['sk1'],
                '👩‍💼'
            )
        ];

        this.requests = [
            new SkillRequest('req1', 's1', 's2', 'sk3', "Hi! I'd love to learn Java. Let me know if you're interested!", 'pending', 'Apr 26, 2025'),
            new SkillRequest('req2', 's3', 's1', 'sk1', "Hey Alex, can you teach me Python basics?", 'accepted', 'Apr 25, 2025'),
            new SkillRequest('req3', 's2', 's1', 'sk2', "Wants to learn Figma. 'I\'d love to learn Figma. Let me know!'", 'pending', 'Apr 26, 2025')
        ];

        this.exchanges = [
            new SkillExchange('ex1', 's1', 's2', 'sk3', 'active', 'Apr 25, 2025')
        ];

        this.currentUserId = 's1';
        this.saveAll();
    }

    loadAndRehydrate() {
        const rawSkills = this.storage.get('skills') || [];
        this.skills = rawSkills.map(s => new Skill(s.id, s.name, s.category, s.description, s.icon, s.addedBy, s.addedAt));

        const rawStudents = this.storage.get('students') || [];
        this.students = rawStudents.map(st => new Student(st.id, st.name, st.studentId, st.department, st.year, st.bio, st.skillsToTeach, st.skillsToLearn, st.avatar));

        const rawRequests = this.storage.get('requests') || [];
        this.requests = rawRequests.map(r => new SkillRequest(r.id, r.requesterId, r.receiverId, r.skillId, r.message, r.status, r.createdAt));

        const rawExchanges = this.storage.get('exchanges') || [];
        this.exchanges = rawExchanges.map(e => new SkillExchange(e.id, e.student1Id, e.student2Id, e.skillId, e.status, e.startedAt));

        // Auto-migrate legacy Sheza name in LocalStorage if present
        let migrated = false;
        this.students.forEach(st => {
            if (st.name === 'Sheza Rahman' || st.name === 'Sheza') {
                st.name = 'Alex';
                migrated = true;
            }
        });
        this.skills.forEach(sk => {
            if (sk.addedBy === 'Sheza Rahman' || sk.addedBy === 'Sheza') {
                sk.addedBy = 'Alex';
                migrated = true;
            }
        });
        this.requests.forEach(r => {
            if (r.message && r.message.includes('Sheza')) {
                r.message = r.message.replace(/Sheza/g, 'Alex');
                migrated = true;
            }
        });

        const savedUser = this.storage.get('currentUser');
        if (savedUser && this.students.some(s => s.id === savedUser)) {
            this.currentUserId = savedUser;
        } else if (this.students.length > 0) {
            this.currentUserId = this.students[0].id;
        }

        if (migrated) {
            this.saveAll();
        }
    }

    saveAll() {
        this.storage.save('skills', this.skills);
        this.storage.save('students', this.students);
        this.storage.save('requests', this.requests);
        this.storage.save('exchanges', this.exchanges);
        this.storage.save('currentUser', this.currentUserId);
    }

    getCurrentStudent() {
        return this.students.find(s => s.id === this.currentUserId) || this.students[0];
    }

    setCurrentStudent(studentId) {
        if (this.students.some(s => s.id === studentId)) {
            this.currentUserId = studentId;
            this.storage.save('currentUser', this.currentUserId);
            if (this.bridge) this.bridge.switchUser(studentId);
        }
    }

    getStudentById(id) {
        return this.students.find(s => s.id === id);
    }

    getSkillById(id) {
        return this.skills.find(s => s.id === id);
    }

    registerStudent(name, studentId, department, year, bio = '') {
        const id = 's' + (Date.now());
        const newStudent = new Student(id, name, studentId, department, year, bio, [], [], '🎓');
        this.students.push(newStudent);
        this.saveAll();
        return newStudent;
    }

    updateStudentProfile(id, name, department, year, bio) {
        const student = this.getStudentById(id);
        if (student) {
            student.name = name;
            student.department = department;
            student.year = year;
            student.bio = bio;
            this.saveAll();
            if (this.bridge) this.bridge.updateProfile(id, name, department, year, bio);
            return true;
        }
        return false;
    }

    addSkill(name, category, description, intent = 'catalog') {
        const id = 'sk' + Date.now();
        const currentStudent = this.getCurrentStudent();
        const newSkill = new Skill(id, name, category, description, '', currentStudent ? currentStudent.name : 'Student', 'Just now');
        this.skills.unshift(newSkill);

        if (intent === 'teach' && currentStudent) {
            currentStudent.addSkill(id);
        } else if (intent === 'learn' && currentStudent) {
            currentStudent.addSkillToLearn(id);
        }

        this.saveAll();
        if (this.bridge) this.bridge.addSkill(name, category, description, intent);
        return newSkill;
    }

    removeSkill(skillId) {
        this.skills = this.skills.filter(s => s.id !== skillId);
        this.students.forEach(st => {
            st.removeSkill(skillId, 'teach');
            st.removeSkill(skillId, 'learn');
        });
        this.saveAll();
        if (this.bridge) this.bridge.removeSkill(skillId);
    }

    searchSkills(query = '', category = 'All') {
        const cleanQuery = query.toLowerCase().trim();
        const currentStudent = this.getCurrentStudent();

        const matchingSkills = this.skills.filter(skill => {
            const matchesText = !cleanQuery || skill.name.toLowerCase().includes(cleanQuery) || skill.description.toLowerCase().includes(cleanQuery);
            const matchesCat = category === 'All' || skill.category === category;
            return matchesText && matchesCat;
        });

        const results = [];
        matchingSkills.forEach(skill => {
            this.students.forEach(student => {
                if (student.id !== currentStudent.id && student.canTeach(skill.id)) {
                    const exists = results.some(r => r.student.id === student.id && r.skill.id === skill.id);
                    if (!exists) {
                        results.push({ student, skill });
                    }
                }
            });
        });

        return results;
    }

    sendRequest(receiverId, skillId, message = '') {
        const currentStudent = this.getCurrentStudent();
        if (receiverId === currentStudent.id) {
            throw new Error("You cannot send an exchange request to yourself.");
        }

        const existing = this.requests.find(r => 
            r.requesterId === currentStudent.id && 
            r.receiverId === receiverId && 
            r.skillId === skillId && 
            r.status === 'pending'
        );

        if (existing) {
            throw new Error("A pending request for this skill already exists with this student.");
        }

        const id = 'req' + Date.now();
        const request = new SkillRequest(id, currentStudent.id, receiverId, skillId, message, 'pending');
        this.requests.unshift(request);
        this.saveAll();
        if (this.bridge) this.bridge.sendRequest(receiverId, skillId, message);
        return request;
    }

    acceptRequest(requestId) {
        const request = this.requests.find(r => r.id === requestId);
        if (!request) return null;

        request.accept();
        const exchange = this.createExchange(request.requesterId, request.receiverId, request.skillId);
        this.saveAll();
        if (this.bridge) this.bridge.acceptRequest(requestId);
        return exchange;
    }

    rejectRequest(requestId) {
        const request = this.requests.find(r => r.id === requestId);
        if (request) {
            request.reject();
            this.saveAll();
            if (this.bridge) this.bridge.rejectRequest(requestId);
            return true;
        }
        return false;
    }

    cancelRequest(requestId) {
        this.requests = this.requests.filter(r => r.id !== requestId);
        this.saveAll();
        if (this.bridge) this.bridge.cancelRequest(requestId);
    }

    createExchange(student1Id, student2Id, skillId) {
        const id = 'ex' + Date.now();
        const exchange = new SkillExchange(id, student1Id, student2Id, skillId, 'active');
        this.exchanges.unshift(exchange);
        this.saveAll();
        return exchange;
    }

    completeExchange(exchangeId) {
        const exchange = this.exchanges.find(e => e.id === exchangeId);
        if (exchange) {
            exchange.complete();
            this.saveAll();
            if (this.bridge) this.bridge.completeExchange(exchangeId);
            return true;
        }
        return false;
    }

    cancelExchange(exchangeId) {
        const exchange = this.exchanges.find(e => e.id === exchangeId);
        if (exchange) {
            exchange.cancel();
            this.saveAll();
            if (this.bridge) this.bridge.cancelExchange(exchangeId);
            return true;
        }
        return false;
    }

    getActiveExchanges() {
        return this.exchanges.filter(e => e.status === 'active');
    }

    getSentRequests() {
        const cur = this.getCurrentStudent();
        return this.requests.filter(r => r.requesterId === cur.id);
    }

    getReceivedRequests() {
        const cur = this.getCurrentStudent();
        return this.requests.filter(r => r.receiverId === cur.id);
    }

    resetDemoData() {
        this.storage.clear();
        this.seedDemoData();
        if (this.bridge) this.bridge.resetData();
    }
}
// ============================================================================
// 8. UI CONTROLLER (Presentation Layer & DOM Event Handling)
// ============================================================================
class UIController {
    constructor(system) {
        this.system = system;
        this.currentView = 'dashboard';
        this.activeCategoryFilter = 'All';
        this.activeExchangeTab = 'active';

        this.initDOMElements();
        this.bindEvents();
        this.renderAll();
    }

    initDOMElements() {
        this.navLinks = document.querySelectorAll('.nav-link, [data-view]');
        this.viewPanels = document.querySelectorAll('.view-panel');
        this.navExchangesBadge = document.getElementById('nav-exchanges-badge');

        this.activeUserSelect = document.getElementById('active-user-select');
        this.headerUserAvatar = document.getElementById('header-user-avatar');
        this.sidebar = document.getElementById('sidebar');
        this.sidebarToggle = document.getElementById('sidebar-toggle');

        this.dashUserName = document.getElementById('dash-user-name');
        this.metricStudents = document.getElementById('metric-students');
        this.metricSkills = document.getElementById('metric-skills');
        this.metricRequests = document.getElementById('metric-requests');
        this.metricExchanges = document.getElementById('metric-exchanges');
        this.dashRecentSkillsList = document.getElementById('dash-recent-skills-list');

        this.studentsGrid = document.getElementById('students-grid');
        this.studentsSearchInput = document.getElementById('students-search-input');

        this.skillsGrid = document.getElementById('skills-grid');
        this.skillsCategoryFilters = document.getElementById('skills-category-filters');

        this.searchSkillInput = document.getElementById('search-skill-input');
        this.searchCategoryFilter = document.getElementById('search-category-filter');
        this.searchResultsCount = document.getElementById('search-results-count');
        this.searchResultsList = document.getElementById('search-results-list');

        this.tabBtns = document.querySelectorAll('[data-exchange-tab]');
        this.tabContentActive = document.getElementById('tab-content-active');
        this.tabContentSent = document.getElementById('tab-content-sent');
        this.tabContentReceived = document.getElementById('tab-content-received');
        this.sentRequestsTbody = document.getElementById('sent-requests-tbody');
        this.receivedRequestsList = document.getElementById('received-requests-list');
        this.tabReceivedBadge = document.getElementById('tab-received-badge');

        this.exchangesList = document.getElementById('exchanges-list');

        this.myProfileAvatar = document.getElementById('my-profile-avatar');
        this.myProfileName = document.getElementById('my-profile-name');
        this.myProfileStudentId = document.getElementById('my-profile-student-id');
        this.myProfileDept = document.getElementById('my-profile-dept');
        this.myProfileYear = document.getElementById('my-profile-year');
        this.myProfileBioText = document.getElementById('my-profile-bio-text');
        this.mySkillsTeachContainer = document.getElementById('my-skills-teach-container');
        this.mySkillsLearnContainer = document.getElementById('my-skills-learn-container');

        this.modalSendRequest = document.getElementById('modal-send-request');
        this.formSendRequest = document.getElementById('form-send-request');
        this.reqTargetStudentSelect = document.getElementById('req-target-student-select');
        this.reqSkillSelect = document.getElementById('req-skill-select');
        this.reqMessageInput = document.getElementById('req-message-input');

        this.modalAddSkill = document.getElementById('modal-add-skill');
        this.formAddSkill = document.getElementById('form-add-skill');
        this.skillNameInput = document.getElementById('skill-name-input');
        this.skillCategoryInput = document.getElementById('skill-category-input');
        this.skillIntentInput = document.getElementById('skill-intent-input');
        this.skillDescInput = document.getElementById('skill-desc-input');

        this.modalEditProfile = document.getElementById('modal-edit-profile');
        this.formEditProfile = document.getElementById('form-edit-profile');
        this.editProfileName = document.getElementById('edit-profile-name');
        this.editProfileDept = document.getElementById('edit-profile-dept');
        this.editProfileYear = document.getElementById('edit-profile-year');
        this.editProfileBio = document.getElementById('edit-profile-bio');

        this.modalViewStudent = document.getElementById('modal-view-student');
        this.viewStudentAvatar = document.getElementById('view-student-avatar');
        this.viewStudentName = document.getElementById('view-student-name');
        this.viewStudentMeta = document.getElementById('view-student-meta');
        this.viewStudentId = document.getElementById('view-student-id');
        this.viewStudentBio = document.getElementById('view-student-bio');
        this.viewStudentTeachSkills = document.getElementById('view-student-teach-skills');
        this.viewStudentLearnSkills = document.getElementById('view-student-learn-skills');
        this.btnModalRequestExchange = document.getElementById('btn-modal-request-exchange');

        this.modalResetDemo = document.getElementById('modal-reset-demo');
        this.btnConfirmResetDemo = document.getElementById('btn-confirm-reset-demo');
        this.btnResetDemoModal = document.getElementById('btn-reset-demo-modal');

        this.modalDeleteSkill = document.getElementById('modal-delete-skill');
        this.deleteSkillTargetName = document.getElementById('delete-skill-target-name');
        this.btnConfirmDeleteSkill = document.getElementById('btn-confirm-delete-skill');
        this.pendingDeleteSkillId = null;

        this.toastContainer = document.getElementById('toast-container');
    }

    bindEvents() {
        document.addEventListener('click', (e) => {
            const navBtn = e.target.closest('[data-view]');
            if (navBtn) {
                const targetView = navBtn.getAttribute('data-view');
                if (targetView) {
                    this.switchView(targetView);
                    if (window.innerWidth <= 768) {
                        this.sidebar.classList.remove('open');
                    }
                }
            }
        });

        if (this.sidebarToggle) {
            this.sidebarToggle.addEventListener('click', () => {
                this.sidebar.classList.toggle('open');
            });
        }

        this.activeUserSelect.addEventListener('change', (e) => {
            this.system.setCurrentStudent(e.target.value);
            const current = this.system.getCurrentStudent();
            this.showToast(`Switched active profile to ${current.name}`, 'info');
            this.renderAll();
        });

        const btnQuickAddSkill = document.getElementById('btn-quick-add-skill');
        if (btnQuickAddSkill) {
            btnQuickAddSkill.addEventListener('click', () => this.openAddSkillModal());
        }

        const btnOpenAddSkillModal = document.getElementById('btn-open-add-skill-modal');
        if (btnOpenAddSkillModal) {
            btnOpenAddSkillModal.addEventListener('click', () => this.openAddSkillModal());
        }

        if (this.skillsCategoryFilters) {
            this.skillsCategoryFilters.addEventListener('click', (e) => {
                if (e.target.classList.contains('pill-btn')) {
                    this.skillsCategoryFilters.querySelectorAll('.pill-btn').forEach(btn => btn.classList.remove('active'));
                    e.target.classList.add('active');
                    this.activeCategoryFilter = e.target.getAttribute('data-category');
                    this.renderSkills();
                }
            });
        }

        if (this.studentsSearchInput) {
            this.studentsSearchInput.addEventListener('input', () => this.renderStudents());
        }

        if (this.searchSkillInput) {
            this.searchSkillInput.addEventListener('input', () => this.renderSearchSkills());
        }
        if (this.searchCategoryFilter) {
            this.searchCategoryFilter.addEventListener('change', () => this.renderSearchSkills());
        }

        document.querySelectorAll('[data-exchange-tab]').forEach(btn => {
            btn.addEventListener('click', () => {
                const tab = btn.getAttribute('data-exchange-tab');
                this.switchExchangeTab(tab);
            });
        });

        const btnTeach = document.getElementById('btn-profile-add-teach-skill');
        if (btnTeach) {
            btnTeach.addEventListener('click', () => this.openAddSkillModal('teach'));
        }
        const btnLearn = document.getElementById('btn-profile-add-learn-skill');
        if (btnLearn) {
            btnLearn.addEventListener('click', () => this.openAddSkillModal('learn'));
        }

        const btnEditProfile = document.getElementById('btn-open-edit-profile');
        if (btnEditProfile) {
            btnEditProfile.addEventListener('click', () => this.openEditProfileModal());
        }
        const btnEditBio = document.getElementById('btn-profile-edit-bio');
        if (btnEditBio) {
            btnEditBio.addEventListener('click', () => this.openEditProfileModal());
        }

        if (this.btnResetDemoModal) {
            this.btnResetDemoModal.addEventListener('click', () => {
                this.modalResetDemo.classList.remove('hidden');
            });
        }

        if (this.btnConfirmResetDemo) {
            this.btnConfirmResetDemo.addEventListener('click', () => {
                this.system.resetDemoData();
                this.modalResetDemo.classList.add('hidden');
                this.showToast('Demo data reset successfully!', 'success');
                this.renderAll();
            });
        }

        if (this.btnConfirmDeleteSkill) {
            this.btnConfirmDeleteSkill.addEventListener('click', () => {
                if (this.pendingDeleteSkillId) {
                    const idToDelete = this.pendingDeleteSkillId;
                    this.pendingDeleteSkillId = null;
                    if (this.modalDeleteSkill) {
                        this.modalDeleteSkill.classList.add('hidden');
                    }
                    this.system.removeSkill(idToDelete);
                    this.showToast('Skill deleted.', 'info');
                    this.renderAll();
                }
            });
        }

        document.querySelectorAll('[data-close-modal]').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const overlay = e.target.closest('.modal-overlay');
                if (overlay) {
                    overlay.classList.add('hidden');
                    if (overlay === this.modalDeleteSkill) {
                        this.pendingDeleteSkillId = null;
                    }
                }
            });
        });

        document.querySelectorAll('.modal-overlay').forEach(modal => {
            modal.addEventListener('click', (e) => {
                if (e.target === modal) {
                    modal.classList.add('hidden');
                    if (modal === this.modalDeleteSkill) {
                        this.pendingDeleteSkillId = null;
                    }
                }
            });
        });

        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape') {
                if (this.modalDeleteSkill && !this.modalDeleteSkill.classList.contains('hidden')) {
                    this.modalDeleteSkill.classList.add('hidden');
                    this.pendingDeleteSkillId = null;
                }
            }
        });

        if (this.formAddSkill) {
            this.formAddSkill.addEventListener('submit', (e) => {
                e.preventDefault();
                const name = this.skillNameInput.value.trim();
                const category = this.skillCategoryInput.value;
                const intent = this.skillIntentInput.value;
                const desc = this.skillDescInput.value.trim();

                if (!name) return;

                this.system.addSkill(name, category, desc, intent);
                this.formAddSkill.reset();
                this.modalAddSkill.classList.add('hidden');
                this.showToast(`Skill "${name}" added successfully!`, 'success');
                this.renderAll();
            });
        }

        if (this.formSendRequest) {
            this.formSendRequest.addEventListener('submit', (e) => {
                e.preventDefault();
                const targetStudentId = this.reqTargetStudentSelect.value;
                const skillId = this.reqSkillSelect.value;
                const message = this.reqMessageInput.value.trim();

                try {
                    this.system.sendRequest(targetStudentId, skillId, message);
                    this.modalSendRequest.classList.add('hidden');
                    this.formSendRequest.reset();
                    const targetStudent = this.system.getStudentById(targetStudentId);
                    this.showToast(`Exchange request sent to ${targetStudent ? targetStudent.name : 'student'}!`, 'success');
                    this.renderAll();
                    this.switchView('exchanges');
                    this.switchExchangeTab('sent');
                } catch (err) {
                    alert(err.message);
                }
            });
        }

        if (this.formEditProfile) {
            this.formEditProfile.addEventListener('submit', (e) => {
                e.preventDefault();
                const cur = this.system.getCurrentStudent();
                const name = this.editProfileName.value.trim();
                const dept = this.editProfileDept.value.trim();
                const year = this.editProfileYear.value;
                const bio = this.editProfileBio.value.trim();

                this.system.updateStudentProfile(cur.id, name, dept, year, bio);
                this.modalEditProfile.classList.add('hidden');
                this.showToast('Profile updated successfully!', 'success');
                this.renderAll();
            });
        }

        document.addEventListener('click', (e) => {
            const cancelBtn = e.target.closest('[data-cancel-request]');
            if (cancelBtn) {
                const reqId = cancelBtn.getAttribute('data-cancel-request');
                if (confirm('Are you sure you want to cancel this request?')) {
                    this.system.cancelRequest(reqId);
                    this.showToast('Request cancelled.', 'info');
                    this.renderAll();
                }
            }

            const acceptBtn = e.target.closest('[data-accept-request]');
            if (acceptBtn) {
                const reqId = acceptBtn.getAttribute('data-accept-request');
                this.system.acceptRequest(reqId);
                this.showToast('Request accepted! Active exchange created.', 'success');
                this.renderAll();
                this.switchView('exchanges');
                this.switchExchangeTab('active');
            }

            const rejectBtn = e.target.closest('[data-reject-request]');
            if (rejectBtn) {
                const reqId = rejectBtn.getAttribute('data-reject-request');
                this.system.rejectRequest(reqId);
                this.showToast('Request rejected.', 'info');
                this.renderAll();
            }

            const completeExchangeBtn = e.target.closest('[data-complete-exchange]');
            if (completeExchangeBtn) {
                const exId = completeExchangeBtn.getAttribute('data-complete-exchange');
                this.system.completeExchange(exId);
                this.showToast('Exchange marked as completed!', 'success');
                this.renderAll();
            }

            const cancelExchangeBtn = e.target.closest('[data-cancel-exchange]');
            if (cancelExchangeBtn) {
                const exId = cancelExchangeBtn.getAttribute('data-cancel-exchange');
                if (confirm('Cancel this active exchange?')) {
                    this.system.cancelExchange(exId);
                    this.showToast('Exchange cancelled.', 'info');
                    this.renderAll();
                }
            }

            const deleteSkillBtn = e.target.closest('[data-delete-skill]');
            if (deleteSkillBtn) {
                const skillId = deleteSkillBtn.getAttribute('data-delete-skill');
                const skill = this.system.getSkillById(skillId);
                if (skill) {
                    this.openDeleteSkillModal(skillId, skill.name);
                }
            }

            const removeMySkillBtn = e.target.closest('[data-remove-my-skill]');
            if (removeMySkillBtn) {
                const skillId = removeMySkillBtn.getAttribute('data-remove-my-skill');
                const type = removeMySkillBtn.getAttribute('data-skill-type');
                const cur = this.system.getCurrentStudent();
                cur.removeSkill(skillId, type);
                this.system.saveAll();
                this.showToast('Skill removed from profile.', 'info');
                this.renderAll();
            }

            const viewStudentBtn = e.target.closest('[data-view-student-id]');
            if (viewStudentBtn) {
                const studentId = viewStudentBtn.getAttribute('data-view-student-id');
                this.openViewStudentModal(studentId);
            }

            const reqExchangeBtn = e.target.closest('[data-open-request-modal]');
            if (reqExchangeBtn) {
                const targetStudentId = reqExchangeBtn.getAttribute('data-student-id');
                const skillId = reqExchangeBtn.getAttribute('data-skill-id');
                this.openSendRequestModal(targetStudentId, skillId);
            }
        });
    }

    switchView(viewName) {
        this.currentView = viewName;
        document.querySelectorAll('.sidebar .nav-link').forEach(link => {
            if (link.getAttribute('data-view') === viewName) {
                link.classList.add('active');
            } else {
                link.classList.remove('active');
            }
        });

        this.viewPanels.forEach(panel => {
            if (panel.id === `view-${viewName}`) {
                panel.classList.add('active');
            } else {
                panel.classList.remove('active');
            }
        });

        window.scrollTo({ top: 0, behavior: 'smooth' });
    }

    switchExchangeTab(tabName) {
        this.activeExchangeTab = tabName;
        document.querySelectorAll('[data-exchange-tab]').forEach(btn => {
            btn.classList.toggle('active', btn.getAttribute('data-exchange-tab') === tabName);
        });
        if (this.tabContentActive) {
            this.tabContentActive.classList.toggle('active', tabName === 'active');
        }
        if (this.tabContentReceived) {
            this.tabContentReceived.classList.toggle('active', tabName === 'received');
        }
        if (this.tabContentSent) {
            this.tabContentSent.classList.toggle('active', tabName === 'sent');
        }
    }

    showToast(message, type = 'info') {
        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;
        const icon = type === 'success' ? '✓' : (type === 'error' ? '⚠️' : 'ℹ️');
        toast.innerHTML = `<span>${icon}</span> <span>${message}</span>`;
        this.toastContainer.appendChild(toast);

        setTimeout(() => {
            toast.style.opacity = '0';
            setTimeout(() => toast.remove(), 300);
        }, 3000);
    }
    renderAll() {
        this.renderUserSwitcher();
        this.renderDashboard();
        this.renderStudents();
        this.renderSkills();
        this.renderSearchSkills();
        this.renderRequests();
        this.renderExchanges();
        this.renderProfile();
    }

    renderUserSwitcher() {
        const currentStudent = this.system.getCurrentStudent();
        this.activeUserSelect.innerHTML = this.system.students.map(s => `
            <option value="${s.id}" ${s.id === currentStudent.id ? 'selected' : ''}>
                ${s.name} ${s.id === currentStudent.id ? '(You)' : ''}
            </option>
        `).join('');

        this.headerUserAvatar.textContent = currentStudent.avatar || '👩‍🎓';
    }

    renderDashboard() {
        const current = this.system.getCurrentStudent();
        this.dashUserName.textContent = current.name.split(' ')[0];

        this.metricStudents.textContent = this.system.students.length;
        this.metricSkills.textContent = this.system.skills.length;

        const pendingRequests = this.system.requests.filter(r => r.status === 'pending');
        this.metricRequests.textContent = pendingRequests.length;

        const activeExchanges = this.system.getActiveExchanges();
        this.metricExchanges.textContent = activeExchanges.length;

        const receivedPending = this.system.getReceivedRequests().filter(r => r.status === 'pending');
        if (receivedPending.length > 0) {
            if (this.navExchangesBadge) {
                this.navExchangesBadge.textContent = receivedPending.length;
                this.navExchangesBadge.classList.remove('hidden');
            }
            if (this.tabReceivedBadge) {
                this.tabReceivedBadge.textContent = receivedPending.length;
                this.tabReceivedBadge.classList.remove('hidden');
            }
        } else {
            if (this.navExchangesBadge) this.navExchangesBadge.classList.add('hidden');
            if (this.tabReceivedBadge) this.tabReceivedBadge.classList.add('hidden');
        }

        const recent = this.system.skills.slice(0, 4);
        if (recent.length === 0) {
            this.dashRecentSkillsList.innerHTML = '<p class="text-muted text-sm">No skills added yet.</p>';
        } else {
            this.dashRecentSkillsList.innerHTML = recent.map(sk => `
                <div class="recent-skill-item">
                    <div class="recent-skill-left">
                        <div class="recent-skill-badge-icon">${sk.icon}</div>
                        <div>
                            <div class="recent-skill-name">${sk.name}</div>
                            <div class="recent-skill-category">${sk.category}</div>
                        </div>
                    </div>
                    <div class="recent-skill-right">
                        <div class="recent-skill-addedby">Added by ${sk.addedBy}</div>
                        <div class="recent-skill-time">${sk.addedAt}</div>
                    </div>
                </div>
            `).join('');
        }
    }

    renderStudents() {
        if (!this.studentsGrid) return;
        const query = this.studentsSearchInput ? this.studentsSearchInput.value.toLowerCase().trim() : '';
        const currentStudent = this.system.getCurrentStudent();

        const filtered = this.system.students.filter(st => {
            if (!query) return true;
            return st.name.toLowerCase().includes(query) || 
                   st.department.toLowerCase().includes(query) ||
                   st.studentId.toLowerCase().includes(query);
        });

        if (filtered.length === 0) {
            this.studentsGrid.innerHTML = `
                <div class="empty-state" style="grid-column: 1 / -1;">
                    <span class="empty-state-icon">👥</span>
                    <p>No students match your search.</p>
                </div>
            `;
            return;
        }

        this.studentsGrid.innerHTML = filtered.map(st => {
            const isMe = st.id === currentStudent.id;
            
            const teachBadges = st.skillsToTeach.map(skId => {
                const sk = this.system.getSkillById(skId);
                return sk ? `<span class="badge-pill badge-teach">${sk.name}</span>` : '';
            }).join('') || '<span class="text-muted text-sm">None listed</span>';

            const learnBadges = st.skillsToLearn.map(skId => {
                const sk = this.system.getSkillById(skId);
                return sk ? `<span class="badge-pill badge-learn">${sk.name}</span>` : '';
            }).join('') || '<span class="text-muted text-sm">None listed</span>';

            return `
                <div class="student-card">
                    <div class="student-card-header">
                        <div class="student-avatar">${st.avatar}</div>
                        <div class="student-info">
                            <div class="student-name-row">
                                <h3 class="student-card-name">${st.name}</h3>
                                ${isMe ? '<span class="you-badge">You</span>' : ''}
                            </div>
                            <div class="student-card-meta">${st.department} • Year ${st.year}</div>
                        </div>
                    </div>

                    <div class="student-skills-section">
                        <span class="section-label-tiny">Can Teach</span>
                        <div class="skill-pills-wrap">${teachBadges}</div>
                    </div>

                    <div class="student-skills-section">
                        <span class="section-label-tiny">Wants to Learn</span>
                        <div class="skill-pills-wrap">${learnBadges}</div>
                    </div>

                    <div class="student-card-footer">
                        <button class="btn btn-secondary btn-block" data-view-student-id="${st.id}">
                            View Profile
                        </button>
                    </div>
                </div>
            `;
        }).join('');
    }

    renderSkills() {
        const current = this.system.getCurrentStudent();
        const filtered = this.system.skills.filter(sk => {
            return this.activeCategoryFilter === 'All' || sk.category === this.activeCategoryFilter;
        });

        if (filtered.length === 0) {
            this.skillsGrid.innerHTML = `
                <div class="empty-state" style="grid-column: 1 / -1;">
                    <span class="empty-state-icon">💡</span>
                    <p>No skills found in this category.</p>
                </div>
            `;
            return;
        }

        this.skillsGrid.innerHTML = filtered.map(sk => {
            const teaches = current.canTeach(sk.id);
            const learns = current.wantsToLearn(sk.id);

            let statusBadge = '';
            if (teaches) {
                statusBadge = '<span class="badge-pill badge-teach">Can Teach</span>';
            } else if (learns) {
                statusBadge = '<span class="badge-pill badge-learn">Want to Learn</span>';
            } else {
                statusBadge = '<span class="badge-pill badge-neutral">Available</span>';
            }

            return `
                <div class="skill-card">
                    <div class="skill-card-top">
                        <div class="skill-card-title-group">
                            <div class="skill-card-icon">${sk.icon}</div>
                            <div>
                                <h3 class="skill-card-name">${sk.name}</h3>
                                <div class="skill-card-category">${sk.category}</div>
                            </div>
                        </div>
                        <button class="skill-delete-btn" data-delete-skill="${sk.id}" title="Delete skill">✕</button>
                    </div>

                    <p class="skill-card-desc">${sk.description || 'No description provided.'}</p>

                    <div class="skill-card-bottom">
                        ${statusBadge}
                    </div>
                </div>
            `;
        }).join('');
    }

    renderSearchSkills() {
        const query = this.searchSkillInput ? this.searchSkillInput.value.trim() : '';
        const category = this.searchCategoryFilter ? this.searchCategoryFilter.value : 'All';

        const results = this.system.searchSkills(query, category);
        this.searchResultsCount.textContent = `${results.length} student${results.length === 1 ? '' : 's'} found`;

        if (results.length === 0) {
            this.searchResultsList.innerHTML = `
                <div class="empty-state content-card text-center">
                    <span class="empty-state-icon">🔍</span>
                    <p>No matching student tutors found for your search.</p>
                    <p class="text-sm text-muted mt-2">Try searching for "Java", "Python", or selecting "All Categories".</p>
                </div>
            `;
            return;
        }

        this.searchResultsList.innerHTML = results.map(({ student, skill }) => {
            const teachBadges = student.skillsToTeach.map(id => {
                const sk = this.system.getSkillById(id);
                return sk ? `<span class="badge-pill badge-teach">${sk.name}</span>` : '';
            }).join('');

            const learnBadges = student.skillsToLearn.map(id => {
                const sk = this.system.getSkillById(id);
                return sk ? `<span class="badge-pill badge-learn">${sk.name}</span>` : '';
            }).join('');

            return `
                <div class="search-result-card">
                    <div class="search-result-left">
                        <div class="student-avatar">${student.avatar}</div>
                        <div class="search-result-info">
                            <h3 class="search-result-name">${student.name}</h3>
                            <div class="search-result-meta">${student.department} • Year ${student.year}</div>
                            <div class="search-skills-inline">
                                <span class="text-sm font-bold text-muted">Can Teach:</span>
                                ${teachBadges}
                                <span class="text-sm font-bold text-muted" style="margin-left:8px;">Wants to Learn:</span>
                                ${learnBadges}
                            </div>
                        </div>
                    </div>
                    <div>
                        <button class="btn btn-primary" data-open-request-modal data-student-id="${student.id}" data-skill-id="${skill.id}">
                            Request Exchange
                        </button>
                    </div>
                </div>
            `;
        }).join('');
    }

    renderRequests() {
        const sent = this.system.getSentRequests();
        const received = this.system.getReceivedRequests();

        if (sent.length === 0) {
            this.sentRequestsTbody.innerHTML = `
                <tr>
                    <td colspan="5" class="text-center text-muted" style="padding: 24px;">No sent requests yet.</td>
                </tr>
            `;
        } else {
            this.sentRequestsTbody.innerHTML = sent.map(req => {
                const receiver = this.system.getStudentById(req.receiverId);
                const skill = this.system.getSkillById(req.skillId);

                let statusClass = 'status-pending';
                if (req.status === 'accepted') statusClass = 'status-accepted';
                if (req.status === 'rejected') statusClass = 'status-rejected';

                let actionHtml = '';
                if (req.status === 'pending') {
                    actionHtml = `<button class="btn btn-danger-outline btn-sm" data-cancel-request="${req.id}">Cancel</button>`;
                } else {
                    actionHtml = `<button class="btn btn-secondary btn-sm" data-view-student-id="${req.receiverId}">View</button>`;
                }

                return `
                    <tr>
                        <td>
                            <strong>${receiver ? receiver.name : 'Unknown Student'}</strong>
                            <div class="text-sm text-muted">${receiver ? receiver.department : ''}</div>
                        </td>
                        <td>${skill ? skill.name : 'Unknown Skill'}</td>
                        <td><span class="status-badge ${statusClass}">${req.status.charAt(0).toUpperCase() + req.status.slice(1)}</span></td>
                        <td>${req.createdAt}</td>
                        <td>${actionHtml}</td>
                    </tr>
                `;
            }).join('');
        }

        if (received.length === 0) {
            this.receivedRequestsList.innerHTML = `
                <div class="empty-state content-card text-center">
                    <span class="empty-state-icon">📬</span>
                    <p>No received requests at this time.</p>
                </div>
            `;
        } else {
            this.receivedRequestsList.innerHTML = received.map(req => {
                const requester = this.system.getStudentById(req.requesterId);
                const skill = this.system.getSkillById(req.skillId);

                let statusBadge = '';
                let actions = '';
                if (req.status === 'pending') {
                    statusBadge = '<span class="status-badge status-pending">Pending</span>';
                    actions = `
                        <button class="btn btn-success btn-sm" data-accept-request="${req.id}">Accept</button>
                        <button class="btn btn-danger-outline btn-sm" data-reject-request="${req.id}">Reject</button>
                    `;
                } else if (req.status === 'accepted') {
                    statusBadge = '<span class="status-badge status-accepted">Accepted</span>';
                } else {
                    statusBadge = '<span class="status-badge status-rejected">Rejected</span>';
                }

                return `
                    <div class="request-card">
                        <div class="request-card-left">
                            <div class="student-avatar">${requester ? requester.avatar : '🎓'}</div>
                            <div>
                                <h4 class="font-bold">${requester ? requester.name : 'Unknown'}</h4>
                                <div class="text-sm text-secondary">
                                    Wants to learn <strong>${skill ? skill.name : ''}</strong> from you.
                                </div>
                                ${req.message ? `<div class="request-message-quote">"${req.message}"</div>` : ''}
                                <div class="text-sm text-muted mt-2">${req.createdAt} &bull; ${statusBadge}</div>
                            </div>
                        </div>
                        <div class="request-actions-group">
                            ${actions}
                        </div>
                    </div>
                `;
            }).join('');
        }
    }

    renderExchanges() {
        const exchanges = this.system.exchanges;

        if (exchanges.length === 0) {
            this.exchangesList.innerHTML = `
                <div class="empty-state content-card text-center">
                    <span class="empty-state-icon">🔄</span>
                    <p>No exchanges recorded yet.</p>
                    <p class="text-sm text-muted mt-2">When a skill exchange request is accepted, an ongoing exchange will appear here.</p>
                </div>
            `;
            return;
        }

        this.exchangesList.innerHTML = exchanges.map(ex => {
            const s1 = this.system.getStudentById(ex.student1Id);
            const s2 = this.system.getStudentById(ex.student2Id);
            const skill = this.system.getSkillById(ex.skillId);

            let statusClass = 'status-active';
            if (ex.status === 'completed') statusClass = 'status-completed';
            if (ex.status === 'cancelled') statusClass = 'status-cancelled';

            let actionBtn = '';
            if (ex.status === 'active') {
                actionBtn = `
                    <button class="btn btn-success btn-sm" data-complete-exchange="${ex.id}">Mark Complete</button>
                    <button class="btn btn-danger-outline btn-sm" data-cancel-exchange="${ex.id}">Cancel</button>
                `;
            } else {
                actionBtn = `<span class="text-sm text-muted">Finished</span>`;
            }

            return `
                <div class="exchange-card">
                    <div class="exchange-students-visual">
                        <div class="exchange-avatar">${s1 ? s1.avatar : '👩‍🎓'}</div>
                        <span class="exchange-arrow">⇄</span>
                        <div class="exchange-avatar">${s2 ? s2.avatar : '👨‍🎓'}</div>
                    </div>

                    <div class="exchange-details">
                        <h3 class="exchange-names">
                            ${s1 ? s1.name : 'Student 1'} ↔ ${s2 ? s2.name : 'Student 2'}
                        </h3>
                        <div class="exchange-skill-row">
                            <span>Skill: <strong>${skill ? skill.name : 'Unknown Skill'}</strong></span>
                            <span class="status-badge ${statusClass}">${ex.status.charAt(0).toUpperCase() + ex.status.slice(1)}</span>
                            <span class="text-muted text-sm">&bull; Started: ${ex.startedAt}</span>
                        </div>
                    </div>

                    <div class="request-actions-group">
                        ${actionBtn}
                    </div>
                </div>
            `;
        }).join('');
    }

    renderProfile() {
        const cur = this.system.getCurrentStudent();
        this.myProfileAvatar.textContent = cur.avatar;
        this.myProfileName.textContent = cur.name;
        this.myProfileStudentId.textContent = cur.studentId;
        this.myProfileDept.textContent = cur.department;
        this.myProfileYear.textContent = `Year ${cur.year}`;
        this.myProfileBioText.textContent = cur.bio || 'No bio written yet. Click Edit to add one!';

        if (cur.skillsToTeach.length === 0) {
            this.mySkillsTeachContainer.innerHTML = '<span class="text-muted text-sm">No skills added yet.</span>';
        } else {
            this.mySkillsTeachContainer.innerHTML = cur.skillsToTeach.map(id => {
                const sk = this.system.getSkillById(id);
                if (!sk) return '';
                return `
                    <span class="badge-pill badge-teach">
                        ${sk.name}
                        <button class="badge-remove-btn" data-remove-my-skill="${sk.id}" data-skill-type="teach" title="Remove">&times;</button>
                    </span>
                `;
            }).join('');
        }

        if (cur.skillsToLearn.length === 0) {
            this.mySkillsLearnContainer.innerHTML = '<span class="text-muted text-sm">No skills added yet.</span>';
        } else {
            this.mySkillsLearnContainer.innerHTML = cur.skillsToLearn.map(id => {
                const sk = this.system.getSkillById(id);
                if (!sk) return '';
                return `
                    <span class="badge-pill badge-learn">
                        ${sk.name}
                        <button class="badge-remove-btn" data-remove-my-skill="${sk.id}" data-skill-type="learn" title="Remove">&times;</button>
                    </span>
                `;
            }).join('');
        }
    }

    openAddSkillModal(preferredIntent = 'catalog') {
        this.formAddSkill.reset();
        this.skillIntentInput.value = preferredIntent;
        this.modalAddSkill.classList.remove('hidden');
        this.skillNameInput.focus();
    }

    openEditProfileModal() {
        const cur = this.system.getCurrentStudent();
        this.editProfileName.value = cur.name;
        this.editProfileDept.value = cur.department;
        this.editProfileYear.value = cur.year;
        this.editProfileBio.value = cur.bio;
        this.modalEditProfile.classList.remove('hidden');
    }

    openSendRequestModal(targetStudentId = null, skillId = null) {
        const cur = this.system.getCurrentStudent();
        const otherStudents = this.system.students.filter(s => s.id !== cur.id);

        if (otherStudents.length === 0) {
            alert('No other students registered to exchange with.');
            return;
        }

        this.reqTargetStudentSelect.innerHTML = otherStudents.map(s => `
            <option value="${s.id}" ${s.id === targetStudentId ? 'selected' : ''}>
                ${s.name} (${s.department} • Year ${s.year})
            </option>
        `).join('');

        const populateSkills = () => {
            const selectedStudentId = this.reqTargetStudentSelect.value;
            const targetStudent = this.system.getStudentById(selectedStudentId);
            if (targetStudent && targetStudent.skillsToTeach.length > 0) {
                this.reqSkillSelect.innerHTML = targetStudent.skillsToTeach.map(sId => {
                    const sk = this.system.getSkillById(sId);
                    return sk ? `<option value="${sk.id}" ${sk.id === skillId ? 'selected' : ''}>${sk.name} (${sk.category})</option>` : '';
                }).join('');
            } else {
                this.reqSkillSelect.innerHTML = this.system.skills.map(sk => `
                    <option value="${sk.id}" ${sk.id === skillId ? 'selected' : ''}>${sk.name} (${sk.category})</option>
                `).join('');
            }
        };

        this.reqTargetStudentSelect.onchange = populateSkills;
        populateSkills();

        this.reqMessageInput.value = '';
        this.modalSendRequest.classList.remove('hidden');
    }

    openViewStudentModal(studentId) {
        const student = this.system.getStudentById(studentId);
        if (!student) return;

        const cur = this.system.getCurrentStudent();
        this.viewStudentAvatar.textContent = student.avatar;
        this.viewStudentName.textContent = student.name + (student.id === cur.id ? ' (You)' : '');
        this.viewStudentMeta.textContent = `${student.department} • Year ${student.year}`;
        this.viewStudentId.textContent = `Student ID: ${student.studentId}`;
        this.viewStudentBio.textContent = student.bio || 'No bio provided.';

        this.viewStudentTeachSkills.innerHTML = student.skillsToTeach.map(sId => {
            const sk = this.system.getSkillById(sId);
            return sk ? `<span class="badge-pill badge-teach">${sk.name}</span>` : '';
        }).join('') || '<span class="text-muted text-sm">None listed</span>';

        this.viewStudentLearnSkills.innerHTML = student.skillsToLearn.map(sId => {
            const sk = this.system.getSkillById(sId);
            return sk ? `<span class="badge-pill badge-learn">${sk.name}</span>` : '';
        }).join('') || '<span class="text-muted text-sm">None listed</span>';

        if (student.id === cur.id) {
            this.btnModalRequestExchange.classList.add('hidden');
        } else {
            this.btnModalRequestExchange.classList.remove('hidden');
            this.btnModalRequestExchange.onclick = () => {
                this.modalViewStudent.classList.add('hidden');
                this.openSendRequestModal(student.id);
            };
        }

        this.modalViewStudent.classList.remove('hidden');
    }

    openDeleteSkillModal(skillId, skillName) {
        this.pendingDeleteSkillId = skillId;
        if (this.deleteSkillTargetName) {
            this.deleteSkillTargetName.textContent = skillName || 'this skill';
        }
        if (this.modalDeleteSkill) {
            this.modalDeleteSkill.classList.remove('hidden');
        }
    }
}

// ============================================================================
// 9. CLASS: BackendBridge (Optional Java REST API Synchronizer)
// ============================================================================
class BackendBridge {
    constructor(system, ui, baseUrl = 'http://localhost:8080') {
        this.system = system;
        this.ui = ui;
        this.baseUrl = baseUrl;
        this.isOnline = false;
        this.statusBadge = document.getElementById('backend-status-badge');
        this.statusText = document.getElementById('backend-status-text');
        this.pollInterval = null;
    }

    async init() {
        await this.checkHealth();
        // Check health periodically every 12 seconds
        this.pollInterval = setInterval(() => this.checkHealth(), 12000);
    }

    async checkHealth() {
        try {
            const controller = new AbortController();
            const timeoutId = setTimeout(() => controller.abort(), 1500);
            const res = await fetch(`${this.baseUrl}/api/health`, { signal: controller.signal });
            clearTimeout(timeoutId);

            if (res.ok) {
                if (!this.isOnline) {
                    this.isOnline = true;
                    this.updateBadge(true);
                    await this.syncFromBackend();
                }
                return true;
            }
        } catch (e) {
            // Backend offline / not reachable
        }

        if (this.isOnline || (this.statusText && this.statusText.textContent.includes('Checking'))) {
            this.isOnline = false;
            this.updateBadge(false);
        }
        return false;
    }

    updateBadge(online) {
        if (!this.statusBadge || !this.statusText) return;
        if (online) {
            this.statusBadge.classList.remove('status-offline');
            this.statusBadge.classList.add('status-online');
            this.statusText.textContent = 'Java Backend: Connected (8080)';
            this.statusBadge.title = 'Connected to SkillX Java REST API Server on port 8080';
        } else {
            this.statusBadge.classList.remove('status-online');
            this.statusBadge.classList.add('status-offline');
            this.statusText.textContent = 'Browser Storage (Offline Java)';
            this.statusBadge.title = 'Java backend not detected. Running seamlessly via browser LocalStorage.';
        }
    }

    async syncFromBackend() {
        try {
            const res = await fetch(`${this.baseUrl}/api/state`);
            if (res.ok) {
                const state = await res.json();
                this.loadStateIntoSystem(state);
                this.ui.renderAll();
                this.ui.showToast('Synchronized with Java Backend!', 'success');
            }
        } catch (err) {
            console.warn('Backend sync failed:', err);
        }
    }

    loadStateIntoSystem(state) {
        if (!state) return;

        // Rehydrate Students
        if (Array.isArray(state.students) && state.students.length > 0) {
            this.system.students = state.students.map(s => new Student(
                s.id,
                s.name,
                s.studentId,
                s.department,
                s.year,
                s.bio,
                s.skillsToTeach || [],
                s.skillsToLearn || [],
                s.avatar || '🎓'
            ));
        }

        // Rehydrate Skills
        if (Array.isArray(state.skills) && state.skills.length > 0) {
            this.system.skills = state.skills.map(sk => new Skill(
                sk.id,
                sk.name,
                sk.category,
                sk.description,
                sk.icon || '💡',
                sk.addedBy || 'Student',
                sk.createdAt || 'Recent'
            ));
        }

        // Rehydrate Requests
        if (Array.isArray(state.requests)) {
            this.system.requests = state.requests.map(r => new SkillRequest(
                r.id,
                r.requesterId,
                r.receiverId,
                r.skillId,
                r.message,
                r.status,
                r.createdAt || 'Recent'
            ));
        }

        // Rehydrate Exchanges
        if (Array.isArray(state.exchanges)) {
            this.system.exchanges = state.exchanges.map(e => new SkillExchange(
                e.id,
                e.student1Id,
                e.student2Id,
                e.skillId,
                e.status,
                e.startedAt || 'Recent',
                e.completedAt || null
            ));
        }

        // Set Current Student
        if (state.currentUserId) {
            this.system.currentUserId = state.currentUserId;
        }

        // Cache state into LocalStorage
        this.system.saveAll();
    }

    async addSkill(name, category, description, intent) {
        if (!this.isOnline) return;
        try {
            await fetch(`${this.baseUrl}/api/skills`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ name, category, description, intent })
            });
        } catch (e) {
            console.warn('Failed to sync addSkill to Java backend:', e);
        }
    }

    async removeSkill(skillId) {
        if (!this.isOnline) return;
        try {
            await fetch(`${this.baseUrl}/api/skills?id=${encodeURIComponent(skillId)}`, {
                method: 'DELETE'
            });
        } catch (e) {
            console.warn('Failed to sync removeSkill to Java backend:', e);
        }
    }

    async sendRequest(receiverId, skillId, message) {
        if (!this.isOnline) return;
        try {
            await fetch(`${this.baseUrl}/api/requests`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ receiverId, skillId, message })
            });
        } catch (e) {
            console.warn('Failed to sync sendRequest to Java backend:', e);
        }
    }

    async acceptRequest(requestId) {
        if (!this.isOnline) return;
        try {
            await fetch(`${this.baseUrl}/api/requests/accept`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ id: requestId })
            });
        } catch (e) {
            console.warn('Failed to sync acceptRequest to Java backend:', e);
        }
    }

    async rejectRequest(requestId) {
        if (!this.isOnline) return;
        try {
            await fetch(`${this.baseUrl}/api/requests/reject`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ id: requestId })
            });
        } catch (e) {
            console.warn('Failed to sync rejectRequest to Java backend:', e);
        }
    }

    async cancelRequest(requestId) {
        if (!this.isOnline) return;
        try {
            await fetch(`${this.baseUrl}/api/requests/cancel`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ id: requestId })
            });
        } catch (e) {
            console.warn('Failed to sync cancelRequest to Java backend:', e);
        }
    }

    async completeExchange(exchangeId) {
        if (!this.isOnline) return;
        try {
            await fetch(`${this.baseUrl}/api/exchanges/complete`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ id: exchangeId })
            });
        } catch (e) {
            console.warn('Failed to sync completeExchange to Java backend:', e);
        }
    }

    async cancelExchange(exchangeId) {
        if (!this.isOnline) return;
        try {
            await fetch(`${this.baseUrl}/api/exchanges/cancel`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ id: exchangeId })
            });
        } catch (e) {
            console.warn('Failed to sync cancelExchange to Java backend:', e);
        }
    }

    async switchUser(studentId) {
        if (!this.isOnline) return;
        try {
            await fetch(`${this.baseUrl}/api/user/switch`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ id: studentId })
            });
        } catch (e) {
            console.warn('Failed to sync switchUser to Java backend:', e);
        }
    }

    async updateProfile(studentId, name, department, year, bio) {
        if (!this.isOnline) return;
        try {
            await fetch(`${this.baseUrl}/api/profile`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ id: studentId, name, department, year, bio })
            });
        } catch (e) {
            console.warn('Failed to sync updateProfile to Java backend:', e);
        }
    }

    async resetData() {
        if (!this.isOnline) return;
        try {
            await fetch(`${this.baseUrl}/api/reset`, {
                method: 'POST'
            });
        } catch (e) {
            console.warn('Failed to sync resetData to Java backend:', e);
        }
    }
}

// ============================================================================
// 10. APPLICATION BOOTSTRAPPER
// ============================================================================
document.addEventListener('DOMContentLoaded', async () => {
    const system = new SkillExchangeSystem();
    system.init();

    const ui = new UIController(system);
    const bridge = new BackendBridge(system, ui);
    system.setBridge(bridge);
    await bridge.init();

    window.skillExchangeSystem = system;
    window.uiController = ui;
    window.backendBridge = bridge;

    console.log('%cSkillX Mini Project Loaded Successfully! 🎓', 'color: #4f46e5; font-size: 16px; font-weight: bold;');
    console.log('Polymorphism Test (Student.displayProfile()):');
    console.log(system.getCurrentStudent().displayProfile());
});