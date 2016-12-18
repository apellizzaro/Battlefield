package storer

import model.Game
import java.util.concurrent._

import com.google.inject.Inject

import scala.concurrent.{ExecutionContext, Future}
//import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by anton on 12/17/2016.
  */
class MemoryStore @Inject() (implicit exec: ExecutionContext) extends StoreApi {

  val gamesStore: ConcurrentHashMap[String,String] = new ConcurrentHashMap()

  def saveObject(key:String, game:String): Future[Boolean] = {

    gamesStore.put(key.toString,game)

    Future(true)
  }

  def retrieveObject(key: String): Future[Either[String,String]]= {

    if (gamesStore.containsKey(key)) {
      val jsonStr = gamesStore.get(key)
      Future(Right(jsonStr))
    }
    else
      Future(Left("Game not found"))
  }

}
