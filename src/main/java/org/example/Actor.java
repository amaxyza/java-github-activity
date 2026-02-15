package org.example;

public record Actor(
        Long id,
        String login,
        String displayLogin,
        String gravatarId,
        String url,
        String actor_url
) {}
