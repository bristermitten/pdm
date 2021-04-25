package me.bristermitten.pdm.reposcraper.entity;

import me.bristermitten.pdm.reposcraper.entity.version.Version;

/**
 * An artifact in a Maven Repository
 */
public class Artifact {
    private final String groupId;
    private final String artifactId;
    private final Version<?> version;


    public Artifact(String groupId, String artifactId, Version<?> version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }
}
