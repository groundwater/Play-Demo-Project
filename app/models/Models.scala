package models

import anorm._
import anorm.SqlParser._

import play.api.db._
import play.api.Play.current

case class User(uid: String, passwdhash: String = "")
object Anonymous extends User("Anonymous")

object User {
    
    def commit( user: User ) {
        DB.withConnection { implicit connection => 
            SQL("""
                INSERT INTO user(id,pwdhash) values ({id},{pwdhash})
                """).on(
                    "id"      -> user.uid,
                    "pwdhash" -> user.passwdhash
                ).executeInsert()
        }
    }
    
    val userParser = str("id") ~ str("pwdhash")
    
    def get( userId: String ) : Option[User] = {
        DB.withConnection{ implicit connection => 
                SQL("""
                    SELECT * FROM user WHERE id={id}
                    """
                ) on ("id"->userId) as (userParser singleOpt) match {
                    case Some(id~pwdhash) => Some(User(id,pwdhash))
                    case _ => None
                }
        }
    }
    
}
