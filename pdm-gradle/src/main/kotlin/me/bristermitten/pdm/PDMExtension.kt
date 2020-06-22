package me.bristermitten.pdm

open class PDMExtension
{
    var version: String = javaClass.classLoader.getResource("version").readText().trim()
}
