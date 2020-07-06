package calculator
import java.math.BigInteger
import java.util.Scanner
fun main() {
    val scanner = Scanner(System.`in`)
    var go: Boolean = true
    loop@ while (go) {
        var str: String = readLine()!!
        //val str = "7 + 3 * ((4 + 3) * 7 + 1) - 6 / (2 + 1)"
        //val str: String = "112234567890 + 112234567890 * (10000000999 - 99900000000000)"
        when {
            str.startsWith("/", true) -> {
                when (str) {
                    "/help" -> println("The program calculates multiplication, division, addition and subtraction")
                    "/exit" -> {
                        println("Bye!")
                        go = false
                    }
                    else -> println("Unknown command")
                }
            }
            Regex("(\\*\\*|\\/\\/)").containsMatchIn(str) -> println("Invalid Expression")
            str == "" -> continue@loop
            str.contains("=") -> assignVariable(str)
            str.contains("(") || str.contains(")")-> parseParenthesesExpression(str)
            else -> {
                calculateVariables(str.replace("\\s{2}".toRegex(), ""))
            }
        }
    }
}

var varMap = mutableMapOf<String, Int>()
var varMapBig = mutableMapOf<String, BigInteger>()
private var stack = mutableListOf<String>()
private var queue = mutableListOf<String>()
private var expressionList = mutableListOf<String>()


fun parseParenthesesExpression(str: String) {
    var openParenthesesCount: Int = 0
    var closeParenthesesCount: Int = 0
    str.forEach {
        if (it == '(') {
            openParenthesesCount +=1
        } else if(it == ')'){
            closeParenthesesCount +=1
        }
    }
    if (openParenthesesCount != closeParenthesesCount) {
        println("Invalid expression")
        return
    }
    var finString = str
    if(Regex("^(.*)[a-zA-Z]+(.*)(\\d{10})(.*)$").containsMatchIn(str)) {
        varMapBig.forEach { (key, value) ->
            finString = finString.replace(key.toRegex(), "$value")
        }
    } else if (Regex("^(.*)[a-zA-Z]+(.*)$").containsMatchIn(str)) {
        varMap.forEach { (key, value) ->
            finString = finString.replace(key.toRegex(), "$value")
        }
    }
    getPostFixEx(finString)
}

private fun getPostFixEx(finString: String) {

    var finString= finString
    finString = finString.replace("(", " ( ")
    finString = finString.replace(")", " ) ")
    finString = finString.replace("+", " + ")
        finString = finString.replace("-", " - ")
        finString = finString.replace("*", " * ")
        finString = finString.replace("/", " / ")
        finString = finString.replace("^", " ^ ")
    finString = finString.replace("\\s+".toRegex(), " ")
    var finStrings: MutableList<String> = finString.split(Regex("\\s")).toMutableList()
    var finList = mutableListOf<String>()
    for(i in 0..finStrings.size-1){
        //println("${finStrings[i]}")
        if(!finStrings[i].equals(Regex("^\\s$"))) {
            finStrings[i] = finStrings[i].trim()
            finList.add(finStrings[i])
        }
    }
    finList.forEach {
        if(!it.isNullOrEmpty()){
            expressionList.add(it)
        }
    }
    for (i in 0 until expressionList.size) {
        when {
            expressionList[i] == "(" -> push(expressionList[i])
            expressionList[i] == ")" -> {
                if (expressionList.contains("(")) {
                    pop()
                }
            }
            Regex("[\\d]").containsMatchIn(expressionList[i]) -> addQueue(expressionList[i])
            Regex("[+-]").containsMatchIn(expressionList[i]) ->
                if (stack.isEmpty() || stack.last() == "(") push(expressionList[i])
                else if (stack.last().contains(Regex("[/*]"))) {
                    pop()
                    push(expressionList[i])
                }
                else {
                    addQueue(stack.last())
                    stack[stack.lastIndex] = expressionList[i]
                }
            Regex("[*/]").containsMatchIn(expressionList[i]) -> {
                if (stack.isNotEmpty() && (stack.last() == "*" || stack.last() == "/")) {
                    pop()
                }
                push(expressionList[i])
            }
        }
    }
    if (stack.isNotEmpty()) {
        for (i in stack.lastIndex downTo 0) {
            if (stack[i] != "(") {
                addQueue(stack[i])
            }
        }
    }
    expressionList.forEach {
        if (Regex("(.*)\\d{10}(.*)").containsMatchIn(it)) {
            calcPostFixBig()
            return
        } else {
            calcPostFix()
            return
        }
    }

}
private fun push(item: String) {
    stack.add(item)
}

