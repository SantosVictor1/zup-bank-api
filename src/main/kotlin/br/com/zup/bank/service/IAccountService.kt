package br.com.zup.bank.service

import br.com.zup.bank.dto.request.AccountRequestDTO
import br.com.zup.bank.dto.response.success.AccountResponseDTO

/**
 * Created by Victor Santos on 26/12/2019
 */
interface IAccountService {

    fun createAccount(accountRequestDTO: AccountRequestDTO): AccountResponseDTO

    fun getAll(): MutableList<AccountResponseDTO>

    fun getById(id: Long): AccountResponseDTO

    fun getByCpf(cpf: String): AccountResponseDTO

    fun getByAccountNumber(accNumber: String): AccountResponseDTO
}