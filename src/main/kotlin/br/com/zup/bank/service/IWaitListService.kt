package br.com.zup.bank.service

import br.com.zup.bank.dto.response.success.UserStatusDTO

interface IWaitListService {
    fun saveOnWait(cpf: String): UserStatusDTO

    fun disapproveRegister(cpf: String)

    fun removeFromList(cpf: String)

    fun getStatus(cpf: String): UserStatusDTO
}