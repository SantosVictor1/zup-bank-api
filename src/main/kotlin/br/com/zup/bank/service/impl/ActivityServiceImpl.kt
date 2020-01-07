package br.com.zup.bank.service.impl

import br.com.zup.bank.dto.request.ActivityRequestDTO
import br.com.zup.bank.dto.response.success.ActivityResponseDTO
import br.com.zup.bank.dto.response.success.ExtractDataDTO
import br.com.zup.bank.dto.response.success.ExtractResponseDTO
import br.com.zup.bank.dto.response.success.PaginationResponseDTO
import br.com.zup.bank.enums.Operation
import br.com.zup.bank.exception.BankException
import br.com.zup.bank.exception.ResourceNotFoundException
import br.com.zup.bank.model.Account
import br.com.zup.bank.model.Activity
import br.com.zup.bank.model.User
import br.com.zup.bank.repository.AccountRepository
import br.com.zup.bank.repository.ActivityRepository
import br.com.zup.bank.repository.UserRepository
import br.com.zup.bank.service.IActivityService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Created by Victor Santos on 27/12/2019
 */
@Service
@Transactional
class ActivityServiceImpl(
    val activityRepository: ActivityRepository,
    val accountRepository: AccountRepository,
    val userRepository: UserRepository
) : IActivityService {
    private lateinit var user: User
    private lateinit var acc: Account

    override fun operation(activityDTO: ActivityRequestDTO): ActivityResponseDTO {
        user = getUser(activityDTO.cpf!!)
        acc = getAccount(activityDTO.accNumber!!)
        if (activityDTO.operation!! == Operation.DEPOSIT) {
            return deposit(activityDTO)
        }

        return withdraw(activityDTO)
    }

    override fun extract(accNumber: String, page: Int, size: Int): ExtractResponseDTO {
        getAccount(accNumber)

        var pageRequest = PageRequest.of(page, size)
        val extracts = activityRepository.findAllByAccountAccountNumberOrderByActivityDateDesc(accNumber, pageRequest)

        return getExtractResponseDTO(extracts, page, size)
    }

    private fun deposit(activityDTO: ActivityRequestDTO): ActivityResponseDTO {
        acc.balance = activityDTO.value!! + acc.balance!!

        accountRepository.save(acc)
        val activity = activityRepository.save(getActivity(user, acc, activityDTO))

        return getActivityResponseDTO(acc, activity)
    }

    private fun withdraw(activityDTO: ActivityRequestDTO): ActivityResponseDTO {
        acc.balance = acc.balance!! - activityDTO.value!!
        validateWithdraw(acc.balance!!)

        accountRepository.save(acc)
        var activity = getActivity(user, acc, activityDTO)
        activity.value = activity.value!! * -1

        activity = activityRepository.save(activity)

        return getActivityResponseDTO(acc, activity)
    }

    private fun validateWithdraw(balance: Double) {
        if (balance < 0.0) {
            badRequest(mutableListOf("Saldo insuficiente!"))
        }
    }

    private fun getActivityResponseDTO(acc: Account, activity: Activity): ActivityResponseDTO {
        return ActivityResponseDTO(acc.balance, acc.accountNumber, activity.activityDate, activity.operation.toString())
    }

    private fun getExtractResponseDTO(activities: Page<Activity>, page: Int, size: Int): ExtractResponseDTO {
        val pagination = PaginationResponseDTO(page, size)
        var extractDataDTO = mutableListOf<ExtractDataDTO>()

        activities.forEach {
            extractDataDTO.add(ExtractDataDTO(it.activityDate, it.value, it.operation))
        }

        return ExtractResponseDTO(extractDataDTO, pagination)
    }

    private fun getActivity(user: User, acc: Account, activityDTO: ActivityRequestDTO): Activity {
        return Activity(
            id = null,
            value = activityDTO.value,
            operation = activityDTO.operation!!,
            account = acc,
            user = user
        )
    }

    private fun getUser(cpf: String): User {
        val user = userRepository.findByCpf(cpf)

        if (!user.isPresent) {
            resourceNotFound(mutableListOf("Usuário não encontrado"))
        }

        return user.get()
    }

    private fun getAccount(accNumber: String): Account {
        val acc = accountRepository.findByAccountNumberOrUserCpf(accNumber)

        if (!acc.isPresent) {
            resourceNotFound(mutableListOf("Conta não encontrada"))
        }

        return acc.get()
    }

    private fun resourceNotFound(errors: MutableList<String>) {
        if (errors.size > 0) {
            throw ResourceNotFoundException(errors)
        }
    }

    private fun badRequest(errors: MutableList<String>) {
        if (errors.size > 0) {
            throw BankException(400, errors)
        }
    }
}