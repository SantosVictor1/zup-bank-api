package br.com.zup.bank

import br.com.zup.bank.dto.request.TransferRequestDTO
import br.com.zup.bank.enums.Operation
import br.com.zup.bank.exception.BankException
import br.com.zup.bank.exception.ResourceNotFoundException
import br.com.zup.bank.model.Account
import br.com.zup.bank.model.Activity
import br.com.zup.bank.model.User
import br.com.zup.bank.repository.AccountRepository
import br.com.zup.bank.repository.ActivityRepository
import br.com.zup.bank.repository.TransferRepository
import br.com.zup.bank.service.impl.TransferServiceImpl
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

/**
 * Created by Victor Santos on 30/12/2019
 */
@RunWith(SpringRunner::class)
class TransferServiceTest {
    @InjectMocks
    private lateinit var transferService: TransferServiceImpl
    @Mock
    private lateinit var transferRepository: TransferRepository
    @Mock
    private lateinit var accountRepository: AccountRepository
    @Mock
    private lateinit var activityRepository: ActivityRepository
    private lateinit var transferDTO: TransferRequestDTO

    @Before
    fun setMocks() {
        transferDTO = TransferRequestDTO()
        transferDTO.destinyAccount = "1234567891"
        transferDTO.transferValue = 150.0
        transferDTO.recipientsCpf = "50359879063"
    }

    @Test(expected = BankException::class)
    fun equalsAccountsTest() {
        transferDTO.originAccount = transferDTO.destinyAccount

        transferService.newTransfer(transferDTO)
    }

    @Test(expected = BankException::class)
    fun invalidValueTest() {
        transferDTO.originAccount = "7894561231"
        transferDTO.transferValue = 0.0

        transferService.newTransfer(transferDTO)
    }

    @Test(expected = ResourceNotFoundException::class)
    fun originAccountNotFoundTest() {
        transferDTO.originAccount = "7894561231"

        Mockito.`when`(accountRepository.existsAccountByAccountNumber(transferDTO.destinyAccount!!)).thenReturn(true)
        Mockito.`when`(accountRepository.existsAccountByAccountNumber(transferDTO.originAccount!!)).thenReturn(false)

        transferService.newTransfer(transferDTO)
    }

    @Test(expected = ResourceNotFoundException::class)
    fun destinyAccountNotFoundTest() {
        Mockito.`when`(accountRepository.existsAccountByAccountNumber(transferDTO.destinyAccount!!)).thenReturn(false)

        transferDTO.originAccount = "7894561231"
        transferService.newTransfer(transferDTO)
    }

    @Test(expected = ResourceNotFoundException::class)
    fun getAccountWithError() {
        transferDTO.originAccount = "7894561231"

        Mockito.`when`(accountRepository.existsAccountByAccountNumber(transferDTO.destinyAccount!!)).thenReturn(true)
        Mockito.`when`(accountRepository.existsAccountByAccountNumber(transferDTO.originAccount!!)).thenReturn(true)
        Mockito.`when`(accountRepository.findByAccountNumber(transferDTO.destinyAccount!!)).thenReturn(Optional.empty())

        transferService.newTransfer(transferDTO)
    }

    @Test(expected = BankException::class)
    fun insufficientFundsTest() {
        transferDTO.originAccount = "7894561231"
        val account = Account(1, 0.0, 1000.0, "1234568977", true, null, null, null)
        val account2 = Account(2, 100.0, 1000.0, "7894561231", true, null, null, null)

        Mockito.`when`(accountRepository.existsAccountByAccountNumber(transferDTO.destinyAccount!!)).thenReturn(true)
        Mockito.`when`(accountRepository.existsAccountByAccountNumber(transferDTO.originAccount!!)).thenReturn(true)
        Mockito.`when`(accountRepository.findByAccountNumber(transferDTO.destinyAccount!!)).thenReturn(Optional.of(account))
        Mockito.`when`(accountRepository.findByAccountNumber(transferDTO.originAccount!!)).thenReturn(Optional.of(account2))

        transferService.newTransfer(transferDTO)
    }

    @Test(expected = BankException::class)
    fun differentCpfTest() {
        transferDTO.originAccount = "7894561231"
        val user = User(1, "Fulano", "50359879064", "fulano@gmail.com", true)
        val destinyAccount = Account(1, 0.0, 1000.0, "1234568977", true, user, null, null)
        val originAccount = Account(2, 200.0, 1000.0, "7894561231", true, null, null, null)

        Mockito.`when`(accountRepository.existsAccountByAccountNumber(transferDTO.destinyAccount!!)).thenReturn(true)
        Mockito.`when`(accountRepository.existsAccountByAccountNumber(transferDTO.originAccount!!)).thenReturn(true)
        Mockito.`when`(accountRepository.findByAccountNumber(transferDTO.destinyAccount!!)).thenReturn(Optional.of(destinyAccount))
        Mockito.`when`(accountRepository.findByAccountNumber(transferDTO.originAccount!!)).thenReturn(Optional.of(originAccount))

        transferService.newTransfer(transferDTO)
    }

    @Test
    fun successfullyTransferTest() {
        transferDTO.originAccount = "7894561231"
        val user = User(1, "Fulano", "50359879063", "fulano@gmail.com", true)
        val destinyAccount = Account(1, 0.0, 1000.0, "1234568977", true, user, null, null)
        val originAccount = Account(2, 200.0, 1000.0, "7894561231", true, null, null, null)
        val originActivity = Activity(1, Date(), transferDTO.transferValue, Operation.TRANSFER, originAccount, user)
        val destinyActivity = Activity(2, Date(), transferDTO.transferValue, Operation.TRANSFER, destinyAccount, user)

        Mockito.`when`(accountRepository.existsAccountByAccountNumber(transferDTO.destinyAccount!!)).thenReturn(true)
        Mockito.`when`(accountRepository.existsAccountByAccountNumber(transferDTO.originAccount!!)).thenReturn(true)
        Mockito.`when`(accountRepository.findByAccountNumber(transferDTO.destinyAccount!!)).thenReturn(Optional.of(destinyAccount))
        Mockito.`when`(accountRepository.findByAccountNumber(transferDTO.originAccount!!)).thenReturn(Optional.of(originAccount))
        Mockito.`when`(activityRepository.saveAll(mutableListOf(originActivity, destinyActivity))).thenReturn(mutableListOf(originActivity, destinyActivity))
        Mockito.`when`(accountRepository.saveAll(mutableListOf(originAccount, destinyAccount))).thenReturn(mutableListOf(originAccount, destinyAccount))

        transferService.newTransfer(transferDTO)

        Mockito.verify(accountRepository, Mockito.times(1)).existsAccountByAccountNumber(transferDTO.destinyAccount!!)
        Mockito.verify(accountRepository, Mockito.times(1)).existsAccountByAccountNumber(transferDTO.originAccount!!)
        Mockito.verify(accountRepository, Mockito.times(1)).findByAccountNumber(transferDTO.destinyAccount!!)
        Mockito.verify(accountRepository, Mockito.times(1)).findByAccountNumber(transferDTO.originAccount!!)
        Mockito.verify(accountRepository, Mockito.times(1)).saveAll(mutableListOf(originAccount, destinyAccount))
//        Mockito.verify(activityRepository, Mockito.times(1)).saveAll(mutableListOf(originActivity, destinyActivity))

    }
}