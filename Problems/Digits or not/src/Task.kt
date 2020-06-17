import java.util.*

fun main(args: Array<String>) {
    val scanner = Scanner(System.`in`)
    val a: Char = scanner.next().first()
    val b: Char = scanner.next().first()
    val x: Char = scanner.next().first()
    val y: Char = scanner.next().first()
    print(a.isDigit())
    print("\\")
    print(b.isDigit() )
    print("\\")
    print(x.isDigit())
    print("\\")
    print(y.isDigit())
}
