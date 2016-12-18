
package module.json
package json4s

import module.JsonApi

import language.implicitConversions
import java.io.{InputStream, OutputStream}
import javax.xml.bind.annotation.XmlTransient

import model.{GameStatusSerializer, ResultShootingSerializer, SeaStatusSerializer}
import org.json4s._
import org.json4s.jackson.JsonMethods
import org.json4s.jackson.Serialization.{write, writePretty}

/**
 * Implementation of [[JsonApi]] using Json4s.
 *
 * '''Note:''' You can override the defaults formats in this trait by overriding the variable `formats`.
 */
trait Json4sApi extends JsonApi {

  /**
   * Defautl formats.
   * Includes JodaTime, IntAsString, StringAsBoolean and JavaEnum.
   */
  @XmlTransient
  implicit val formats = (DefaultFormats + NoTypeHints) ++ org.json4s.ext.JodaTimeSerializers.all  ++ List(SeaStatusSerializer,GameStatusSerializer,ResultShootingSerializer)/*++ List(new IntAsStringSerializer, new StringAsBooleanDeserializer, new JavaEnumSerializer) */
  override def generate[A <: AnyRef: Manifest](obj: A): String = write[A](obj)
  def generate[A <: AnyRef](obj: A, out: OutputStream) = write[A](obj, out)
  def generatePretty[A <: AnyRef](obj: A): String = writePretty[A](obj)
  def generateJValue[A](obj: A) = Extraction.decompose(obj)
  def rawParse(input: String) = JsonMethods.parse(input)
  def rawParse(input: InputStream) = JsonMethods.parse(input)
  override def parse[A: Manifest](input: String): A = JsonMethods.parse(input).extract[A](implicitly, implicitly[Manifest[A]])
  override def parse[A: Manifest](input: InputStream): A = JsonMethods.parse(input).extract[A](implicitly, implicitly[Manifest[A]])
  override def parseOpt[A: Manifest](input: String): Option[A] = JsonMethods.parseOpt(input).map(_.extract[A](implicitly, implicitly[Manifest[A]]))
  override def parseOpt[A: Manifest](input: InputStream): Option[A] = JsonMethods.parseOpt(input).map(_.extract[A](implicitly, implicitly[Manifest[A]]))
  override def toMap(input: String): Map[String, Any] = JsonMethods.parse(input).asInstanceOf[JObject].values
  def mapToClass[A: Manifest](m: Map[String, Any]): A = generateJValue(m).extract[A](implicitly, implicitly[Manifest[A]])
}
/**
 * Static instance of Json4s
 */
object Json extends Json4sApi
