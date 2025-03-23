package civictech.auth

interface JwtTokenProvider {
    fun generate(userName: String, roles: List<String> = listOf("USER")): String
}