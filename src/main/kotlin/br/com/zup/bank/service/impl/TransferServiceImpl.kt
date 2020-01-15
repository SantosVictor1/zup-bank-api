package br.com.zup.bank.service.impl

import br.com.zup.bank.common.BankErrorCode
import br.com.zup.bank.config.KafkaConsumerConfig
import br.com.zup.bank.dto.request.TransferRequestDTO
import br.com.zup.bank.enums.Operation
import br.com.zup.bank.enums.Status
import br.com.zup.bank.exception.BankException
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
import com.google.gson.Gson
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import javax.transaction.Transactional

/**
 * Created by Victor Santos on 30/12/2019
 */
@Service
class TransferServiceImpl(
    val transferRepository: TransferRepository,
    val accountRepository: AccountRepository,
    val activityRepository: ActivityRepository
) : ITransferService {
    private val log: Logger = LoggerFactory.getLogger(KafkaConsumerConfig::class.java)

    override fun newTransfer(transferRequestDTO: TransferRequestDTO, transfer: Transfer?) {
        validateAccounts(transferRequestDTO, transfer!!)

        val originAccount = getAccount(transferRequestDTO.originAccount, transfer)
        val destinyAccount = getAccount(transferRequestDTO.destinyAccount, transfer)

        doTransfer(originAccount, destinyAccount, transferRequestDTO, transfer)

        transfer.transferStatus = Status.COMPLETED
        transferRepository.save(transfer)
    }

    override fun saveTransfer(transfer: Transfer): Transfer {
        return transferRepository.save(transfer)
    }

    @Transactional
    override fun doTransfer(originAccount: Account, destinyAccount: Account, transferDTO: TransferRequestDTO, transfer: Transfer?) {
        originAccount.balance = originAccount.balance - transferDTO.transferValue
        destinyAccount.balance = transferDTO.transferValue + destinyAccount.balance

        if (originAccount.balance < 0) {
            invalidResourceException(BankErrorCode.BANK024.code, "balance", "activityRequestDTO", transfer!!)
        }

        if (destinyAccount.user?.cpf != transferDTO.recipientsCpf) {
            invalidResourceException(BankErrorCode.BANK014.code, "cpf", "activityRequestDTO", transfer!!)
        }

        val originActivity = getActivity(originAccount, transferDTO)
        val destinyActivity = getActivity(destinyAccount, transferDTO)

        activityRepository.saveAll(mutableListOf(originActivity, destinyActivity))

        accountRepository.saveAll(mutableListOf(originAccount, destinyAccount))
    }

    @KafkaListener(topics = ["bank_api"], groupId = "group-id")
    fun listen(transferDto: String) {
        val transferRequestDTO = Gson().fromJson(transferDto, TransferRequestDTO::class.java)
        val transfer = transferRepository.findById(transferRequestDTO.transferId!!).get()

        log.info("#===============> Object <===============#\n\n")
        log.info(transferRequestDTO.toString())

        try {
            newTransfer(transferRequestDTO, transfer)
        } catch (ex: BankException) {
            transfer.transferStatus = Status.FAILED
            transferRepository.save(transfer)
        }
    }

    private fun getActivity(acc: Account, transferDTO: TransferRequestDTO): Activity {
        return Activity(null, transferDTO.date, transferDTO.transferValue, Operation.TRANSFER, acc)
    }

    private fun validateAccounts(transferDTO: TransferRequestDTO, transfer: Transfer) {
        if (transferDTO.originAccount == transferDTO.destinyAccount) {
            duplicatedResourceException(BankErrorCode.BANK032.code, "", TransferRequestDTO::class.simpleName!!, transfer)
        }

        if (transferDTO.transferValue <= 0) {
            invalidResourceException(
                BankErrorCode.BANK040.code,
                TransferRequestDTO::transferValue.name,
                TransferRequestDTO::class.simpleName!!,
                transfer
            )
        }
    }

    private fun getAccount(accNumber: String, transfer: Transfer): Account {
        val account = accountRepository.findByAccountNumberAndIsActiveTrue(accNumber)

        if (account == null) {
            resourceNotFoundException(
                BankErrorCode.BANK022.code,
                Account::accountNumber.name,
                Account::class.simpleName!!,
                transfer
            )
        }

        return account!!
    }

    private fun resourceNotFoundException(errorCode: String, field: String, objectName: String, transfer: Transfer) {
        transfer.transferStatus = Status.FAILED
        transferRepository.save(transfer)

        throw ResourceNotFoundBankException(errorCode, field, objectName)
    }

    private fun duplicatedResourceException(errorCode: String, field: String, objectName: String, transfer: Transfer) {
        transfer.transferStatus = Status.FAILED
        transferRepository.save(transfer)

        throw DuplicatedResourceBankException(errorCode, field, objectName)
    }

    private fun invalidResourceException(errorCode: String, field: String, objectName: String, transfer: Transfer) {
        transfer.transferStatus = Status.FAILED
        transferRepository.save(transfer)

        throw InvalidResourceBankException(errorCode, field, objectName)
    }
}