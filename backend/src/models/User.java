package models;

/**
 * Base Abstract Class: User
 * Demonstrates Abstraction in OOP.
 */
public abstract class User {
    protected String id;
    protected String name;

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Abstract method to be overridden by subclasses.
     * Demonstrates Polymorphism.
     */
    public abstract String displayProfile();

    /**
     * Abstract method to serialize model to JSON format.
     */
    public abstract String toJson();
}