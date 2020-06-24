package me.bristermitten.pdm.json

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import me.bristermitten.pdmlibs.repository.MavenRepositoryFactory
import me.bristermitten.pdmlibs.repository.Repository

class RepositoryTypeAdapter(
        private val repositoryFactory: MavenRepositoryFactory
) : TypeAdapter<Repository>()
{
    override fun write(writer: JsonWriter, value: Repository)
    {
        writer.value(value.url)
    }

    override fun read(reader: JsonReader): Repository
    {
        return repositoryFactory.create(reader.nextString())
    }
}
