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
                .toList()
    }
}
