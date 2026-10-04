package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import models.*;
import service.SkillExchangeSystem;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Built-in Lightweight HTTP REST Server.
 * Uses com.sun.net.httpserver without external dependencies.
 */
public class SkillServer {
    private final int port;
    private final SkillExchangeSystem system;
    private HttpServer server;

    public SkillServer(int port, SkillExchangeSystem system) {
        this.port = port;
        this.system = system;
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);

        // Core Endpoints
        server.createContext("/api/health", new HealthHandler());
        server.createContext("/api/state", new StateHandler());
        server.createContext("/api/students", new StudentsHandler());
        server.createContext("/api/skills", new SkillsHandler());
        server.createContext("/api/requests", new RequestsHandler());
        server.createContext("/api/requests/accept", new AcceptRequestHandler());
        server.createContext("/api/requests/reject", new RejectRequestHandler());
        server.createContext("/api/requests/cancel", new CancelRequestHandler());
        server.createContext("/api/exchanges", new ExchangesHandler());
        server.createContext("/api/exchanges/complete", new CompleteExchangeHandler());
        server.createContext("/api/exchanges/cancel", new CancelExchangeHandler());
        server.createContext("/api/user/switch", new SwitchUserHandler());
        server.createContext("/api/profile", new ProfileHandler());
        server.createContext("/api/reset", new ResetHandler());
        server.createContext("/", new StaticFileHandler());

