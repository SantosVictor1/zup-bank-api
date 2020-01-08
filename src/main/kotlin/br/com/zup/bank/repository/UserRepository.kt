package br.com.zup.bank.repository

import br.com.zup.bank.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

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

    /**
     * Método responsável por procurar um usuário pelo CPF
     *
     * @param  cpf  CPF que será usado na busca
     * @return User encontrado
     */
    @Query(value = "SELECT user FROM User user WHERE user.cpf = :cpf AND user.isActive = true")
    fun findByCpf(@Param(value = "cpf") cpf: String?): Optional<User>

    fun findByCpfAndIsActiveFalse(cpf: String): User?

    @Query(value = "SELECT user FROM User user WHERE user.id = :id AND user.isActive = true")
    override fun findById(@Param(value = "id") id: Long): Optional<User>
}