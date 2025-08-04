package dev.mikan.altairkit.api.yml

import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.StandardOpenOption

object OutputStreamUtil {
    private val EMPTY_LINK_OPTION_ARRAY = emptyArray<LinkOption>()
    private val OPEN_OPTIONS_APPEND = arrayOf(StandardOpenOption.CREATE, StandardOpenOption.APPEND)
    private val OPEN_OPTIONS_TRUNCATE = arrayOf(StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)

    @Throws(IOException::class)
    fun newOutputStream(file: File, append: Boolean): OutputStream {
        return newOutputStream(file.toPath(), append)
    }

    @Throws(IOException::class)
    fun newOutputStream(path: Path, append: Boolean): OutputStream {
        return newOutputStream(
            path,
            EMPTY_LINK_OPTION_ARRAY,
            *if (append) OPEN_OPTIONS_APPEND else OPEN_OPTIONS_TRUNCATE
        )
    }

    @Throws(IOException::class)
    private fun newOutputStream(
        path: Path,
        linkOptions: Array<LinkOption>,
        vararg openOptions: StandardOpenOption
    ): OutputStream {
        if (!Files.exists(path, *linkOptions)) {
            createParentDirectories(path)
        }
        return Files.newOutputStream(path, *openOptions)
    }

    @Throws(IOException::class)
    private fun createParentDirectories(path: Path) {
        path.parent?.takeUnless { Files.exists(it) }?.let {
            Files.createDirectories(it)
        }
    }
}