package machine

import java.util.*
class Coffee(){
    companion object {
        var money: Int = 550
        var disposableCups: Int = 9
        var coffeeBeans: Int = 120
        var milk: Int = 540
        var water: Int = 400
    }

    fun remainingCoffee() {
        println("The coffee machine has:")
        println("$water of water")
        println("$milk of milk")
        println("$coffeeBeans of coffee beans")
        println("$disposableCups of disposable cups")
        println("$$money of money")
    }

    fun buyCoffee() {
        val scanner = Scanner(System.`in`)
        print("What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino:")
        var coffeeVariety = scanner.next()
        if (coffeeVariety == null) {
            coffeeVariety = scanner.next()
        }
        when (coffeeVariety) {
            "1" -> prepareCoffee(250,0,16,4)
            "2" -> prepareCoffee(350,75,20,7)
            "3" -> prepareCoffee(200,100,12,6)
        }
    }

    private fun prepareCoffee(useWater: Int, useMilk: Int, useCoffeeBeans: Int, addMoney: Int) {
        if (water >= useWater && milk >= useMilk && coffeeBeans >= useCoffeeBeans && disposableCups >= 1) {
            water -= useWater
            milk -= useMilk
            coffeeBeans -= useCoffeeBeans
            disposableCups -= 1
            money += addMoney
            println("I have enough resources, making you a coffee!")
        } else notEnoughCoffee(useWater,useMilk,useCoffeeBeans)
    }

    private fun notEnoughCoffee(noWater: Int, noMilk: Int, noCoffeeBeans: Int) {
        when {
            water < noWater -> println("Sorry, not enough water!")
            milk < noMilk -> println("Sorry, not enough coffee Beans!")
            coffeeBeans < noCoffeeBeans -> println("Sorry, not enough coffee Beans!")
            disposableCups < 1 -> println("Sorry, not enough disposable cups!")
        }
    }

    fun fillCoffee() {
        val scanner = Scanner(System.`in`)
        println("Write how many ml of water do you want to add:")
        water += scanner.nextInt()
        println("Write how many ml of milk do you want to add:")
        milk += scanner.nextInt()
        println("Write how many grams of coffee beans do you want to add:")
        coffeeBeans += scanner.nextInt()
        println("Write how many disposable cups of coffee do you want to add:")
        disposableCups += scanner.nextInt()
    }

    fun takeCoffee() {
        println("I gave you $$money")
        money = 0
    }
}

fun main() {  
    val scanner = Scanner(System.`in`)
    var count = 1
    while (count > 0) {
        println("Write action (buy, fill, take):")
        val coffee = Coffee()
        when(scanner.next()){
            "buy" -> coffee.buyCoffee()
            "fill" -> coffee.fillCoffee()
            "take" -> coffee.takeCoffee()
            "remaining" -> coffee.remainingCoffee()
            "exit" -> count = 0
            else -> println("Invalid input")
        }
    }
}
