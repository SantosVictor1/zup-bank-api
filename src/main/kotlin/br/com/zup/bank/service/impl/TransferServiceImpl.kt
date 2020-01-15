package br.com.zup.bank.service.impl

import br.com.zup.bank.common.BankErrorCode
import br.com.zup.bank.config.KafkaConsumerConfig
import br.com.zup.bank.dto.request.TransferRequestDTO
import br.com.zup.bank.dto.response.success.NewTransferResponseDTO
import br.com.zup.bank.dto.response.success.StatusResponseDTO
import br.com.zup.bank.enums.Operation
import br.com.zup.bank.enums.Status
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
import java.util.*
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
    private lateinit var transfer: Transfer

    override fun newTransfer(transferRequestDTO: TransferRequestDTO): NewTransferResponseDTO {
        validateAccounts(transferRequestDTO)

        val originAccount = getAccount(transferRequestDTO.originAccount)
        val destinyAccount = getAccount(transferRequestDTO.destinyAccount)

        doTransfer(originAccount, destinyAccount, transferRequestDTO)

        this.transfer.transferStatus = Status.COMPLETED
        transferRepository.save(this.transfer)

        return getTransferResponseDTO(transferRequestDTO)
    }

    override fun saveTransfer(transfer: Transfer): Transfer {
        return transferRepository.save(transfer)
    }

    @Transactional
    override fun doTransfer(originAccount: Account, destinyAccount: Account, transferDTO: TransferRequestDTO) {
        originAccount.balance = originAccount.balance - transferDTO.transferValue
        destinyAccount.balance = transferDTO.transferValue + destinyAccount.balance

        if (originAccount.balance < 0) {
            invalidResourceException(BankErrorCode.BANK024.code, "balance", "activityRequestDTO")
        }

        if (destinyAccount.user?.cpf != transferDTO.recipientsCpf) {
            invalidResourceException(BankErrorCode.BANK014.code, "cpf", "activityRequestDTO")
        }

        val originActivity = getActivity(originAccount, transferDTO)
        val destinyActivity = getActivity(destinyAccount, transferDTO)

        activityRepository.saveAll(mutableListOf(originActivity, destinyActivity))

        accountRepository.saveAll(mutableListOf(originAccount, destinyAccount))
    }

    @KafkaListener(topics = ["bank_api"], groupId = "group-id")
    fun listen(transferDto: String) {
        val transferRequestDTO = Gson().fromJson(transferDto, TransferRequestDTO::class.java)
        this.transfer = transferRepository.findById(transferRequestDTO.transferId!!).get()

        log.info("#===============> Object <===============#\n\n")
        log.info(transferRequestDTO.toString())

        newTransfer(transferRequestDTO)
    }

    private fun getActivity(acc: Account, transferDTO: TransferRequestDTO): Activity {
        return Activity(null, transferDTO.date, transferDTO.transferValue, Operation.TRANSFER, acc)
    }

    private fun getTransferResponseDTO(transferDTO: TransferRequestDTO): NewTransferResponseDTO {
        return NewTransferResponseDTO(Date(), transferDTO.transferValue, transferDTO.destinyAccount)
    }

    private fun validateAccounts(transferDTO: TransferRequestDTO) {
        if (transferDTO.originAccount == transferDTO.destinyAccount) {
            duplicatedResourceException(BankErrorCode.BANK032.code, "", TransferRequestDTO::class.simpleName!!)
        }

        if (transferDTO.transferValue <= 0) {
            invalidResourceException(
                BankErrorCode.BANK040.code,
                TransferRequestDTO::transferValue.name,
                TransferRequestDTO::class.simpleName!!
            )
        }
    }

    private fun getAccount(accNumber: String): Account {
        val account = accountRepository.findByAccountNumberAndIsActiveTrue(accNumber)

        if (account == null) {
            resourceNotFoundException(
                BankErrorCode.BANK022.code,
                Account::accountNumber.name,
                Account::class.simpleName!!
            )
        }

        return account!!
    }

    private fun resourceNotFoundException(errorCode: String, field: String, objectName: String) {
        transfer.transferStatus = Status.FAILED
        transferRepository.save(transfer)

        throw ResourceNotFoundBankException(errorCode, field, objectName)
    }

    private fun duplicatedResourceException(errorCode: String, field: String, objectName: String) {
        transfer.transferStatus = Status.FAILED
        transferRepository.save(transfer)

        throw DuplicatedResourceBankException(errorCode, field, objectName)
    }

    private fun invalidResourceException(errorCode: String, field: String, objectName: String) {
        transfer.transferStatus = Status.FAILED
        transferRepository.save(transfer)

        throw InvalidResourceBankException(errorCode, field, objectName)
    }
}