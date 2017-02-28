package storer

import scala.collection.concurrent.Map
import com.google.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/**
  * Mimic a phisical db to store the games. In this case it uses a concurrent TrieMap, it could be a Dynamo table or any other store
  */
class MemoryStore @Inject() (implicit exec: ExecutionContext) extends StoreApi {

  val gamesStore: Map[String,String] = scala.collection.concurrent.TrieMap()

  def saveObject(key:String, game:String): Future[Boolean] = {

    gamesStore.put(key.toString,game)

    Future(true)
  }

  def retrieveObject(key: String): Future[Either[String,String]]= {
    gamesStore.get(key).map(r=>Future(Right(r))).getOrElse (Future(Left("Object not found")))
  }

  def getGamesSummary: Future[String Either List[String]] = {
    Future (Right(gamesStore.filter( kv=>kv._2.contains("GameSettingUp") || kv._2.contains("GameInProgress")).values.toList))
  }

}
