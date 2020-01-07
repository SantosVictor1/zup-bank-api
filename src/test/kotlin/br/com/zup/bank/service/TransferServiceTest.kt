package br.com.zup.bank.service

import br.com.zup.bank.dto.request.TransferRequestDTO
import br.com.zup.bank.dto.response.success.NewTransferResponseDTO
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
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.test.context.junit4.SpringRunner
import java.util.*

/**
 * Created by Victor Santos on 30/12/2019
 */
@RunWith(SpringRunner::class)
class TransferServiceTest {
    private val transferService: TransferServiceImpl = TransferServiceImpl(
        Mockito.mock(TransferRepository::class.java),
        Mockito.mock(AccountRepository::class.java),
        Mockito.mock(ActivityRepository::class.java)
    )
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

        Mockito.`when`(transferService.accountRepository.existsAccountByAccountNumber(transferDTO.destinyAccount!!)).thenReturn(true)
        Mockito.`when`(transferService.accountRepository.existsAccountByAccountNumber(transferDTO.originAccount!!)).thenReturn(false)

        transferService.newTransfer(transferDTO)
    }

    @Test(expected = ResourceNotFoundException::class)
    fun destinyAccountNotFoundTest() {
        Mockito.`when`(transferService.accountRepository.existsAccountByAccountNumber(transferDTO.destinyAccount!!)).thenReturn(false)

        transferDTO.originAccount = "7894561231"
        transferService.newTransfer(transferDTO)
    }

    @Test(expected = ResourceNotFoundException::class)
    fun getAccountWithError() {
        transferDTO.originAccount = "7894561231"

        Mockito.`when`(transferService.accountRepository.existsAccountByAccountNumber(transferDTO.destinyAccount!!)).thenReturn(true)
        Mockito.`when`(transferService.accountRepository.existsAccountByAccountNumber(transferDTO.originAccount!!)).thenReturn(true)
        Mockito.`when`(transferService.accountRepository.findByAccountNumberOrUserCpf(transferDTO.destinyAccount!!)).thenReturn(Optional.empty())

        transferService.newTransfer(transferDTO)
    }

    @Test(expected = BankException::class)
    fun insufficientFundsTest() {
        transferDTO.originAccount = "7894561231"
        val account = Account(1, 0.0, 1000.0, "1234568977", true, null, null, null)
        val account2 = Account(2, 100.0, 1000.0, "7894561231", true, null, null, null)

        Mockito.`when`(transferService.accountRepository.existsAccountByAccountNumber(transferDTO.destinyAccount!!)).thenReturn(true)
        Mockito.`when`(transferService.accountRepository.existsAccountByAccountNumber(transferDTO.originAccount!!)).thenReturn(true)
        Mockito.`when`(transferService.accountRepository.findByAccountNumberOrUserCpf(transferDTO.destinyAccount!!)).thenReturn(Optional.of(account))
        Mockito.`when`(transferService.accountRepository.findByAccountNumberOrUserCpf(transferDTO.originAccount!!)).thenReturn(Optional.of(account2))

        transferService.newTransfer(transferDTO)
    }

    @Test(expected = BankException::class)
    fun differentCpfTest() {
        transferDTO.originAccount = "7894561231"
        val user = User(1, "Fulano", "50359879064", "fulano@gmail.com", true)
        val destinyAccount = Account(1, 0.0, 1000.0, "1234568977", true, user, null, null)
        val originAccount = Account(2, 200.0, 1000.0, "7894561231", true, null, null, null)

        Mockito.`when`(transferService.accountRepository.existsAccountByAccountNumber(transferDTO.destinyAccount!!)).thenReturn(true)
        Mockito.`when`(transferService.accountRepository.existsAccountByAccountNumber(transferDTO.originAccount!!)).thenReturn(true)
        Mockito.`when`(transferService.accountRepository.findByAccountNumberOrUserCpf(transferDTO.destinyAccount!!)).thenReturn(Optional.of(destinyAccount))
        Mockito.`when`(transferService.accountRepository.findByAccountNumberOrUserCpf(transferDTO.originAccount!!)).thenReturn(Optional.of(originAccount))

        transferService.newTransfer(transferDTO)
    }

    @Test
    fun successfullyTransferTest() {
        transferDTO.originAccount = "7894561231"
        val user = User(1, "Fulano", "50359879063", "fulano@gmail.com", true)
        val destinyAccount = Account(1, 0.0, 1000.0, "1234568977", true, user, null, null)
        val originAccount = Account(2, 200.0, 1000.0, "7894561231", true, user, null, null)
        val originActivity = Activity(null, transferDTO.date, -transferDTO.transferValue!!, Operation.TRANSFER, originAccount, user)
        val destinyActivity = Activity(null, transferDTO.date, transferDTO.transferValue, Operation.TRANSFER, destinyAccount, user)
        val newTransferResponseDTO = NewTransferResponseDTO(Date(), "Transferência no valor de 150.0 reais para a conta 1234567891 realizada com sucesso")

        Mockito.`when`(transferService.accountRepository.existsAccountByAccountNumber(transferDTO.destinyAccount!!)).thenReturn(true)
        Mockito.`when`(transferService.accountRepository.existsAccountByAccountNumber(transferDTO.originAccount!!)).thenReturn(true)
        Mockito.`when`(transferService.accountRepository.findByAccountNumberOrUserCpf(transferDTO.destinyAccount!!)).thenReturn(Optional.of(destinyAccount))
        Mockito.`when`(transferService.accountRepository.findByAccountNumberOrUserCpf(transferDTO.originAccount!!)).thenReturn(Optional.of(originAccount))
        Mockito.`when`(transferService.activityRepository.saveAll(mutableListOf(originActivity, destinyActivity))).thenReturn(mutableListOf(originActivity, destinyActivity))
        Mockito.`when`(transferService.accountRepository.saveAll(mutableListOf(originAccount, destinyAccount))).thenReturn(mutableListOf(originAccount, destinyAccount))

        val transferResponseDTO = transferService.newTransfer(transferDTO)

        Assert.assertEquals(transferResponseDTO.message, newTransferResponseDTO.message)

        Mockito.verify(transferService.accountRepository, Mockito.times(1)).existsAccountByAccountNumber(transferDTO.destinyAccount!!)
        Mockito.verify(transferService.accountRepository, Mockito.times(1)).existsAccountByAccountNumber(transferDTO.originAccount!!)
        Mockito.verify(transferService.accountRepository, Mockito.times(1)).findByAccountNumberOrUserCpf(transferDTO.destinyAccount!!)
        Mockito.verify(transferService.accountRepository, Mockito.times(1)).findByAccountNumberOrUserCpf(transferDTO.originAccount!!)
        Mockito.verify(transferService.accountRepository, Mockito.times(1)).saveAll(mutableListOf(originAccount, destinyAccount))
        Mockito.verify(transferService.activityRepository, Mockito.times(1)).saveAll(mutableListOf(originActivity, destinyActivity))
    }
}