package de.netherspace.tools.spectre.parser

import com.sun.codemodel.JCodeModel
import com.sun.codemodel.writer.FileCodeWriter
import java.io.File

interface BaseCodeModelWriter {

    fun writeCodeModel(codeModel: JCodeModel, destFolder: File): List<File> {
        codeModel.build(FileCodeWriter(destFolder))
        return destFolder
                .walk()
                .filter { it.name.endsWith(".java") }
                .filter { !isPackageBlacklisted(it) }
                .toList()
    }

    /**
     * Checks if the given Java file lies in a blacklisted package.
     *
     * @return true if the class lies in a blacklisted package
     */
    private fun isPackageBlacklisted(file: File): Boolean {
        val packageBlacklist = listOf(
                "java/util",
                "java/lang"
        )
        return packageBlacklist
                .map { file.parent.endsWith(it) }
                .filter { it }
                .count() > 0
    }
}
