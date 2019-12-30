package br.com.zup.bank.service.impl

import br.com.zup.bank.dto.request.TransferRequestDTO
import br.com.zup.bank.dto.response.success.NewTransferResponseDTO
import br.com.zup.bank.enums.Operation
import br.com.zup.bank.exception.BankException
import br.com.zup.bank.model.Account
import br.com.zup.bank.model.Activity
import br.com.zup.bank.model.Transfer
import br.com.zup.bank.repository.AccountRepository
import br.com.zup.bank.repository.ActivityRepository
import br.com.zup.bank.repository.TransferRepository
import br.com.zup.bank.service.ITransferService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional

/**
 * Created by Victor Santos on 30/12/2019
 */
@Service
@Transactional
class TransferServiceImpl : ITransferService {
    @Autowired
    private lateinit var transferRepository: TransferRepository
    @Autowired
    private lateinit var accountRepository: AccountRepository
    @Autowired
    private lateinit var activityRepository: ActivityRepository

    override fun newTransfer(transferRequestDTO: TransferRequestDTO): NewTransferResponseDTO {
        validateAccounts(transferRequestDTO)

        val originAccount = getAccount(transferRequestDTO.originAccount!!)
        val destinyAccount = getAccount(transferRequestDTO.destinyAccount!!)
        var transfer = getTransfer(transferRequestDTO, mutableListOf(originAccount, destinyAccount))

        doTransfer(originAccount, destinyAccount, transferRequestDTO)

        transferRepository.save(transfer)

        return getTransferResponseDTO(transferRequestDTO)
    }

    override fun getAllTransfer(): MutableList<Transfer> {
        return transferRepository.findAll()
    }

    override fun getById(id: Long): Transfer {
        return transferRepository.findById(id).get()
    }

    private fun doTransfer(originAccount: Account, destinyAccount: Account, transferDTO: TransferRequestDTO) {
        originAccount.balance = originAccount.balance!! - transferDTO.transferValue!!
        destinyAccount.balance = transferDTO.transferValue!! + destinyAccount.balance!!

        if (originAccount.balance!! < 0) {
            throw BankException(400, "Saldo insuficiente da conta de origem")
        }

        var originActivity = getActivity(originAccount, transferDTO)
        originActivity.value = originActivity.value!! * -1
        val destinyActivity = getActivity(destinyAccount, transferDTO)

        activityRepository.saveAll(mutableListOf(originActivity, destinyActivity))

        accountRepository.saveAll(mutableListOf(originAccount, destinyAccount))
    }

    private fun getActivity(acc: Account, transferDTO: TransferRequestDTO): Activity {
        return Activity(null, Date(), transferDTO.transferValue, Operation.TRANSFER, acc, acc.user)
    }

    private fun getTransfer(transferDTO: TransferRequestDTO, accounts: MutableList<Account>): Transfer {
        return Transfer(null, transferDTO.transferValue, Date(), accounts)
    }

    private fun getTransferResponseDTO(transferDTO: TransferRequestDTO): NewTransferResponseDTO {
        val message = "Transferência no valor de ${transferDTO.transferValue} " +
            "reais para a conta ${transferDTO.destinyAccount} realizada com sucesso"

        return NewTransferResponseDTO(Date(), message)
    }

    private fun validateAccounts(transferDTO: TransferRequestDTO) {
        if (transferDTO.originAccount == transferDTO.destinyAccount) {
            throw BankException(400, "Números de contas iguais")
        }

        if (transferDTO.transferValue!! <= 0) {
            throw BankException(400, "Valor deve ser maior que 0")
        }

        if (!findAccountByNumber(transferDTO.destinyAccount!!)) {
            throw BankException(404, "Conta de destino não encontrada")
        }

        if (!findAccountByNumber(transferDTO.originAccount!!)) {
            throw BankException(404, "Conta de origem não encontrada")
        }
    }

    private fun getAccount(accNumber: String): Account {
        return accountRepository.findByAccountNumber(accNumber).get()
    }

    private fun findAccountByNumber(accNumber: String): Boolean {
        return accountRepository.existsAccountByAccountNumber(accNumber)
    }
}