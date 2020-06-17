data class Player(val id: Int, val name: String, val hp: Int) {
    companion object Properties {
        const val id = 19
        const val hp = 100
        fun create(name: String): Player {
            return Player(Player.Properties.id, name, Player.Properties.hp)
        }
    }
}
