package me.bristermitten.pdm.reposcraper.entity;

/**
 * An artifact in a Maven Repository
 */
public class Project {
    private final String groupId;

    public Project(String groupId) {
        this.groupId = groupId;
    }
}
