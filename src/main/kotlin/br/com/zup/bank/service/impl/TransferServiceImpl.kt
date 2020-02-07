package br.com.zup.bank.service.impl

import br.com.zup.bank.common.BankErrorCode
import br.com.zup.bank.common.Message
import br.com.zup.bank.dto.request.ActivityRequestDTO
import br.com.zup.bank.dto.request.TransferRequestDTO
import br.com.zup.bank.dto.response.success.StatusResponseDTO
import br.com.zup.bank.enums.Operation
import br.com.zup.bank.enums.Status
import br.com.zup.bank.exception.BankException
import br.com.zup.bank.exception.DuplicatedResourceBankException
import br.com.zup.bank.exception.InvalidResourceBankException
import br.com.zup.bank.exception.ResourceNotFoundBankException
import br.com.zup.bank.model.Account
import br.com.zup.bank.model.Activity
import br.com.zup.bank.model.Transfer
import br.com.zup.bank.model.User
import br.com.zup.bank.repository.AccountRepository
import br.com.zup.bank.repository.ActivityRepository
import br.com.zup.bank.repository.TransferRepository
import br.com.zup.bank.service.ITransferService
import com.google.gson.Gson
import org.springframework.data.jpa.repository.Lock
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import java.util.*
import javax.persistence.LockModeType
import javax.transaction.Transactional

/**
 * Created by Victor Santos on 30/12/2019
 */
@Service
class TransferServiceImpl(
    private val transferRepository: TransferRepository,
    private val accountRepository: AccountRepository,
    private val activityRepository: ActivityRepository,
    private val messageResource: Message
) : ITransferService {
    @KafkaListener(topics = ["bank_api"], groupId = "bank_id")
    override fun listen(transferDTO: String) {
        lateinit var transfer: Transfer
        try {
            val transferRequestDTO = Gson().fromJson(transferDTO, TransferRequestDTO::class.java)
            transfer = transferRepository.findById(transferRequestDTO.transferId!!).get()

            newTransfer(transferRequestDTO, transfer)
        } catch (ex: BankException) {
            transfer.errorCode = ex.errorCode
            transfer.transferStatus = Status.FAILED
            transferRepository.save(transfer)
        } catch (ex: Exception) {
            transfer.errorCode = BankErrorCode.BANK050.code
            transfer.transferStatus = Status.FAILED
            transferRepository.save(transfer)
        }
    }

    override fun newTransfer(transferRequestDTO: TransferRequestDTO, transfer: Transfer) {
        validateAccounts(transferRequestDTO)

        val originAccount = getAccount(transferRequestDTO.originAccount)
        val destinyAccount = getAccount(transferRequestDTO.destinyAccount)

        doTransfer(originAccount, destinyAccount, transferRequestDTO)

        transfer.transferStatus = Status.COMPLETED
        saveTransfer(transfer)
    }

    override fun saveTransfer(transfer: Transfer): Transfer {
        return transferRepository.save(transfer)
    }

    @Transactional
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    override fun doTransfer(originAccount: Account, destinyAccount: Account, transferDTO: TransferRequestDTO) {
        originAccount.balance = originAccount.balance - transferDTO.transferValue
        destinyAccount.balance = transferDTO.transferValue + destinyAccount.balance

        if (originAccount.balance < 0) {
            invalidResourceException(
                BankErrorCode.BANK024.code,
                Account::balance.name,
                ActivityRequestDTO::class.simpleName!!
            )
        }

        if (destinyAccount.user?.cpf != transferDTO.recipientsCpf) {
            invalidResourceException(
                BankErrorCode.BANK014.code,
                User::cpf.name,
                ActivityRequestDTO::class.simpleName!!
            )
        }

        val originActivity = getActivity(originAccount, transferDTO, Operation.NEGATIVE_TRANSFER)
        val destinyActivity = getActivity(destinyAccount, transferDTO, Operation.POSITIVE_TRANSFER)

        activityRepository.saveAll(mutableListOf(originActivity, destinyActivity))

        accountRepository.saveAll(mutableListOf(originAccount, destinyAccount))
    }

    override fun getTransferStatus(id: Long): StatusResponseDTO {
        val transfer = transferRepository.findById(id)

        if (!transfer.isPresent) {
            resourceNotFoundException(
                BankErrorCode.BANK033.code,
                Transfer::id.name,
                Transfer::class.simpleName!!
            )
        }

        val message = messageResource.getMessage(transfer.get().errorCode!!)

        return StatusResponseDTO(transfer.get().id!!, transfer.get().transferStatus, message)
    }

    private fun getActivity(acc: Account, transferDTO: TransferRequestDTO, operation: Operation): Activity {
        return Activity(null, Date(), transferDTO.transferValue, operation, acc)
    }

    private fun validateAccounts(transferDTO: TransferRequestDTO) {
        if (transferDTO.originAccount == transferDTO.destinyAccount) {
            duplicatedResourceException(
                BankErrorCode.BANK032.code,
                Account::accountNumber.name,
                TransferRequestDTO::class.simpleName!!
            )
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
        throw ResourceNotFoundBankException(errorCode, field, objectName)
    }

    private fun duplicatedResourceException(errorCode: String, field: String, objectName: String) {
        throw DuplicatedResourceBankException(errorCode, field, objectName)
    }

    private fun invalidResourceException(errorCode: String, field: String, objectName: String) {
        throw InvalidResourceBankException(errorCode, field, objectName)
    }
}