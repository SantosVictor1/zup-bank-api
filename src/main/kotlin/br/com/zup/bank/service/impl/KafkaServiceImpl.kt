package br.com.zup.bank.service.impl

import br.com.zup.bank.common.BankErrorCode
import br.com.zup.bank.dto.request.TransferRequestDTO
import br.com.zup.bank.dto.response.success.StatusResponseDTO
import br.com.zup.bank.exception.ResourceNotFoundBankException
import br.com.zup.bank.model.Account
import br.com.zup.bank.model.Transfer
import br.com.zup.bank.repository.AccountRepository
import br.com.zup.bank.service.IKafkaService
import br.com.zup.bank.service.ITransferService
import com.google.gson.Gson
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaServiceImpl(
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val transferService: ITransferService,
    private val accountRepository: AccountRepository
) : IKafkaService {
    override fun newTransferRequest(transferRequestDTO: TransferRequestDTO): StatusResponseDTO {
        val originAccount = getAccount(transferRequestDTO.originAccount)
        val destinyAccount = getAccount(transferRequestDTO.destinyAccount)

        var transfer = Transfer.toEntity(transferRequestDTO, originAccount, destinyAccount)

        transfer = transferService.saveTransfer(transfer)

        transferRequestDTO.transferId = transfer.id

        kafkaTemplate.send("bank_api", Gson().toJson(transferRequestDTO))

        return StatusResponseDTO(transfer.id!!, transfer.transferStatus, transfer.errorCode!!)
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
}