package br.com.zup.bank.service

import br.com.zup.bank.dto.request.AccountRequestDTO
import br.com.zup.bank.dto.response.success.AccountBalanceDTO
import br.com.zup.bank.dto.response.success.AccountResponseDTO

/**
 * Created by Victor Santos on 26/12/2019
 */
interface IAccountService {

    fun createAccount(accountRequestDTO: AccountRequestDTO): AccountResponseDTO

    fun getAll(): MutableList<AccountResponseDTO>

    fun getById(id: Long): AccountResponseDTO

    fun getByAccountNumberOrCpf(accNumber: String): AccountResponseDTO

    fun getAccountBalance(accNumber: String): AccountBalanceDTO
}