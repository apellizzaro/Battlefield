package storer

import scala.collection.concurrent.Map
import com.google.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by anton on 12/17/2016.
  */
class MemoryStore @Inject() (implicit exec: ExecutionContext) extends StoreApi {

  val gamesStore: Map[String,String] = scala.collection.concurrent.TrieMap()

  def saveObject(key:String, game:String): Future[Boolean] = {

    gamesStore.put(key.toString,game)

    Future(true)
  }

  def retrieveObject(key: String): Future[Either[String,String]]= {
    gamesStore.get(key).map(r=>Future(Right(r))).getOrElse (Future(Left("Game not found")))
  }

  def getGameStats : Future[List[String]] = {
    Future (gamesStore.filter( kv=>kv._2.contains("GameSettingUp") || kv._2.contains("GameInProgress")).values.toList)
  }

  def getGamesSummary: Future[String Either List[String]] = {
    Future (Right(gamesStore.filter( kv=>kv._2.contains("GameSettingUp") || kv._2.contains("GameInProgress")).values.toList))
  }

}
