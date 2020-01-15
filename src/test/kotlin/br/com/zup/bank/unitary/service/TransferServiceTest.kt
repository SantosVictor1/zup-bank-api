package br.com.zup.bank.unitary.service

import br.com.zup.bank.dto.request.TransferRequestDTO
import br.com.zup.bank.dto.response.success.NewTransferResponseDTO
import br.com.zup.bank.enums.Operation
import br.com.zup.bank.exception.DuplicatedResourceBankException
import br.com.zup.bank.exception.InvalidResourceBankException
import br.com.zup.bank.exception.ResourceNotFoundBankException
import br.com.zup.bank.model.Account
import br.com.zup.bank.model.Activity
import br.com.zup.bank.model.User
import br.com.zup.bank.repository.AccountRepository
import br.com.zup.bank.repository.ActivityRepository
import br.com.zup.bank.repository.TransferRepository
import br.com.zup.bank.service.impl.TransferServiceImpl
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.util.*

/**
 * Created by Victor Santos on 30/12/2019
 */
class TransferServiceTest {
    private val transferService: TransferServiceImpl = TransferServiceImpl(
        Mockito.mock(TransferRepository::class.java),
        Mockito.mock(AccountRepository::class.java),
        Mockito.mock(ActivityRepository::class.java)
    )
    private lateinit var transferDTO: TransferRequestDTO

    @Before
    fun setMocks() {
        transferDTO = TransferRequestDTO("7894561231", "1234567891", "50359879063", 150.0, transferId = null)
    }

    @Test(expected = DuplicatedResourceBankException::class)
    fun equalsAccountsTest() {
        transferDTO.originAccount = transferDTO.destinyAccount

        transferService.newTransfer(transferDTO)
    }

    @Test(expected = InvalidResourceBankException::class)
    fun invalidValueTest() {
        transferDTO.originAccount = "7894561231"
        transferDTO.transferValue = 0.0

        transferService.newTransfer(transferDTO)
    }

    @Test(expected = ResourceNotFoundBankException::class)
    fun originAccountNotFoundTest() {
        transferDTO.originAccount = "7894561231"

        Mockito.`when`(transferService.accountRepository.existsAccountByAccountNumber(transferDTO.destinyAccount)).thenReturn(true)
        Mockito.`when`(transferService.accountRepository.existsAccountByAccountNumber(transferDTO.originAccount)).thenReturn(false)

        transferService.newTransfer(transferDTO)
    }

    @Test(expected = ResourceNotFoundBankException::class)
    fun destinyAccountNotFoundTest() {
        Mockito.`when`(transferService.accountRepository.existsAccountByAccountNumber(transferDTO.destinyAccount)).thenReturn(false)

        transferDTO.originAccount = "7894561231"
        transferService.newTransfer(transferDTO)
    }

    @Test(expected = ResourceNotFoundBankException::class)
    fun getAccountWithError() {
        transferDTO.originAccount = "7894561231"

        Mockito.`when`(transferService.accountRepository.existsAccountByAccountNumber(transferDTO.destinyAccount)).thenReturn(true)
        Mockito.`when`(transferService.accountRepository.existsAccountByAccountNumber(transferDTO.originAccount)).thenReturn(true)
        Mockito.`when`(transferService.accountRepository.findByAccountNumberOrUserCpf("", transferDTO.destinyAccount)).thenReturn(null)

        transferService.newTransfer(transferDTO)
    }

    @Test(expected = InvalidResourceBankException::class)
    fun insufficientFundsTest() {
        transferDTO.originAccount = "7894561231"
        val destinyAccount = Account(1, 0.0, 1000.0, "1234568977", true, null)
        val originAccount = Account(2, 100.0, 1000.0, "7894561231", true, null)

        Mockito.`when`(transferService.accountRepository.existsAccountByAccountNumber(transferDTO.destinyAccount)).thenReturn(true)
        Mockito.`when`(transferService.accountRepository.existsAccountByAccountNumber(transferDTO.originAccount)).thenReturn(true)
        Mockito.`when`(transferService.accountRepository.findByAccountNumberAndIsActiveTrue(transferDTO.destinyAccount)).thenReturn(destinyAccount)
        Mockito.`when`(transferService.accountRepository.findByAccountNumberAndIsActiveTrue(transferDTO.originAccount)).thenReturn(originAccount)

        transferService.newTransfer(transferDTO)
    }

