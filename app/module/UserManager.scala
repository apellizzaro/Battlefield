package module

import javax.inject.Inject

import com.google.inject.Singleton
import dataAccess.UserDaoInterface
import model.User

import scala.concurrent.Future

/**
  * Created by apellizz on 1/1/17.
  */
@Singleton
class UserManager @Inject() (userDaoInterface: UserDaoInterface) {

  def login (userName: User): Future [String Either String]  = {
    userDaoInterface.login(userName)
  }

  def geUserByToken (token:String): Future [String Either User] = {
    userDaoInterface.getUserByToken(token)
  }

}
