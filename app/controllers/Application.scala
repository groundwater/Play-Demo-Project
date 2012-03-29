package controllers

import play.api._
import play.api.mvc._
import play.api.libs.concurrent._
import play.api.templates._
import play.api.data._
import play.api.data.Forms._

import akka.actor._
import akka.pattern._
import akka.util._
import akka.util.duration._

import message._
import actions._
import actors._
import models._

import play.api.Play.current
import play.api.mvc.Security.Authenticated
import play.api.i18n.Messages
import play.libs.Akka.system

class Security(guard: ActorRef) extends Controller {
    
    val loginForm = Form(
        tuple(
            "userid"   -> text,
            "password" -> text
        )
    )
    
    def login = Action { implicit request =>
        Logger.info("Anonymous User Authenticating")
        loginForm.bindFromRequest.fold (
            errors => BadRequest,
            {
                case (user,password) => Async{ // Spawn Asynchronous Request; Frees Controller
                    implicit val timeout = Timeout(5 seconds) // Required by ask (?) method
                    new AkkaPromise(guard ? Authenticate(user,password)) map {
                        case RegisterUser(id) => 
                            Logger.info("User {id} Authenticated")
                            Redirect("/").withSession("user"->id).flashing("message"->Messages("login.success"))
                        case Failure => Unauthorized(Messages("login.failure"))
                        case _ => InternalServerError("500.011")
                    }
                }
                case _ => InternalServerError("500.101")
            }
        )
    }
    
    def register = Action {
        Logger.info("User Registration")
        TODO
    }
    
    def logout = Action { implicit request =>
        Redirect( "/" ).withSession( session - "user" ).flashing("message"->Messages("logout.success"))
    }
}

trait SecurityContext {
    val guard: ActorRef
    lazy val security = new Security(guard)
}

class Application extends Controller {
    def index = AnyAuthenticated { implicit user => 
        Action { implicit request =>
            Logger.info("Accessed by User: " + user)
            Ok( views.html.index() )
        }
    }
    def login = Action { implicit request =>
        Ok( views.html.login() )
    }
}

trait ApplicationContext {
    lazy val application = new Application()
}

object MainContext extends SecurityContext with ApplicationContext {
    val guard = system.actorOf(Props[SecurityGuard],"security")
}


