package br.com.zup.bank.repository

import br.com.zup.bank.model.WaitList
import org.springframework.data.jpa.repository.JpaRepository

interface WaitListRepository : JpaRepository<WaitList, Long> {
    fun existsByCpf(cpf: String): Boolean

    fun findByCpf(cpf: String): WaitList?

    fun deleteByCpf(cpf: String)
}