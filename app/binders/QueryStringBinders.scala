package binders

import play.api.i18n.Lang
import play.api.mvc.QueryStringBindable

object QueryStringBinders {
  implicit object LangQueryStringBindable extends QueryStringBindable[Lang] {

    override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, Lang]] =
      params.get(key)
        .flatMap(seq => seq.headOption) // flatMap removes double Option
        .map(code => Lang.get(code).toRight(s"$code is not a valid language"))

    override def unbind(key: String, value: Lang): String = value.code
  }
}
