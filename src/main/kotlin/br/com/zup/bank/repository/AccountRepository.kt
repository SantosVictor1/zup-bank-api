package br.com.zup.bank.repository

import br.com.zup.bank.model.Account
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

/**
 * Created by Victor Santos on 26/12/2019
 */
@Repository
interface AccountRepository : JpaRepository<Account, Long> {

    /**
     * Método responsável por verificar se existe uma conta relacionada com o CPF
     *
     * @param  cpf  CPF que será usado na busca
     * @return true se já existir
     */
    fun existsAccountByUserCpf(cpf: String): Boolean

    /**
     * Método responsável por verificar se existe uma conta pelo seu número
     *
     * @param  accNumber  Número de conta que será usado na busca
     * @return true se já existir
     */
    fun existsAccountByAccountNumber(accNumber: String): Boolean

    fun findByUserCpf(cpf: String): Optional<Account>

    fun findByAccountNumberAndIsActiveTrue(accNumber: String): Optional<Account>

    /**
     * Método responsável por encontrar um conta baseado em seu número
     *
     * @param  accNumber  AccountNumber que será usado na busca
     * @return Optional
     */
    @Query(value = "SELECT acc FROM Account acc WHERE acc.isActive = true AND (acc.accountNumber = :accNumber OR acc.user.cpf = :cpf)")
    fun findByAccountNumberOrUserCpf(
        @Param(value = "cpf") cpf: String,
        @Param(value = "accNumber") accNumber: String
    ): Optional<Account>
}