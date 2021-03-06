package controllers

import javax.inject.{Inject, Singleton}

import akka.actor.ActorSystem
import model.User
import module.UserManager
import module.json.json4s.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext

/**
  * Simulates a user login controlelr
  */
@Singleton
class UserController @Inject()(actorSystem: ActorSystem, userManager:UserManager)(implicit exec: ExecutionContext) extends Controller {

  /*
  * Performs a pretend login
  * Body f the request is Json with the user information*/
  def login = Action.async(parse.json) { request => {
      val bodyStr = request.body.toString()
      val user = Json.parse[User](bodyStr)

      userManager.login(user).map {
        case Right(token) => Ok(token)
        case Left(e) => Unauthorized(e)
      }
    }
  }
}
