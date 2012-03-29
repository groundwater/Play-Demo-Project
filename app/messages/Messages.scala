package message

case class Register(id: String,password: String)
case class Authenticate(id: String,password: String)

case object Success
case object Failure

case class RegisterUser(id: String)
