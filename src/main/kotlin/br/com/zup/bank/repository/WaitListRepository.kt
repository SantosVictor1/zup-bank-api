package br.com.zup.bank.repository

import br.com.zup.bank.model.Waitlist
import org.springframework.data.jpa.repository.JpaRepository

interface WaitListRepository : JpaRepository<Waitlist, Long> {
    fun existsByCpf(cpf: String): Boolean

    fun findByCpf(cpf: String): Waitlist?

    fun deleteByCpf(cpf: String)
}