        server.setExecutor(null); // default executor
        server.start();
        System.out.println("⚡ SkillX Java REST Server running on port " + port);
        System.out.println("   Open: http://localhost:" + port + " in your browser");
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    // CORS helper
    private static void setCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    private static boolean handleOptions(HttpExchange exchange) throws IOException {
        setCorsHeaders(exchange);
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return true;
        }
        return false;
    }

    private static void sendJson(HttpExchange exchange, int statusCode, String responseJson) throws IOException {
        setCorsHeaders(exchange);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        byte[] bytes = responseJson.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    private static String readBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    private static Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        if (query == null) return result;
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                try {
                    result.put(entry[0], URLDecoder.decode(entry[1], "UTF-8"));
                } catch (Exception ignored) {}
            } else if (entry.length == 1) {
                result.put(entry[0], "");
            }
        }
        return result;
    }

    // 1. Health Handler
    private class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleOptions(exchange)) return;
            String json = "{\"status\":\"ok\",\"backend\":\"Java HTTP Server (SkillX)\",\"port\":" + port + "}";
            sendJson(exchange, 200, json);
        }
    }

    // 2. Full State Handler
    private class StateHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleOptions(exchange)) return;
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            sb.append("\"currentUserId\":\"").append(system.getCurrentUserId()).append("\",");

            // Students
            sb.append("\"students\":[");
            List<Student> students = system.getStudents();
            for (int i = 0; i < students.size(); i++) {
                sb.append(students.get(i).toJson());
                if (i < students.size() - 1) sb.append(",");
            }
            sb.append("],");

            // Skills
            sb.append("\"skills\":[");
            List<Skill> skills = system.getSkills();
            for (int i = 0; i < skills.size(); i++) {
                sb.append(skills.get(i).toJson());
                if (i < skills.size() - 1) sb.append(",");
            }
            sb.append("],");

            // Requests
            sb.append("\"requests\":[");
            List<SkillRequest> reqs = system.getRequests();
            for (int i = 0; i < reqs.size(); i++) {
                sb.append(reqs.get(i).toJson());
                if (i < reqs.size() - 1) sb.append(",");
            }
            sb.append("],");

            // Exchanges
            sb.append("\"exchanges\":[");
            List<SkillExchange> exs = system.getExchanges();
            for (int i = 0; i < exs.size(); i++) {
                sb.append(exs.get(i).toJson());
                if (i < exs.size() - 1) sb.append(",");
            }
            sb.append("]");

            sb.append("}");
            sendJson(exchange, 200, sb.toString());
        }
    }

    // 3. Students Handler
    private class StudentsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleOptions(exchange)) return;
            StringBuilder sb = new StringBuilder("[");
            List<Student> list = system.getStudents();
            for (int i = 0; i < list.size(); i++) {
                sb.append(list.get(i).toJson());
                if (i < list.size() - 1) sb.append(",");
            }
            sb.append("]");
            sendJson(exchange, 200, sb.toString());
        }
    }

    // 4. Skills Handler
    private class SkillsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleOptions(exchange)) return;
            String method = exchange.getRequestMethod();

            if ("GET".equalsIgnoreCase(method)) {
                StringBuilder sb = new StringBuilder("[");
                List<Skill> list = system.getSkills();
                for (int i = 0; i < list.size(); i++) {
                    sb.append(list.get(i).toJson());
                    if (i < list.size() - 1) sb.append(",");
                }
                sb.append("]");
                sendJson(exchange, 200, sb.toString());
            } else if ("POST".equalsIgnoreCase(method)) {
                String body = readBody(exchange);
                String name = JsonUtils.getString(body, "name");
                String category = JsonUtils.getString(body, "category");
                String desc = JsonUtils.getString(body, "description");
                String intent = JsonUtils.getString(body, "intent");

                Skill s = system.addSkill(name, category, desc, intent);
                sendJson(exchange, 201, s.toJson());
            } else if ("DELETE".equalsIgnoreCase(method)) {
                Map<String, String> query = queryToMap(exchange.getRequestURI().getQuery());
                String id = query.get("id");
                if (id != null) {
                    system.removeSkill(id);
                    sendJson(exchange, 200, "{\"success\":true}");
                } else {
                    sendJson(exchange, 400, "{\"error\":\"Missing skill id\"}");
                }
            }
        }
    }

    // 5. Requests Handler
    private class RequestsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleOptions(exchange)) return;
            String method = exchange.getRequestMethod();

            if ("GET".equalsIgnoreCase(method)) {
                StringBuilder sb = new StringBuilder("[");
                List<SkillRequest> list = system.getRequests();
                for (int i = 0; i < list.size(); i++) {
                    sb.append(list.get(i).toJson());
                    if (i < list.size() - 1) sb.append(",");
                }
                sb.append("]");
                sendJson(exchange, 200, sb.toString());
            } else if ("POST".equalsIgnoreCase(method)) {
                String body = readBody(exchange);
                String receiverId = JsonUtils.getString(body, "receiverId");
                String skillId = JsonUtils.getString(body, "skillId");
                String message = JsonUtils.getString(body, "message");

                try {
                    SkillRequest req = system.sendRequest(receiverId, skillId, message);
                    sendJson(exchange, 201, req.toJson());
                } catch (Exception e) {
                    sendJson(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
                }
            }
        }
    }

    // 6. Accept Request Handler
    private class AcceptRequestHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleOptions(exchange)) return;
            String body = readBody(exchange);
            String id = JsonUtils.getString(body, "id");
            if (id.isEmpty()) {
                Map<String, String> q = queryToMap(exchange.getRequestURI().getQuery());
                id = q.getOrDefault("id", "");
            }

            SkillExchange ex = system.acceptRequest(id);
            if (ex != null) {
                sendJson(exchange, 200, ex.toJson());
            } else {
                sendJson(exchange, 404, "{\"error\":\"Request not found\"}");
            }
        }
    }

    // 7. Reject Request Handler
    private class RejectRequestHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleOptions(exchange)) return;
            String body = readBody(exchange);
            String id = JsonUtils.getString(body, "id");
            if (id.isEmpty()) {
                Map<String, String> q = queryToMap(exchange.getRequestURI().getQuery());
                id = q.getOrDefault("id", "");
            }

            boolean ok = system.rejectRequest(id);
            sendJson(exchange, ok ? 200 : 404, "{\"success\":" + ok + "}");
        }
    }

    // 8. Cancel Request Handler
    private class CancelRequestHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleOptions(exchange)) return;
            String body = readBody(exchange);
            String id = JsonUtils.getString(body, "id");
            if (id.isEmpty()) {
                Map<String, String> q = queryToMap(exchange.getRequestURI().getQuery());
                id = q.getOrDefault("id", "");
            }

            boolean ok = system.cancelRequest(id);
            sendJson(exchange, 200, "{\"success\":" + ok + "}");
        }
    }

    // 9. Exchanges Handler
    private class ExchangesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleOptions(exchange)) return;
            StringBuilder sb = new StringBuilder("[");
            List<SkillExchange> list = system.getExchanges();
            for (int i = 0; i < list.size(); i++) {
                sb.append(list.get(i).toJson());
                if (i < list.size() - 1) sb.append(",");
            }
            sb.append("]");
            sendJson(exchange, 200, sb.toString());
        }
    }

    // 10. Complete Exchange Handler
    private class CompleteExchangeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleOptions(exchange)) return;
            String body = readBody(exchange);
            String id = JsonUtils.getString(body, "id");
            if (id.isEmpty()) {
                Map<String, String> q = queryToMap(exchange.getRequestURI().getQuery());
                id = q.getOrDefault("id", "");
            }
            boolean ok = system.completeExchange(id);
            sendJson(exchange, ok ? 200 : 404, "{\"success\":" + ok + "}");
        }
    }

    // 11. Cancel Exchange Handler
    private class CancelExchangeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleOptions(exchange)) return;
            String body = readBody(exchange);
            String id = JsonUtils.getString(body, "id");
            if (id.isEmpty()) {
                Map<String, String> q = queryToMap(exchange.getRequestURI().getQuery());
                id = q.getOrDefault("id", "");
            }
            boolean ok = system.cancelExchange(id);
            sendJson(exchange, ok ? 200 : 404, "{\"success\":" + ok + "}");
        }
    }

    // 12. Switch User Handler
    private class SwitchUserHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleOptions(exchange)) return;
            String body = readBody(exchange);
            String id = JsonUtils.getString(body, "id");
            if (!id.isEmpty()) {
                system.setCurrentUserId(id);
                sendJson(exchange, 200, "{\"success\":true,\"currentUserId\":\"" + id + "\"}");
            } else {
                sendJson(exchange, 400, "{\"error\":\"Missing user id\"}");
            }
        }
    }

    // 13. Profile Update Handler
    private class ProfileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleOptions(exchange)) return;
            String body = readBody(exchange);
            String id = JsonUtils.getString(body, "id");
            String name = JsonUtils.getString(body, "name");
            String dept = JsonUtils.getString(body, "department");
            String year = JsonUtils.getString(body, "year");
            String bio = JsonUtils.getString(body, "bio");

            boolean ok = system.updateProfile(id, name, dept, year, bio);
            sendJson(exchange, ok ? 200 : 404, "{\"success\":" + ok + "}");
        }
    }

    // 14. Reset Handler
    private class ResetHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleOptions(exchange)) return;
            system.seedDemoData();
            sendJson(exchange, 200, "{\"success\":true,\"message\":\"Demo data restored\"}");
        }
    }

    // 15. Static File Handler (serves index.html, style.css, app.js directly)
    private class StaticFileHandler implements HttpHandler {
        private final File webRoot;

        public StaticFileHandler() {
            File current = new File(".").getAbsoluteFile();
            if (new File(current, "index.html").exists()) {
                this.webRoot = current;
            } else if (current.getParentFile() != null && new File(current.getParentFile(), "index.html").exists()) {
                this.webRoot = current.getParentFile();
            } else {
                this.webRoot = current;
            }
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (handleOptions(exchange)) return;

            String path = exchange.getRequestURI().getPath();
            if (path == null || path.equals("/") || path.isEmpty()) {
                path = "/index.html";
            }

            File targetFile = new File(webRoot, path).getCanonicalFile();
            if (!targetFile.getPath().startsWith(webRoot.getCanonicalPath()) || !targetFile.exists() || targetFile.isDirectory()) {
                String notFound = "<h1>404 Not Found</h1>";
                exchange.sendResponseHeaders(404, notFound.length());
                OutputStream os = exchange.getResponseBody();
                os.write(notFound.getBytes(StandardCharsets.UTF_8));
                os.close();
                return;
            }

            String mime = "application/octet-stream";
            String lower = targetFile.getName().toLowerCase();
            if (lower.endsWith(".html") || lower.endsWith(".htm")) mime = "text/html; charset=UTF-8";
            else if (lower.endsWith(".css")) mime = "text/css; charset=UTF-8";
            else if (lower.endsWith(".js")) mime = "application/javascript; charset=UTF-8";
            else if (lower.endsWith(".json")) mime = "application/json; charset=UTF-8";
            else if (lower.endsWith(".svg")) mime = "image/svg+xml";
            else if (lower.endsWith(".png")) mime = "image/png";
            else if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) mime = "image/jpeg";
            else if (lower.endsWith(".ico")) mime = "image/x-icon";

            setCorsHeaders(exchange);
            exchange.getResponseHeaders().set("Content-Type", mime);

            byte[] bytes = java.nio.file.Files.readAllBytes(targetFile.toPath());
            exchange.sendResponseHeaders(200, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }
}