import java.math.BigDecimal
import kotlin.math.*

const val DOUBLE_OR_INT_REGEX = "(\\d+(\\.\\d+)?+|\\.\\d+)"
const val DOUBLE_OR_INT_REGEX_IN_BRACES = "\\[$DOUBLE_OR_INT_REGEX\\]"

val operators = listOf(
    Operator(
        symbol = "+",
        regex = "\\+".toRegex(),
        description = "addition",
        canPrecedeNumber = false,
        unaryOperator = false,
        operatorWithBase = false
    ) { firstNumber, secondNumber ->
        require(secondNumber != null) { binaryOperationError("addition") }
        BigDecimal(firstNumber + secondNumber)
    },
    Operator(
        symbol = "-",
        regex = "-".toRegex(),
        description = "subtraction",
        canPrecedeNumber = true,
        unaryOperator = false,
        operatorWithBase = false
    ) { firstNumber, secondNumber ->
        require(secondNumber != null) { binaryOperationError("subtraction") }
        BigDecimal(firstNumber - secondNumber)
    },
    Operator(
        symbol = "*",
        regex = "\\*".toRegex(),
        description = "multiplication",
        canPrecedeNumber = false,
        unaryOperator = false,
        operatorWithBase = false
    ) { firstNumber, secondNumber ->
        require(secondNumber != null) { binaryOperationError("multiplication") }
        BigDecimal(firstNumber * secondNumber)
    },
    Operator(
        symbol = "/",
        regex = "/".toRegex(),
        description = "division",
        canPrecedeNumber = false,
        unaryOperator = false,
        operatorWithBase = false
    ) { firstNumber, secondNumber ->
        require(secondNumber != null) { binaryOperationError("division") }
        require(secondNumber != 0.0) { "Division by $secondNumber is not allowed!" }
        BigDecimal(firstNumber / secondNumber)
    },
    Operator(
        symbol = "^i",
        regex = "\\^$DOUBLE_OR_INT_REGEX".toRegex(),
        description = "exponentiation with power i",
        canPrecedeNumber = false,
        unaryOperator = true,
        operatorWithBase = true
    ) { firstNumber, secondNumber ->
        BigDecimal(firstNumber.pow(secondNumber!!))
    },
    Operator(
        symbol = "%",
        regex = "%".toRegex(),
        description = "modulus",
        canPrecedeNumber = false,
        unaryOperator = false,
        operatorWithBase = false
    ) { firstNumber, secondNumber ->
        require(secondNumber != null) { binaryOperationError("modulus") }
        require(secondNumber != 0.0) { "Division by $secondNumber is not allowed!" }
        BigDecimal(firstNumber % secondNumber)
    },
    Operator(
        symbol = "V[i]",
        regex = "V$DOUBLE_OR_INT_REGEX_IN_BRACES".toRegex(),
        description = "root with index i",
        canPrecedeNumber = true,
        unaryOperator = true,
        operatorWithBase = true
    ) { firstNumber, secondNumber ->
        require(firstNumber >= 0.0) { "It is impossible to get even radical of negative number!" }
        BigDecimal(nthRoot(firstNumber, secondNumber!!))
    },
    Operator(
        symbol = "!",
        regex = "!".toRegex(),
        description = "factorial",
        canPrecedeNumber = false,
        unaryOperator = true,
        operatorWithBase = false
    ) { firstNumber, _ ->
        require(firstNumber >= 0.0) { "It is possible to get factorial only of integer! You must use integer." }
        BigDecimal(factorial(firstNumber))
    },
    Operator(
        symbol = "log[b]",
        regex = "log$DOUBLE_OR_INT_REGEX_IN_BRACES".toRegex(),
        description = "logarithm with base b",
        canPrecedeNumber = true,
        unaryOperator = true,
        operatorWithBase = true
    ) { firstNumber, secondNumber ->
        require(firstNumber >= 0.0) { "You can get logarithm only from number greater than 0!" }
        BigDecimal(log(firstNumber, secondNumber!!))
    },
    Operator(
        symbol = "ln",
        regex = "ln".toRegex(),
        description = "logarithm with base e",
        canPrecedeNumber = true,
        unaryOperator = true,
        operatorWithBase = false
    ) { firstNumber, _ ->
        require(firstNumber >= 0.0) { "You can get logarithm only from number greater than 0!" }
        BigDecimal(ln(firstNumber))
    }
)

fun binaryOperationError(operationDescription: String) =
    "Binary operation $operationDescription requires two operands. Second number is missing."

val operatorsWithDescriptions = operators.associate { it.symbol to it.description }
val operatorsWithBase = operators.filter { it.operatorWithBase }.map { it.regex }
val operatorsWithoutBase = operators.filter { !it.operatorWithBase }.map { it.regex }

fun validOperator(operatorInput: String?): Operator? = operatorInput?.let {
    when (val operator = operators.firstOrNull { operatorInput.matches(it.regex) }) {
        null -> null
        else -> operator.setOperator(operatorInput)
    }
}

fun calculate(firstNumber: Double, operator: Operator, secondNumber: Double? = null): String {
    val processedSecondNumber = when {
        operator.operatorWithBase -> operator.operator.getBase()
        else -> secondNumber
    }
    return operator.calculation.invoke(firstNumber, processedSecondNumber).toPlainString()
}

private fun String.getBase(): Double = replace("[^0-9.]".toRegex(), "").toDouble()

class Operator(
    val symbol: String,
    val regex: Regex,
    val description: String,
    val canPrecedeNumber: Boolean,
    val unaryOperator: Boolean,
    val operatorWithBase: Boolean,
    val calculation: (Double, Double?) -> BigDecimal
) {
    lateinit var operator: String

    fun setOperator(operatorInput: String): Operator {
        this.operator = operatorInput
        return this
    }
}

fun nthRoot(num: Double, index: Double): Double {
    val temporaryResult = Math.E.pow(ln(num) / index)
    val rounded = round(temporaryResult)
    return when {
        abs(rounded - temporaryResult) < 0.00000000000002 -> rounded
        else -> temporaryResult
    }
}

fun factorial(first: Double): Double {
    TODO("Not yet implemented")
}