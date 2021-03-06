package at.searles.parsing.format

class EditableFormatContext(private val editableText: EditableText) {
    private var indentLevel = 0
    private var positionDelta = 0

    private var mustAddNewLine = false
    private var mustAddEmptyLine = false
    private var mustAddSpace = false

    fun appendSpace() {
        mustAddSpace = true
    }

    fun appendNewLine() {
        mustAddNewLine = true
    }

    fun appendEmptyLine() {
        mustAddEmptyLine = true
    }

    fun indent() {
        indentLevel ++
    }

    fun unindent() {
        indentLevel --
    }

    fun delete(position: Long, length: Int) {
        editableText.delete(position + positionDelta, length.toLong())
        positionDelta -= length
    }

    private fun insertIndentation(position: Long) {
        insert(indentation.repeat(indentLevel), position)
    }

    private fun insert(string: String, position: Long) {
        editableText.insert(position + positionDelta, string)
        positionDelta += string.length
    }

    fun confirmToken(tokenId: Int, start: Long, length: Int) {
        if(mustAddEmptyLine) {
            mustAddEmptyLine = false
            mustAddNewLine = false
            mustAddSpace = false

            insert(emptyLine, start)
            insertIndentation(start)
        }

        if(mustAddNewLine) {
            mustAddNewLine = false
            mustAddSpace = false

            insert(newLine, start)
            insertIndentation(start)
        }

        if(mustAddSpace) {
            mustAddSpace = false
            insert(space, start)
        }
    }

    companion object {
        const val emptyLine = "\n\n"
        const val newLine = "\n"
        const val space = " "
        val indentation = space.repeat(2)
    }
}
