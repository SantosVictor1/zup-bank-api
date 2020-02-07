package br.com.zup.bank.service.impl

import br.com.zup.bank.common.BankErrorCode
import br.com.zup.bank.common.Message
import br.com.zup.bank.dto.response.success.UserStatusDTO
import br.com.zup.bank.enums.Status
import br.com.zup.bank.exception.MultipleRegisterBankException
import br.com.zup.bank.model.WaitList
import br.com.zup.bank.repository.WaitListRepository
import br.com.zup.bank.service.IUserService
import br.com.zup.bank.service.IWaitListService
import org.springframework.stereotype.Service

@Service
class WaitListServiceImpl(
    private val waitListRepository: WaitListRepository,
    private val userService: IUserService,
    private val message: Message
) : IWaitListService {
    override fun saveOnWait(cpf: String): UserStatusDTO {
        var waitList = findByCpf(cpf)

        if (waitList != null) {
            waitList.status = Status.IN_PROCESS
            waitList.message = ""
            waitListRepository.save(waitList)
        } else {
            waitList = waitListRepository.save(WaitList.toEntity(cpf))
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
        val waitList = findByCpf(cpf)

        return if (waitList == null) {
            userService.getByCpf(cpf, true)
            val message = message.getMessage(BankErrorCode.BANK072.code)
            UserStatusDTO.toDto(cpf, Status.COMPLETED, message)
        } else {
            val message = message.getMessage(waitList.message)
            UserStatusDTO.toDto(cpf, waitList.status, message)
        }
    }

    override fun validateRequest(cpf: String) {
        val waitList = findByCpf(cpf)

        if (waitList?.status == Status.IN_PROCESS) {
            throw MultipleRegisterBankException(
                BankErrorCode.BANK071.code,
                "",
                WaitList::class.simpleName!!
            )
        }
    }

    private fun findByCpf(cpf: String): WaitList? {
        return waitListRepository.findByCpf(cpf)
    }
}