private fun pop() {
    Loop@ for (i in stack.lastIndex downTo 0) {
        if (stack[i] == "(") {
            stack[i] = " "
            break@Loop
        }
        addQueue(stack[i])
        stack[i] = " "
    }
    stack.removeIf { it == " " }
}

private fun addQueue(item: String) {
    queue.add(item)
}

private fun calcPostFixBig() {
    //println("queue $queue")
    val stack = mutableListOf<BigInteger>()
    for (item in queue) {
        when {
            Regex("[\\d]").containsMatchIn(item) -> {
                stack.add(BigInteger(item))
            }

            item == "*" -> {
                stack[stack.lastIndex - 1] =  stack[stack.lastIndex - 1].multiply(stack.last())
                //stack[stack.lastIndex - 1] = stack[stack.lastIndex - 1] * stack.last()
                stack.removeAt(stack.lastIndex)
            }

            item == "/" -> {
                if (stack.last() != BigInteger.ZERO) {
                    val (bigResult, r1) = stack[stack.lastIndex - 1].divideAndRemainder(stack.last())
                    stack[stack.lastIndex - 1] = bigResult
                    //stack[stack.lastIndex - 1] = stack[stack.lastIndex - 1] / stack.last()
                    stack.removeAt(stack.lastIndex)

                } else {
                    println("Cannot divide by Zero")
                    stack.removeAt(stack.lastIndex)
                    return
                }
            }

            item == "+" -> {
                stack[stack.lastIndex - 1] = stack[stack.lastIndex - 1] + stack.last()
                stack.removeAt(stack.lastIndex)
            }

            item == "-" -> {
                stack[stack.lastIndex - 1] = stack[stack.lastIndex - 1] - stack.last()
                stack.removeAt(stack.lastIndex)
            }
        }
    }
    println("${stack.first()}")
}

private fun calcPostFix() {
    val stack = mutableListOf<Int>()
            //println(queue)
    for (item in queue) {
        //println(item)
       // println(stack)
        //println(stack)
        when {
            Regex("[\\d]").containsMatchIn(item) -> {
                stack.add(item.toInt())
            }
            item == "*" -> {
                stack[stack.lastIndex - 1] = stack[stack.lastIndex - 1] * stack.last()
                stack.removeAt(stack.lastIndex)
            }
            item == "/" -> {
                stack[stack.lastIndex - 1] = stack[stack.lastIndex - 1] / stack.last()
                stack.removeAt(stack.lastIndex)
            }
            item == "-" -> {
                stack[stack.lastIndex - 1] = stack[stack.lastIndex - 1] - stack.last()
                stack.removeAt(stack.lastIndex)
            }
            item == "+" -> {
                stack[stack.lastIndex - 1] = stack[stack.lastIndex - 1] + stack.last()
                stack.removeAt(stack.lastIndex)
            }
        }
    }
    println("${stack.first()}")
}

fun assignVariable(str: String) {
    val assignNumber = Regex("^\\s*[a-zA-Z]+\\s*=\\s*-?\\d+\\s*$")
    val assignVariable = Regex("^\\s*[a-zA-Z]+\\s*=\\s*[a-zA-Z]+\\s*$")
    val invalidIdentifier = Regex("^\\s*[a-zA-Z0-9]+\\s*=(.*)$")
    val invalidVarAssignment = Regex("^(.*)=\\s*[a-zA-Z0-9]$")
    val invalidAssignment = Regex("^(.*)=(.*)=(.*)$")
    when {
        assignNumber.containsMatchIn(str) -> {
            val varNumber = str.split("=").toTypedArray()
            for (i in 0..varNumber.lastIndex) {
                varNumber[i] = varNumber[i].replace("\\s".toRegex(), "")
            }
            try {
                varMap[varNumber[0]]=varNumber[1].toInt()
            }
            catch (e: NumberFormatException) {
                varMapBig[varNumber[0]] = BigInteger(varNumber[1])
            }

            return
        }
        assignVariable.containsMatchIn(str) -> {
            val varNumber = str.split("=").toTypedArray()
            for (i in 0..varNumber.lastIndex) {
                varNumber[i] = varNumber[i].replace("\\s".toRegex(), "")
            }
            var found: Boolean = false
            for ((key, value) in varMap) {
                if (key == varNumber[1]) {
                    varMap[varNumber[0]] = value
                    found = true
                }
            }
            for ((key, value) in varMapBig) {
                if (key == varNumber[1]) {
                    varMapBig[varNumber[0]] = value
                    found = true
                }
            }
            if (!found) {
                println("Unknown variable")
            }
            return
        }
        invalidIdentifier.containsMatchIn(str) -> {
            println("Invalid identifier")
            return
        }
        invalidAssignment.containsMatchIn(str) || invalidVarAssignment.containsMatchIn(str) -> {
            println("Invalid assignment")
            return
        }
        else -> {
            println("Invalid identifier")
        }
    }
}

