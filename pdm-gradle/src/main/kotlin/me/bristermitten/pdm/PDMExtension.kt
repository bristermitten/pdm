package me.bristermitten.pdm

import me.bristermitten.pdm.repository.MavenCentral

open class PDMExtension
{
    var version: String = javaClass.classLoader.getResource("version")?.readText()?.trim() ?: "0.0.1"
    var outputDirectory: String = DependencyManager.PDM_DIRECTORY_NAME
    var centralMirror: String = MavenCentral.DEFAULT_CENTRAL_MIRROR
}
