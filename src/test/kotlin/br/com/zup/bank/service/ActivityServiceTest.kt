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
import org.mockito.Mock
import org.mockito.Mockito
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

/**
 * Created by Victor Santos on 30/12/2019
 */
class ActivityServiceTest {
    private val activityService: ActivityServiceImpl = ActivityServiceImpl(
        Mockito.mock(ActivityRepository::class.java)
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
        activityDTO.operation = Operation.DEPOSIT

        user = User(
            id = 1,
            name = "Victor",
            cpf = "50359879063",
            email = "teste@gmail.com",
            isActive = true
        )

        account = Account(1, 1000.0, 1000.0, "7278424688", true, user)
        activity = Activity(id = 1, value = 100.0, operation = Operation.DEPOSIT, account = account)
    }

    @Test
    fun extractWithSuccess() {
        val paginationDTO = PaginationResponseDTO(0, 10)
        var pageRequest = PageRequest.of(0, 10)

        Mockito.`when`(activityService.activityRepository.
            findAllByAccountAccountNumberOrderByActivityDateDesc(account.accountNumber, pageRequest)
        ).thenReturn((Page.empty(pageRequest)))

        val extracts = activityService.extract(account.accountNumber, pageRequest)

        Assert.assertEquals(extracts, ExtractResponseDTO(mutableListOf(), paginationDTO))

        Mockito.verify(activityService.activityRepository, Mockito.times(1))
            .findAllByAccountAccountNumberOrderByActivityDateDesc(account.accountNumber, pageRequest)
    }

    @Test
    fun createActivityWithSuccess() {
        Mockito.`when`(activityService.activityRepository.save(Mockito.any(Activity::class.java))).thenReturn(activity)

        val activityResponse = activityService.createActivity(account, activityDTO)

        Assert.assertEquals(activityResponse.accNumber, account.accountNumber)
        Assert.assertEquals(activityResponse.operation, activity.operation.toString())

        Mockito.verify(activityService.activityRepository, Mockito.times(1)).save(Mockito.any(Activity::class.java))
    }
}