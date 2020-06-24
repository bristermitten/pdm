package me.bristermitten.pdm

import me.bristermitten.pdmlibs.artifact.Artifact

private val SPIGOT_GROUP_IDS = setOf(
        "net.minecraft",
        "org.spigotmc",
        "org.bukkit",
        "com.destroystokyo.paper"
)

private val SPIGOT_ARTIFACT_IDS = setOf(
        "server",
        "spigot",
        "spigot-api",
        "bukkit",
        "craftbukkit",
        "paper-api"
)

/**
 * Return if this artifact is a Spigot artifact.
 *
 * This is determined by comparing the group and artifact id with a given table (which includes NMS, Bukkit, CraftBukkit, and Paper too)
 */
fun Artifact.isSpigotArtifact(): Boolean
{
    return groupId in SPIGOT_GROUP_IDS && artifactId in SPIGOT_ARTIFACT_IDS
}
