package br.com.zup.bank

//import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
//@EnableProcessApplication
class BankApplication

fun main(args: Array<String>) {
    runApplication<BankApplication>(*args)
}
