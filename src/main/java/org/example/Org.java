package org.example;

public record Org(
        Long id,
        String login,
        String gravatarId,
        String url,
        String avatar_url
) {}
