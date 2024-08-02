package dependencies

interface JubilareRepository {
    fun greet(): String
}

class JubilareRepositoryImpl: JubilareRepository {
    override fun greet(): String {
        return "Hello from JubilareRepository"
    }
}