package br.com.zup.bank.repository

import br.com.zup.bank.model.Transfer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Created by Victor Santos on 30/12/2019
 */
@Repository
interface TransferRepository : JpaRepository<Transfer, Long> {
}