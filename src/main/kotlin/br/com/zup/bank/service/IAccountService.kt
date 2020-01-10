package br.com.zup.bank.service

import br.com.zup.bank.dto.request.AccountRequestDTO
import br.com.zup.bank.dto.request.ActivityRequestDTO
import br.com.zup.bank.dto.response.success.AccountBalanceDTO
import br.com.zup.bank.dto.response.success.AccountResponseDTO
import br.com.zup.bank.dto.response.success.ActivityResponseDTO
import br.com.zup.bank.dto.response.success.ExtractResponseDTO

/**
 * Created by Victor Santos on 26/12/2019
 */
interface IAccountService {

    fun createAccount(accountRequestDTO: AccountRequestDTO): AccountResponseDTO

    fun getAll(): MutableList<AccountResponseDTO>

    fun getById(id: Long): AccountResponseDTO

    fun getByAccountNumberOrCpf(accNumber: String, cpf: String): AccountResponseDTO

    fun getAccountBalance(accNumber: String): AccountBalanceDTO

    fun deactivateAccount(cpf: String)

    fun reactivateAccount(cpf: String)

    fun deposit(activityRequestDTO: ActivityRequestDTO): ActivityResponseDTO

    fun withdraw(activityRequestDTO: ActivityRequestDTO): ActivityResponseDTO

    fun extract(accNumber: String, page: Int, size: Int): ExtractResponseDTO
}