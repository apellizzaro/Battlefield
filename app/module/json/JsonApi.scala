package module

import java.io.InputStream

import scala.util.Try

/**
 * Abstraction over JsonApi.
 */
trait JsonApi {

  /**
   * Generates a JSON string for the given object.
   * @param obj Object with a statically known type for which a ClassTag is available.
   * @tparam A type of the passed object.
   */
  def generate[A <: AnyRef: Manifest](obj: A): String

  /**
   * Parse a class from the given input.
   * @param input JSON string.
   * @tparam Statically known type for which a ClassTag is available.
   */
  def parse[A: Manifest](input: String): A

  /**
   * Parse a class from the given input.
   * @param input InputStream from which to consume JSON.
   * @tparam Statically known type for which a ClassTag is available.
   */
  def parse[A: Manifest](input: InputStream): A

  /**
   * Parse a class from the given input.
   * @param input JSON string.
   * @tparam Statically known type for which a ClassTag is available.
   * @return parsing might fail so it returns an option
   */
  def parseOpt[A: Manifest](input: String): Option[A] = Try(parse(input)).toOption

  /**
   * Parse a class from the given input.
   * @param input InputStream from which to consume JSON.
   * @tparam Statically known type for which a ClassTag is available.
   * @return parsing might fail so it returns an option
   */
  def parseOpt[A: Manifest](input: InputStream): Option[A] = Try(parse(input)).toOption

  /**
   * Creates a map out of the passed JSON input.
   * @param input JSON string.
   */
  def toMap(input: String): Map[String, Any]
}
