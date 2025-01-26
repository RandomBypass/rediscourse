package edu.udemy.redis.util;

public enum UserClass {

    PRIME(0),
    STD(10),
    GUEST(20);

    private final int priority;

    UserClass(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return "UserClass{" +
                "name=" + this.name() +
                "priority=" + priority +
                '}';
    }
}