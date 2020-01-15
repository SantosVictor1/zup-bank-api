package br.com.zup.bank.service

import br.com.zup.bank.dto.request.TransferRequestDTO
import br.com.zup.bank.dto.response.success.StatusResponseDTO

interface IKafkaService {
    fun newTransferRequest(transferRequestDTO: TransferRequestDTO): StatusResponseDTO
}