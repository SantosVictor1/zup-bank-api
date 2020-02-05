package br.com.zup.bank.service

import br.com.zup.bank.dto.request.BlacklistRequestDTO
import br.com.zup.bank.dto.response.success.BlacklistResponseDTO

interface IBlacklistService {
    fun saveNewCpf(blacklistRequestDTO: BlacklistRequestDTO): BlacklistResponseDTO
    fun findByCpf(cpf: String): BlacklistResponseDTO
    fun existsByCpf(cpf: String): Boolean
}