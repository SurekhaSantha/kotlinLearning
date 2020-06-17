import java.util.Scanner

fun main(args: Array<String>) {
    val scanner = Scanner(System.`in`)
    val a: Char = scanner.next().first()
    val b: Char = scanner.next().first()
    val c: Char = scanner.next().first()
    println(a + 1 == b && b + 1 == c)
}
