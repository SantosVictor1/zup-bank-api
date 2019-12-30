package br.com.zup.bank.service

import br.com.zup.bank.dto.request.TransferRequestDTO
import br.com.zup.bank.dto.response.success.NewTransferResponseDTO

/**
 * Created by Victor Santos on 30/12/2019
 */
interface ITransferService {

    fun newTransfer(transferRequestDTO: TransferRequestDTO): NewTransferResponseDTO
}