import service.SkillExchangeSystem;
import server.SkillServer;

/**
 * Main application entry point for SkillX Java Backend.
 * Demonstrates starting the backend server and testing OOP methods.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("🎓 SkillX — Student Skill Exchange System (Java)");
        System.out.println("College OOP Mini Project Backend");
        System.out.println("==================================================");

        // 1. Instantiate Core OOP Service
        SkillExchangeSystem system = new SkillExchangeSystem();

        // 2. Demonstrate Polymorphism in Console
        System.out.println("\n[OOP Demonstration: Polymorphism]");
        models.Student currentStudent = system.getCurrentStudent();
        if (currentStudent != null) {
            System.out.println("Calling Student.displayProfile() (Overridden from User):");
            System.out.println("-> " + currentStudent.displayProfile());
        }

        // 3. Start HTTP Server on port 8080 (or args[0])
        int port = 8080;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (Exception ignored) {}
        }

        try {
            SkillServer server = new SkillServer(port, system);
            server.start();
            System.out.println("\n📡 REST API Endpoints active at http://localhost:" + port + "/api/");
            System.out.println("Press Ctrl+C to stop server.\n");
        } catch (Exception e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}