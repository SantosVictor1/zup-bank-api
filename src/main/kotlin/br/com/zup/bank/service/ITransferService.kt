package br.com.zup.bank.service

import br.com.zup.bank.dto.request.TransferRequestDTO
import br.com.zup.bank.dto.response.success.StatusResponseDTO
import br.com.zup.bank.model.Account
import br.com.zup.bank.model.Transfer

/**
 * Created by Victor Santos on 30/12/2019
 */
interface ITransferService {
    fun newTransfer(transferRequestDTO: TransferRequestDTO, transfer: Transfer)

    fun saveTransfer(transfer: Transfer): Transfer

    fun doTransfer(originAccount: Account, destinyAccount: Account, transferDTO: TransferRequestDTO)

    fun getTransferStatus(id: Long): StatusResponseDTO

    fun listen(transferDTO: String)
}