package org.example;

public record UserActivityResponse(
        Long id,
        String type,
        Repository repo,
        Actor actor,
        Object payload,
        String created_at,
        Org org
) {
}
