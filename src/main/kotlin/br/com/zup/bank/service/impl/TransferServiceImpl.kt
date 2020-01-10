package br.com.zup.bank.service.impl

import br.com.zup.bank.common.BankErrorCode
import br.com.zup.bank.dto.request.TransferRequestDTO
import br.com.zup.bank.dto.response.success.NewTransferResponseDTO
import br.com.zup.bank.enums.Operation
import br.com.zup.bank.exception.DuplicatedResourceBankException
import br.com.zup.bank.exception.InvalidResourceBankException
import br.com.zup.bank.exception.ResourceNotFoundBankException
import br.com.zup.bank.model.Account
import br.com.zup.bank.model.Activity
import br.com.zup.bank.model.Transfer
import br.com.zup.bank.repository.AccountRepository
import br.com.zup.bank.repository.ActivityRepository
import br.com.zup.bank.repository.TransferRepository
import br.com.zup.bank.service.ITransferService
import org.springframework.stereotype.Service
import java.util.*
import javax.transaction.Transactional

/**
 * Created by Victor Santos on 30/12/2019
 */
@Service
@Transactional
class TransferServiceImpl(
    val transferRepository: TransferRepository,
    val accountRepository: AccountRepository,
    val activityRepository: ActivityRepository
) : ITransferService {
    override fun newTransfer(transferRequestDTO: TransferRequestDTO): NewTransferResponseDTO {
        validateAccounts(transferRequestDTO)

        val originAccount = getAccount(transferRequestDTO.originAccount)
        val destinyAccount = getAccount(transferRequestDTO.destinyAccount)
        var transfer = getTransfer(transferRequestDTO, mutableListOf(originAccount, destinyAccount))

        doTransfer(originAccount, destinyAccount, transferRequestDTO)

        transferRepository.save(transfer)

        return getTransferResponseDTO(transferRequestDTO)
    }

    private fun doTransfer(originAccount: Account, destinyAccount: Account, transferDTO: TransferRequestDTO) {
        originAccount.balance = originAccount.balance - transferDTO.transferValue
        destinyAccount.balance = transferDTO.transferValue + destinyAccount.balance

        if (originAccount.balance < 0) {
            invalidResourceException(BankErrorCode.BANK024.code, "balance", "activityRequestDTO")
        }

        if (destinyAccount.user?.cpf != transferDTO.recipientsCpf) {
            invalidResourceException(BankErrorCode.BANK014.code, "cpf", "activityRequestDTO")
        }

        var originActivity = getActivity(originAccount, transferDTO)
        originActivity.value = originActivity.value * -1
        val destinyActivity = getActivity(destinyAccount, transferDTO)

        activityRepository.saveAll(mutableListOf(originActivity, destinyActivity))

        accountRepository.saveAll(mutableListOf(originAccount, destinyAccount))
    }

    private fun getActivity(acc: Account, transferDTO: TransferRequestDTO): Activity {
        return Activity(null, transferDTO.date, transferDTO.transferValue, Operation.TRANSFER, acc)
    }

    private fun getTransfer(transferDTO: TransferRequestDTO, accounts: MutableList<Account>): Transfer {
        return Transfer(null, transferDTO.transferValue, Date(), accounts)
    }

    private fun getTransferResponseDTO(transferDTO: TransferRequestDTO): NewTransferResponseDTO {
        return NewTransferResponseDTO(Date(), transferDTO.transferValue , transferDTO.destinyAccount)
    }

    private fun validateAccounts(transferDTO: TransferRequestDTO) {
        if (transferDTO.originAccount == transferDTO.destinyAccount) {
            duplicatedResourceException(BankErrorCode.BANK032.code, "", "transferDTO")
        }

        if (transferDTO.transferValue <= 0) {
            invalidResourceException(BankErrorCode.BANK040.code, "transferValue", "transferDTO")
        }

        if (!findAccountByNumber(transferDTO.destinyAccount)) {
            resourceNotFoundException(BankErrorCode.BANK022.code, "destintyAccount", "transferDTO")
        }

        if (!findAccountByNumber(transferDTO.originAccount)) {
            resourceNotFoundException(BankErrorCode.BANK022.code, "originAccount", "transferDTO")
        }
    }

    private fun getAccount(accNumber: String): Account {
        val account = accountRepository.findByAccountNumberAndIsActiveTrue(accNumber)

        if (!account.isPresent) {
            resourceNotFoundException(BankErrorCode.BANK022.code, "accNumber", "Account")
        }

        return account.get()
    }

    private fun findAccountByNumber(accNumber: String): Boolean {
        return accountRepository.existsAccountByAccountNumber(accNumber)
    }

    private fun resourceNotFoundException(errorCode: String, field: String, objectName: String) {
        throw ResourceNotFoundBankException(errorCode, field, objectName)
    }

    private fun duplicatedResourceException(errorCode: String, field: String, objectName: String) {
        throw DuplicatedResourceBankException(errorCode, field, objectName)
    }

    private fun invalidResourceException(errorCode: String, field: String, objectName: String) {
        throw InvalidResourceBankException(errorCode, field, objectName)
    }
}