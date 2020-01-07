package br.com.zup.bank.service

import br.com.zup.bank.dto.request.ActivityRequestDTO
import br.com.zup.bank.dto.response.success.ActivityResponseDTO
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
import br.com.zup.bank.service.impl.ActivityServiceImpl
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

/**
 * Created by Victor Santos on 30/12/2019
 */
@RunWith(SpringRunner::class)
class ActivityServiceTest {
    private val activityService: ActivityServiceImpl = ActivityServiceImpl(
        Mockito.mock(ActivityRepository::class.java),
        Mockito.mock(AccountRepository::class.java),
        Mockito.mock(UserRepository::class.java)
    )
    private lateinit var user: User
    private lateinit var account: Account
    private lateinit var activity: Activity
    private lateinit var activityDTO: ActivityRequestDTO

    @Before
    fun setObjects() {
        activityDTO = ActivityRequestDTO()
        activityDTO.accNumber = "6085506328"
        activityDTO.cpf = "50359879063"
        activityDTO.value = 100.0

        user = User(
            id = 1,
            name = "Victor",
            cpf = "50359879063",
            email = "teste@gmail.com",
            isActive = true
        )

        account = Account(1, 1000.0, 1000.0, "7278424688", true, user)
        activity = Activity(id = 1, value = 100.0, operation = Operation.DEPOSIT, account = account, user = user)
    }

    @Test(expected = ResourceNotFoundException::class)
    fun userNotExistsTest() {
        Mockito.`when`(activityService.userRepository.findByCpf(user.cpf)).thenReturn(Optional.empty())

        activityService.operation(activityDTO)
    }

//    @Test(expected = ResourceNotFoundException::class)
//    fun accountNotExistsTest() {
//        Mockito.`when`(activityService.userRepository.findByCpf(activityDTO.cpf)).thenReturn(Optional.of(user))
//        Mockito.`when`(activityService.accountRepository.findByAccountNumberOrUserCpf(activityDTO.accNumber!!)).thenReturn(Optional.empty())
//
//        activityService.operation(activityDTO)
//    }
//
//    @Test
//    fun userExistsTest() {
//        val activityResponseDTO = ActivityResponseDTO(
//            1100.0,
//            "7278424688",
//            activity.activityDate,
//            Operation.DEPOSIT.toString()
//        )
//        Mockito.`when`(activityService.userRepository.findByCpf(user.cpf)).thenReturn(Optional.of(user))
//        Mockito.`when`(activityService.accountRepository.findByAccountNumberOrUserCpf(activityDTO.accNumber!!)).thenReturn(Optional.of(account))
//        Mockito.`when`(activityService.accountRepository.save(account)).thenReturn(account)
//        Mockito.`when`(activityService.activityRepository.save(Mockito.any(Activity::class.java))).thenReturn(activity)
//
//        activityDTO.operation = Operation.DEPOSIT
//
//        val operationResponse = activityService.operation(activityDTO)
//
//        Assert.assertEquals(operationResponse, activityResponseDTO)
//
//        Mockito.verify(activityService.userRepository, Mockito.times(1)).findByCpf(user.cpf)
//        Mockito.verify(activityService.accountRepository, Mockito.times(1)).findByAccountNumberOrUserCpf(activityDTO.accNumber!!)
//        Mockito.verify(activityService.accountRepository, Mockito.times(1)).save(account)
//        Mockito.verify(activityService.activityRepository, Mockito.times(1)).save(Mockito.any(Activity::class.java))
//    }
//
//    @Test(expected = BankException::class)
//    fun withdrawWithError() {
//        Mockito.`when`(activityService.userRepository.findByCpf(user.cpf)).thenReturn(Optional.of(user))
//        Mockito.`when`(activityService.accountRepository.findByAccountNumberOrUserCpf(activityDTO.accNumber!!)).thenReturn(Optional.of(account))
//        Mockito.`when`(activityService.accountRepository.save(account)).thenReturn(account)
//        Mockito.`when`(activityService.activityRepository.save(Mockito.any(Activity::class.java))).thenReturn(activity)
//
//        activityDTO.operation = Operation.WITHDRAW
//        activityDTO.value = 10000.0
//
//        activityService.operation(activityDTO)
//    }
//
//    @Test
//    fun withdrawWithSuccess() {
//        val activityResponseDTO = ActivityResponseDTO(
//            900.0,
//            "7278424688",
//            activity.activityDate,
//            Operation.WITHDRAW.toString()
//        )
//
//        activity.operation = Operation.WITHDRAW
//
//        Mockito.`when`(activityService.userRepository.findByCpf(user.cpf)).thenReturn(Optional.of(user))
//        Mockito.`when`(activityService.accountRepository.findByAccountNumberOrUserCpf(activityDTO.accNumber!!)).thenReturn(Optional.of(account))
//        Mockito.`when`(activityService.accountRepository.save(account)).thenReturn(account)
//        Mockito.`when`(activityService.activityRepository.save(Mockito.any(Activity::class.java))).thenReturn(activity)
//
//        activityDTO.operation = Operation.WITHDRAW
//
//        val operationResponse = activityService.operation(activityDTO)
//
//        Assert.assertEquals(operationResponse, activityResponseDTO)
//
//        Mockito.verify(activityService.userRepository, Mockito.times(1)).findByCpf(user.cpf)
//        Mockito.verify(activityService.accountRepository, Mockito.times(1)).findByAccountNumberOrUserCpf(activityDTO.accNumber!!)
//        Mockito.verify(activityService.accountRepository, Mockito.times(1)).save(account)
//        Mockito.verify(activityService.activityRepository, Mockito.times(1)).save(Mockito.any(Activity::class.java))
//    }
//
//    @Test
//    fun extractWithSuccess() {
//        val paginationDTO = PaginationResponseDTO(0, 10)
//        var pageRequest = PageRequest.of(0, 10)
//
//        Mockito.`when`(activityService.accountRepository.findByAccountNumberOrUserCpf(activityDTO.accNumber!!)).thenReturn(Optional.of(account))
//        Mockito.`when`(activityService.activityRepository.findAllByAccountAccountNumberOrderByActivityDateDesc(activityDTO.accNumber!!, pageRequest))
//            .thenReturn((Page.empty(pageRequest)))
//
//        val extracts = activityService.extract(activityDTO.accNumber!!, 0, 10)
//
//        Assert.assertEquals(extracts, ExtractResponseDTO(mutableListOf(), paginationDTO))
//
//        Mockito.verify(activityService.activityRepository, Mockito.times(1))
//            .findAllByAccountAccountNumberOrderByActivityDateDesc(activityDTO.accNumber!!, pageRequest)
//    }
}