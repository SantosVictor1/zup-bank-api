package br.com.zup.bank.repository

import br.com.zup.bank.model.Account
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

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

    /**
     * Método responsável por encontrar uma conta pelo cpf do usuário
     *
     * @param  cpf  CPF que será usado na busca
     * @return Account se encontrar ou null caso contrário
     */
    fun findByUserCpf(cpf: String): Account?

    /**
     * Método responsável por encontrar uma conta ativa pelo seu número
     *
     * @param  accNumber  Número de conta que será usado na busca
     * @return Account se encontrar ou null caso contrário
     */
    fun findByAccountNumberAndIsActiveTrue(accNumber: String): Account?

    /**
     * Método responsável por encontrar um conta ativa baseado em seu número ou pelo cpf do usuário
     *
     * @param  accNumber  Número de conta que será usado na busca
     * @param  cpf  CPF que será usado na busca
     * @return Account se encontrar ou null caso contrário
     */
    @Query(value = "SELECT acc FROM Account acc WHERE acc.isActive = true AND (acc.accountNumber = :accNumber OR acc.user.cpf = :cpf)")
    fun findByAccountNumberOrUserCpf(
        @Param(value = "cpf") cpf: String,
        @Param(value = "accNumber") accNumber: String
    ): Account?
}