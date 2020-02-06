package br.com.zup.bank.service.impl

import br.com.zup.bank.common.BankErrorCode
import br.com.zup.bank.dto.request.BlacklistRequestDTO
import br.com.zup.bank.dto.response.success.BlacklistResponseDTO
import br.com.zup.bank.exception.DuplicatedResourceBankException
import br.com.zup.bank.exception.ResourceNotFoundBankException
import br.com.zup.bank.model.Blacklist
import br.com.zup.bank.repository.BlacklistRepository
import br.com.zup.bank.service.IBlacklistService
import org.springframework.stereotype.Service

@Service
class BlacklistServiceImpl(
    private val blacklistRepository: BlacklistRepository
) : IBlacklistService {
    override fun saveNewCpf(blacklistRequestDTO: BlacklistRequestDTO): BlacklistResponseDTO {
        existsByCpf(blacklistRequestDTO.cpf)

        val blacklist = Blacklist.toEntity(blacklistRequestDTO)

        return BlacklistResponseDTO.toDto(blacklistRepository.save(blacklist))
    }

    override fun findByCpf(cpf: String): BlacklistResponseDTO {
        val blacklist = blacklistRepository.findByCpf(cpf) ?: throw ResourceNotFoundBankException(
            BankErrorCode.BANK060.code,
            BlacklistRequestDTO::cpf.name,
            Blacklist::class.simpleName!!
        )

        return BlacklistResponseDTO.toDto(blacklist)
    }

    override fun existsByCpf(cpf: String): Boolean {
        return blacklistRepository.existsByCpf(cpf)
    }

    override fun removeFromList(cpf: String) {
        if (existsByCpf(cpf)) {
            blacklistRepository.deleteByCpf(cpf)
        }
    }
}