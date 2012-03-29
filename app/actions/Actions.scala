package actions

import play.api._
import play.api.mvc._
import play.api.libs.concurrent._
import play.api.templates._
import play.api.data._
import play.api.data.Forms._

import models._

case class Logging[A](action: Action[A]) extends Action[A] {
    def apply(request: Request[A]): Result = {
        Logger.info("Calling Action")
        action(request)
    }
    lazy val parser = action.parser
}

case class UnAuthenticated[A](action: Action[A]) extends Action[A] {
    def apply(request: Request[A]): Result = {
        request.session.get("user") match {
            case None => Results.Redirect("/")
            case _ => action(request)
        }
    }
    lazy val parser = action.parser
}

trait AnyAuthenticated[A] extends Action[A]
object AnyAuthenticated {
    def apply[A] (bodyParser: BodyParser[A]) (block: User => Request[A] => Result ) = new AnyAuthenticated[A] {
        def parser = bodyParser
        def apply(req: Request[A]) = {
            val user = req.session.get("user") match {
                case Some(uid) => User(uid)
                case _ => Anonymous
            }
            block(user)(req)
        }
    }
    def apply (block: User => Request[AnyContent] => Result): Action[AnyContent] = {
        AnyAuthenticated(BodyParsers.parse.anyContent)(block)
    }
}