fun calculateVariables(str: String) {
    val variableOnly = Regex("^[a-zA-Z]+$")
    val calculateAllVariables = Regex("^(.*)[a-zA-Z]+(.*)$")
    val calculateAllNumber = Regex("^[^a-zA-Z]$")
    when {
        variableOnly.containsMatchIn(str) -> {
            if(varMap[str] != null) {
                println(varMap[str])
            } else {
                println("Unknown variable")
            }
            return
        }
        calculateAllVariables.containsMatchIn(str) -> {
            var finString = str
            varMap.forEach { (key, value) ->
                finString = finString.replace(key.toRegex(), "$value")
            }
            varMapBig.forEach { (key, value) ->
                finString = finString.replace(key.toRegex(), "$value")
            }
            cal(finString)
        }
        calculateAllNumber.containsMatchIn(str) -> cal(str)
        else -> cal(str)
    }
}
fun cal(str: String) {
    val numbers = str.split(" ").toTypedArray()
    var number: Int = 0
    var numberBig: BigInteger = BigInteger.ZERO
    if (isDigit(numbers[0])) {
        try {
            number = numbers[0].toInt()
            calculation(numbers,number)
        } catch (e: NumberFormatException) {
            numberBig = BigInteger(numbers[0])
            calculationBig(numbers,numberBig)
        }
    } else {
        println("Invalid expression")
    }
}

fun calculationBig(numbers: Array<String>,numberBig: BigInteger ) {
    var numberBig: BigInteger = numberBig
    for(i in 2..numbers.lastIndex step 2) {
        if(isDigit(numbers[i]) && numbers[i-1].contains("*")) {
            numberBig = numberBig.multiply(BigInteger(numbers[i]))
            //numberBig *= BigInteger(numbers[i])
        } else if(isDigit(numbers[i]) && numbers[i-1].contains("/")) {
            val (numberBigResult, r1) = numberBig.divideAndRemainder(BigInteger(numbers[i]))
            numberBig = numberBigResult
            //numberBig /= BigInteger(numbers[i])
        } else if (isDigit(numbers[i]) && numbers[i-1].contains("-")) {
            if (subtract(numbers[i-1])) numberBig -= BigInteger(numbers[i])
            else numberBig += BigInteger(numbers[i])
        } else if(isDigit(numbers[i]) && numbers[i-1].contains("+")) {
            numberBig += BigInteger(numbers[i])
        }
    }

    println("$numberBig")
}

fun calculation(numbers: Array<String>, number: Int){
    var number: Int = number
    for(i in 2..numbers.lastIndex step 2) {
        if(isDigit(numbers[i]) && numbers[i-1].contains("*")) {
            number *= numbers[i].toInt()
        } else if(isDigit(numbers[i]) && numbers[i-1].contains("/")) {
            number /= numbers[i].toInt()
        } else if (isDigit(numbers[i]) && numbers[i-1].contains("-")) {
            if (subtract(numbers[i-1])) number -= numbers[i].toInt()
            else number += numbers[i].toInt()
        } else if(isDigit(numbers[i]) && numbers[i-1].contains("+")) {
            number += numbers[i].toInt()
        }
    }
    println(number)
}

private fun subtract(str:String): Boolean {
    var subtract: Boolean = true
    var count = 0
    for (i in str) {
        count += 1
    }
    subtract = count % 2 != 0
    return subtract
}

private fun isDigit(str:String): Boolean {
    var digit: Boolean = false
    for (i in str.toCharArray()) {
        digit = i.isDigit()
    }
    return digit
}
