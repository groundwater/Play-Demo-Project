package models

case class User(uid: String)
object Anonymous extends User("Anonymous")
