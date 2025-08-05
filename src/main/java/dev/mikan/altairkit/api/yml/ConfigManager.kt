package dev.mikan.altairkit.api.yml

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import kotlin.jvm.Throws

class ConfigManager {

    private var folder: File
    private var plugin: Plugin
    private val fileMap: MutableMap<String, FileConfiguration> = mutableMapOf()

    constructor(plugin: Plugin) {
        this.plugin = plugin
        this.folder = plugin.dataFolder
        loadFolder()
    }


    fun get(file: String) = fileMap[file]


    /*
    * Loads a file
    *
    * If the file or the specified dirs don't exist
    * it will create it
    * */
    @Throws(IOException::class)
    fun load(filePath: String) : ConfigManager {
        var path = filePath

        if (!path.endsWith(".yml"))
            path += ".yml"

        val source = plugin.getResource(path)
        require(source != null) {"Invalid source for path: $path, check your files."}
        val file = File(folder,filePath)

        if (file.exists()) return this

        file.mkdirs()
        file.createNewFile()
        copyInputStreamToFile(source,file)

        val config = YamlConfiguration.loadConfiguration(file);
        fileMap.put(path,config)

        return this
    }


    private fun loadFolder(){
        if (!this.folder.exists())
            this.folder.mkdirs()
    }


    /*
    * Uses byte chunking technique to write the yml file
    * keeping comments
    * */
    private fun copyInputStreamToFile(source: InputStream, destination: File) {
        val inputStream = source
        copyToFile(inputStream, destination)
        inputStream.close()

    }


    @Throws(IOException::class)
    fun copyToFile(inputStream: InputStream, file: File) {
        val out = OutputStreamUtil.newOutputStream(file, false);
        copy(inputStream, out);

        out.close()
    }


    private fun copy(inputStream: InputStream,  outputStream: OutputStream) : Int {
        val count = copyLarge(inputStream, outputStream)
        return if (count > 2147483647L) -1 else count.toInt()
    }


    private fun copyLarge(inputStream: InputStream,  outputStream: OutputStream): Long = copy(inputStream, outputStream, 8192)


    private fun copyLarge(inputStream: InputStream,  outputStream: OutputStream, buffer: ByteArray): Long {
        var count: Long = 0
        var n = 0
        while (-1 != n) {
            n = inputStream.read(buffer)
            count += n.toLong()
            outputStream.write(buffer, 0, n)
        }
        return count;
    }


    private fun copy( inputStream: InputStream,  outputStream: OutputStream,  bufferSize: Int): Long {
        return copyLarge(inputStream, outputStream, byteArray(bufferSize))
    }

    private fun byteArray(size:Int) = ByteArray(size)

}