    @Test(expected = InvalidResourceBankException::class)
    fun differentCpfTest() {
        transferDTO.originAccount = "7894561231"
        val user = User(1, "Fulano", "50359879064", "fulano@gmail.com", true)
        val destinyAccount = Account(1, 0.0, 1000.0, "1234568977", true, user)
        val originAccount = Account(2, 200.0, 1000.0, "7894561231", true, null)

        Mockito.`when`(transferService.accountRepository.existsAccountByAccountNumber(transferDTO.destinyAccount)).thenReturn(true)
        Mockito.`when`(transferService.accountRepository.existsAccountByAccountNumber(transferDTO.originAccount)).thenReturn(true)
        Mockito.`when`(transferService.accountRepository.findByAccountNumberAndIsActiveTrue(transferDTO.destinyAccount)).thenReturn(destinyAccount)
        Mockito.`when`(transferService.accountRepository.findByAccountNumberAndIsActiveTrue(transferDTO.originAccount)).thenReturn(originAccount)

        transferService.newTransfer(transferDTO)
    }

    @Test
    fun successfullyTransferTest() {
        transferDTO.originAccount = "7894561231"
        val user = User(1, "Fulano", "50359879063", "fulano@gmail.com", true)
        val destinyAccount = Account(1, 0.0, 1000.0, "1234568977", true, user)
        val originAccount = Account(2, 200.0, 1000.0, "7894561231", true, user)
        val originActivity = Activity(null, transferDTO.date, transferDTO.transferValue, Operation.TRANSFER, originAccount)
        val destinyActivity = Activity(null, transferDTO.date, transferDTO.transferValue, Operation.TRANSFER, destinyAccount)
        val newTransferResponseDTO = NewTransferResponseDTO(Date(), transferDTO.transferValue, destinyAccount.accountNumber)

        Mockito.`when`(transferService.accountRepository.existsAccountByAccountNumber(transferDTO.destinyAccount)).thenReturn(true)
        Mockito.`when`(transferService.accountRepository.existsAccountByAccountNumber(transferDTO.originAccount)).thenReturn(true)
        Mockito.`when`(transferService.accountRepository.findByAccountNumberAndIsActiveTrue(transferDTO.destinyAccount)).thenReturn(destinyAccount)
        Mockito.`when`(transferService.accountRepository.findByAccountNumberAndIsActiveTrue(transferDTO.originAccount)).thenReturn(originAccount)
        Mockito.`when`(transferService.activityRepository.saveAll(mutableListOf(originActivity, destinyActivity))).thenReturn(mutableListOf(originActivity, destinyActivity))
        Mockito.`when`(transferService.accountRepository.saveAll(mutableListOf(originAccount, destinyAccount))).thenReturn(mutableListOf(originAccount, destinyAccount))

        val transferResponseDTO = transferService.newTransfer(transferDTO)

//        Assert.assertThat(transferResponseDTO.transferValue, CoreMatchers.`is`(newTransferResponseDTO.transferValue))

        Mockito.verify(transferService.accountRepository, Mockito.times(1)).existsAccountByAccountNumber(transferDTO.destinyAccount)
        Mockito.verify(transferService.accountRepository, Mockito.times(1)).existsAccountByAccountNumber(transferDTO.originAccount)
        Mockito.verify(transferService.accountRepository, Mockito.times(1)).findByAccountNumberAndIsActiveTrue(transferDTO.destinyAccount)
        Mockito.verify(transferService.accountRepository, Mockito.times(1)).findByAccountNumberAndIsActiveTrue(transferDTO.originAccount)
        Mockito.verify(transferService.accountRepository, Mockito.times(1)).saveAll(mutableListOf(originAccount, destinyAccount))
        Mockito.verify(transferService.activityRepository, Mockito.times(1)).saveAll(mutableListOf(originActivity, destinyActivity))
    }
}