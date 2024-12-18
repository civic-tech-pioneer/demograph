package civictech.auth

interface JwtTokenProvider {
    fun generate(username: String, roles: List<String>): String
}