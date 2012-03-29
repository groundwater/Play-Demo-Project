package actors

import play.api._

import akka.actor._
import akka.pattern._
import akka.util._

import message._
import models._

import play.api.Play.current
import play.api.libs.Codecs.sha1

class SecurityGuard extends Actor {
    
    val seed = current.configuration.getString("application.secret") match {
        case Some(key) => key
        case _ => 
            Logger.warn("Please Set Application Secret")
            ""
    }
    
    def hash ( input: String ) : String = {
        sha1( seed + input )
    }
    
    def receive = {
        case Authenticate(user,password) => 
            User.get(user) match {
                case Some(user) => 
                    if ( user.passwdhash == hash(password) )
                        sender ! RegisterUser(user.uid)
                    else 
                        sender ! Failure
                case None => sender ! Failure
            }
        case Register(user,password) =>
            User.commit( User(user,hash(password)) )
            sender ! Success
        case _ => sender ! Failure
    }
}

