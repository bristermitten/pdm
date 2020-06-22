package me.bristermitten.pdm

open class PDMExtension
{
    var version: String = javaClass.classLoader.getResource("version").readText().trim()
    var outputDirectory: String = DependencyManager.PDM_DIRECTORY_NAME
}
