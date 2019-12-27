package br.com.zup.bank.service

import br.com.zup.bank.model.Account
import br.com.zup.bank.model.User

/**
 * Created by Victor Santos on 26/12/2019
 */
interface IAccountService {

    fun createAccount(user: User): Account

    fun getAll(): MutableList<Account>

    fun getById(id: Long): Account

    fun getByCpf(cpf: String): Account
}