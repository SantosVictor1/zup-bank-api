package br.com.zup.bank.repository

import br.com.zup.bank.model.Blacklist
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BlacklistRepository : JpaRepository<Blacklist, Long> {
    fun existsByCpf(cpf: String): Boolean

    fun findByCpf(cpf: String): Blacklist?

    fun deleteByCpf(cpf: String)
}