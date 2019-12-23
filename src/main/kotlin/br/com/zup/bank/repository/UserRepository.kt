package br.com.zup.bank.repository

import br.com.zup.bank.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Created by Victor Santos on 23/12/2019
 */
@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun existsByCpf(cpf: String?): Boolean
    fun existsByEmail(email: String?): Boolean
}