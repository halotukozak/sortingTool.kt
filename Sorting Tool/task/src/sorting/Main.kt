package sorting

import java.io.File
import java.util.*

fun main(args: Array<String>) {
    val (sortingType, dataType, inputFileName, outputFileName) = extractArgs(args)
    if (sortingType == null || dataType == null) return
    val input = scan(dataType, inputFileName)
    val size = getSize(input.size, dataType)
    val data = getSortedData(dataType, sortingType, input)
    if (outputFileName.isNullOrEmpty()) {
        println(size)
        println(data)
    } else {
        val file = File(outputFileName)
        file.writeText(size + data)
    }
}

private fun extractArgs(args: Array<String>): List<String?> {
    val dashArgs = args.filter { it.isArg() }
    var sortingType = ""
    var dataType = ""
    var inputFileName: String? = null
    var outputFileName: String? = null
    for (arg in dashArgs) {
        val indexOfParameter = args.indexOf(arg) + 1
        try {
            when (arg) {
                "-sortingType" -> sortingType = args.getParameter(indexOfParameter) ?: throw NoSortingTypeException()
                "-dataType" -> dataType = args.getParameter(indexOfParameter) ?: throw NoDataTypeException()
                "-inputFile" -> inputFileName = args.getParameter(indexOfParameter) ?: throw NoFileNameException()
                "-outputFile" -> outputFileName = args.getParameter(indexOfParameter) ?: throw NoFileNameException()
                else -> throw InvalidArgumentException(arg)
            }
        } catch (e: Exception) {
            println(e.message)
            return List(4) { null }
        }
    }
    return listOf(sortingType, dataType, inputFileName, outputFileName)
}

private fun Array<String>.getParameter(indexOfParameter: Int): String? {
    val parameter = this.getOrNull(indexOfParameter) ?: ""
    return if (this.isNotEmpty() && parameter.isNotArg()) parameter else null
}

fun scan(dataType: String, inputFileName: String?): MutableList<String> {
    val file = File(inputFileName ?: "")
    val scanner = if (file.exists()) {
        Scanner(file.readText())
    } else {
        Scanner(System.`in`)
    }
    val output = mutableListOf<String>()
    when (dataType) {
        "line" -> while (scanner.hasNext()) {
            output.add(scanner.nextLine())
        }

        else -> while (scanner.hasNext()) {
            output.addAll(scanner.next().split(" "))
        }
    }
    return output
}

fun getSize(size: Int, dataType: String): String {
    return "Total " + when (dataType) {
        "long", "" -> "numbers"
        "line" -> "lines"
        else -> "words"
    } + ": $size."

}


private fun getSortedData(dataType: String, sortingType: String, input: MutableList<String>): String {
    var output = ""
    if (sortingType == "byCount") {
        val counting = mutableMapOf<String, Int>()
        input.forEach {
            if (counting.containsKey(it)) {
                counting[it] = counting[it]!! + 1
            } else {
                counting[it] = 1
            }
        }
        val preparedList =
            if (dataType == "long") counting.map { it.key.toInt() to it.value }.toList() else counting.toList()
        val sortedCounting = preparedList.sortedWith(compareBy({ it.second }, { it.first })).toMap()
        sortedCounting.forEach { (key, value) ->
            val percentage = value.times(100).div(input.size)
            output += "$key: $value time(s), $percentage%\n"
        }
    } else {
        val (sortedData, separator) = when (dataType) {
            "line" -> {
                Pair(input.sorted(), "\n")
            }
            "long" -> {
                val intInput = mutableListOf<Int>()
                input.forEach {
                    try {
                        val element = it.toIntOrNull() ?: throw NotLongException(it)
                        intInput.add(element)
                    } catch (e: Exception) {
                        println(e.message)
                    }
                }
                Pair(input.map { it.toInt() }.sorted(), " ")
            }

            else -> {
                Pair(input.sorted(), " ")
            }
        }
        output += "Sorted sortedData: ${sortedData.joinToString(separator)}"
    }
    return output
}

class NoDataTypeException : Exception("No data type defined !")
class NoSortingTypeException : Exception("No sorting type defined !")
class NoFileNameException : Exception("No filename defined !")
class InvalidArgumentException(arg: String) : Exception("$arg is not a valid parameter. It will be skipped.")
class NotLongException(it: String) : Exception("\"$it\" is not a long. It will be skipped.")


private fun String.isArg(): Boolean = this.matches(Regex("^-.+"))
private fun String.isNotArg(): Boolean = !this.isArg()
