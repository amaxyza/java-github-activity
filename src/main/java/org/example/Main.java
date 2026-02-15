package org.example;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java github-activity <username>");
            System.exit(1);
        }

        String username = args[0];
        var userActivity = new UserActivity(username);
        userActivity.printUserActivity();
    }
}
