package br.com.zup.bank.service

import br.com.zup.bank.dto.request.TransferRequestDTO
import br.com.zup.bank.dto.response.success.TransferResponseDTO
import br.com.zup.bank.model.Transfer

/**
 * Created by Victor Santos on 30/12/2019
 */
interface ITransferService {

    fun newTransfer(transferRequestDTO: TransferRequestDTO): TransferResponseDTO

    fun getAllTransfer(): MutableList<Transfer>

    fun getById(id: Long): Transfer
}