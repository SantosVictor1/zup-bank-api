package br.com.zup.bank.repository

import br.com.zup.bank.model.Account
import org.springframework.data.jpa.repository.JpaRepository
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
     * Método responsável por encontrar um conta baseado no CPF do usuário
     *
     * @param  cpf  CPF que será usado na busca
     * @return Optional
     */
    fun findByUserCpf(cpf: String): Optional<Account>
}