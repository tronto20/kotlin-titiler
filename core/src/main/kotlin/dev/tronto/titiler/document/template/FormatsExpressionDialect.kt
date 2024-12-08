package dev.tronto.titiler.document.template

import org.thymeleaf.context.IExpressionContext
import org.thymeleaf.dialect.IExpressionObjectDialect
import org.thymeleaf.expression.IExpressionObjectFactory

class FormatsExpressionDialect : IExpressionObjectDialect {
    override fun getName(): String {
        return "formats"
    }

    override fun getExpressionObjectFactory(): IExpressionObjectFactory {
        return object : IExpressionObjectFactory {
            override fun getAllExpressionObjectNames(): MutableSet<String> {
                return mutableSetOf("formats")
            }

            override fun buildObject(context: IExpressionContext, expressionObjectName: String?): Any? {
                return if (expressionObjectName?.equals("formats") == true) {
                    Formats(context.locale)
                } else {
                    null
                }
            }

            override fun isCacheable(expressionObjectName: String?): Boolean {
                return true
            }
        }
    }
}
