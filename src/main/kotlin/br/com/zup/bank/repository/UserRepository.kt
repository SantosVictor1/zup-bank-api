package br.com.zup.bank.repository

import br.com.zup.bank.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Created by Victor Santos on 23/12/2019
 */
@Repository
interface UserRepository : JpaRepository<User, Long> {

    /**
     * Método responsável por verificar se o cpf já existe
     *
     * @param  cpf  CPF para verificar duplicidade
     * @return true se já existir o CPF
     */
    fun existsByCpf(cpf: String?): Boolean

    /**
     * Método responsável por verificar se o email já existe
     *
     * @param  email  Email para verificar duplicidade
     * @return true se já existir o CPF
     */
    fun existsByEmail(email: String?): Boolean
}