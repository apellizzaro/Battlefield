package storer

import com.google.inject.ImplementedBy
import model.Game

import scala.concurrent.Future

/**
  * Created by anton on 12/17/2016.
  */
//stores json
@ImplementedBy(classOf[MemoryStore])
trait StoreApi {
  def saveObject(key:String,game:String): Future[Boolean]
  def retrieveObject(key: String): Future[Either[String,String]]
  def getGameStats:Future[List[String]]
  def getGamesSummary: Future[String Either List[String]]
}
