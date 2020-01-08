package br.com.zup.bank.common

import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.support.MessageSourceAccessor
import org.springframework.lang.Nullable
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

/**
 * Created by Victor Santos on 08/01/2020
 */
@Component
class Message(
    val messageSource: MessageSource
) {
    private lateinit var acessor: MessageSourceAccessor

    @PostConstruct
    private fun init() {
        acessor = MessageSourceAccessor(messageSource, LocaleContextHolder.getLocale())
    }

    fun getMessage(code: String): String {
        return acessor.getMessage(code, LocaleContextHolder.getLocale())
    }
}