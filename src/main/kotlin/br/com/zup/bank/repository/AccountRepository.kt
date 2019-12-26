package br.com.zup.bank.repository

import br.com.zup.bank.model.Account
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Created by Victor Santos on 26/12/2019
 */
interface AccountRepository : JpaRepository<Account, Long> {
}