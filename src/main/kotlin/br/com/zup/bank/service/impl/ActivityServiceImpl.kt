package br.com.zup.bank.service.impl

import br.com.zup.bank.dto.request.ActivityRequestDTO
import br.com.zup.bank.dto.response.success.ActivityResponseDTO
import br.com.zup.bank.exception.BankException
import br.com.zup.bank.model.Account
import br.com.zup.bank.model.Activity
import br.com.zup.bank.model.User
import br.com.zup.bank.repository.*
import br.com.zup.bank.service.IActivityService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * Created by Victor Santos on 27/12/2019
 */
@Service
class ActivityServiceImpl : IActivityService {
    @Autowired
    private lateinit var activityRepository: ActivityRepository
    @Autowired
    private lateinit var accountRepository: AccountRepository
    @Autowired
    private lateinit var userRepository: UserRepository

    @Transactional(rollbackFor = [BankException::class])
    override fun doDeposit(activityDTO: ActivityRequestDTO): ActivityResponseDTO {
        val user = getUser(activityDTO.cpf!!)
        var acc = getAccount(activityDTO.accNumber!!)
        acc.balance = activityDTO.value!! + acc.balance!!

        accountRepository.save(acc)
        val activity = activityRepository.save(getActivity(user, acc, activityDTO))

        return getActivityResponseDTO(acc, activity)
    }

    private fun getActivityResponseDTO(acc: Account, activity: Activity): ActivityResponseDTO {
        return ActivityResponseDTO(acc.balance, acc.accountNumber, activity.activityDate, activity.operation.toString())
    }

    private fun getActivity(user: User, acc: Account, activityDTO: ActivityRequestDTO): Activity {
        val date: Date = Date()

        return Activity(null, date, activityDTO.value, activityDTO.operation!!, acc, user)
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