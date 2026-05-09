package samu.kiss.myapplication.common

fun validEmailAddress(email: String): Boolean {
    val regex = Regex("""^[A-Za-z0-9._%+\-]+@[A-Za-z0-9.\-]+\.[A-Za-z]{2,}$""")
    return email.matches(regex)
}

fun validPassword(password: String): Boolean {
    val regex = Regex("""^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$""")
    return password.matches(regex)
}