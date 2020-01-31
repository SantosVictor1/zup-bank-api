package br.com.zup.bank

import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class SayHelloDelegate : JavaDelegate {
    private val LOGGER: Logger = LoggerFactory.getLogger(SayHelloDelegate::class.java)

    @Throws(Exception::class)
    override fun execute(execution: DelegateExecution?) {
        LOGGER.info("hello {}", execution)
    }

}