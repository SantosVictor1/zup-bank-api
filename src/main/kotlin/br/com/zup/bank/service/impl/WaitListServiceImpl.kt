package br.com.zup.bank.service.impl

import br.com.zup.bank.common.BankErrorCode
import br.com.zup.bank.common.Message
import br.com.zup.bank.dto.response.success.UserStatusDTO
import br.com.zup.bank.enums.Status
import br.com.zup.bank.exception.MultipleRegisterBankException
import br.com.zup.bank.model.Waitlist
import br.com.zup.bank.repository.WaitListRepository
import br.com.zup.bank.service.IWaitListService
import org.springframework.stereotype.Service

@Service
class WaitListServiceImpl(
    private val waitListRepository: WaitListRepository,
    private val message: Message
) : IWaitListService {
    override fun saveOnWait(cpf: String): UserStatusDTO {
        var waitList = findByCpf(cpf)

        if (waitList != null) {
            if (waitList.status == Status.IN_PROCESS) {
                throw MultipleRegisterBankException(
                    BankErrorCode.BANK071.code,
                    "",
                    Waitlist::class.simpleName!!
                )
            }
            waitList.status = Status.IN_PROCESS
            waitListRepository.save(waitList)
        } else {
            waitList = waitListRepository.save(Waitlist.toEntity(cpf))
        }

        return UserStatusDTO.toDto(waitList.cpf, waitList.status)
    }

    override fun disapproveRegister(cpf: String) {
        val waitList = findByCpf(cpf)!!
        waitList.status = Status.FAILED
        waitList.message = BankErrorCode.BANK070.code

        waitListRepository.save(waitList)
    }

    override fun removeFromList(cpf: String) {
        waitListRepository.deleteByCpf(cpf)
    }

    override fun getStatus(cpf: String): UserStatusDTO {
        val waitList = findByCpf(cpf)!!
        val message = message.getMessage(waitList.message)

        return UserStatusDTO.toDto(waitList.cpf, waitList.status, message)
    }

    private fun findByCpf(cpf: String): Waitlist? {
        return waitListRepository.findByCpf(cpf)
    }
}