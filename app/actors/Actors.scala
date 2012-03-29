package actors

import akka.actor._
import akka.pattern._
import akka.util._

import message._

class SecurityGuard extends Actor {
    def receive = {
        case _ => sender ! RegisterUser("Bob")
    }
}

