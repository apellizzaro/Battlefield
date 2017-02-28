package dataAccess

import model._
import javax.inject._

import com.google.inject.ImplementedBy
import module.json.json4s.Json
import storer.StoreApi

import scala.concurrent.{ExecutionContext, Future}
/**
  * DAO for user information
  */

@ImplementedBy(classOf[UserDao])
trait UserDaoInterface {
  def login (user: User) : Future [String Either String]
  def getUserByToken (token:String) : Future[String Either User]
}

@Singleton
class UserDao @Inject() (storer:StoreApi) (implicit exec: ExecutionContext) extends UserDaoInterface {

  def login (user: User) : Future [String Either String] = {
    //assuming always succesfull
    val token = java.util.UUID.randomUUID().toString
    storer.saveObject(token,Json.generate(user))
    Future(Right(token))
  }

  def getUserByToken (token:String) : Future[String Either User] = {
    storer.retrieveObject(token).map {
      case Right(s) => Right(Json.parse[User](s))
      case Left(e) => Left(e)
    }
  }

}
