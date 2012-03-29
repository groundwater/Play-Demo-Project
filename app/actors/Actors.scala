package actors

import akka.actor._
import akka.pattern._
import akka.util._

import message._

class SecurityGuard extends Actor {
    def receive = {
        case Authenticate(user,password) => sender ! RegisterUser(user)
        case _ => sender ! Failure
    }
}
