package br.com.zup.bank.service.impl

import br.com.zup.bank.dto.request.ActivityRequestDTO
import br.com.zup.bank.dto.response.success.ActivityResponseDTO
import br.com.zup.bank.dto.response.success.ExtractResponseDTO
import br.com.zup.bank.enums.Operation
import br.com.zup.bank.exception.BankException
import br.com.zup.bank.model.Account
import br.com.zup.bank.model.Activity
import br.com.zup.bank.model.User
import br.com.zup.bank.repository.AccountRepository
import br.com.zup.bank.repository.ActivityRepository
import br.com.zup.bank.repository.UserRepository
import br.com.zup.bank.service.IActivityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * Created by Victor Santos on 27/12/2019
 */
@Service
@Transactional
class ActivityServiceImpl : IActivityService {
    @Autowired
    private lateinit var activityRepository: ActivityRepository
    @Autowired
    private lateinit var accountRepository: AccountRepository
    @Autowired
    private lateinit var userRepository: UserRepository
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

    override fun extract(accNumber: String): MutableList<ExtractResponseDTO> {
        getAccount(accNumber)

        var extractResponseDTO = mutableListOf<ExtractResponseDTO>()
        val extracts = activityRepository.findAllByAccountAccountNumberOrderByActivityDateDesc(accNumber)

        extracts.forEach {
            extractResponseDTO.add(getExtractResponseDTO(it))
        }

        return extractResponseDTO
    }

    private fun deposit(activityDTO: ActivityRequestDTO): ActivityResponseDTO {
        acc.balance = activityDTO.value!! + acc.balance!!

        accountRepository.save(acc)
        val activity = activityRepository.save(getActivity(user, acc, activityDTO))

        return getActivityResponseDTO(acc, activity)
    }

    private fun withdraw(activityDTO: ActivityRequestDTO): ActivityResponseDTO {
        acc.balance =  acc.balance!! - activityDTO.value!!
        validateWithdraw(acc.balance!!)

        accountRepository.save(acc)
        val activity = activityRepository.save(getActivity(user, acc, activityDTO))

        return getActivityResponseDTO(acc, activity)
    }

    private fun validateWithdraw(balance: Double) {
        if (balance < 0.0) {
            throw BankException(400, "Saldo insuficiente!")
        }
    }

    private fun getActivityResponseDTO(acc: Account, activity: Activity): ActivityResponseDTO {
        return ActivityResponseDTO(acc.balance, acc.accountNumber, activity.activityDate, activity.operation.toString())
    }

    private fun getExtractResponseDTO(activity: Activity): ExtractResponseDTO {
        return ExtractResponseDTO(activity.activityDate, activity.value, activity.account?.accountNumber, activity.operation)
    }

    private fun getActivity(user: User, acc: Account, activityDTO: ActivityRequestDTO): Activity {
        return Activity(null, Date(), activityDTO.value, activityDTO.operation!!, acc, user)
    }

    private fun getUser(cpf: String): User {
        val user = userRepository.findByCpf(cpf)

        if (!user.isPresent) {
            throw BankException(404, "Usuário não encontrado")
        }

        return user.get()
    }

    private fun getAccount(accNumber: String): Account {
        val acc = accountRepository.findByAccountNumber(accNumber)

        if (!acc.isPresent) {
            throw BankException(404, "Conta não encontrada")
        }

        return acc.get()
    }
}