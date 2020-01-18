package br.com.zup.bank.unitary.service

import br.com.zup.bank.common.Message
import br.com.zup.bank.dto.request.TransferRequestDTO
import br.com.zup.bank.dto.response.success.NewTransferResponseDTO
import br.com.zup.bank.enums.Operation
import br.com.zup.bank.enums.Status
import br.com.zup.bank.exception.DuplicatedResourceBankException
import br.com.zup.bank.exception.InvalidResourceBankException
import br.com.zup.bank.exception.ResourceNotFoundBankException
import br.com.zup.bank.model.Account
import br.com.zup.bank.model.Activity
import br.com.zup.bank.model.Transfer
import br.com.zup.bank.model.User
import br.com.zup.bank.repository.AccountRepository
import br.com.zup.bank.repository.ActivityRepository
import br.com.zup.bank.repository.TransferRepository
import br.com.zup.bank.service.impl.TransferServiceImpl
import com.nhaarman.mockito_kotlin.argumentCaptor
import org.apache.commons.io.IOUtils
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.nio.charset.Charset
import java.util.*

/**
 * Created by Victor Santos on 30/12/2019
 */
class TransferServiceTest {
    private val transferRepository = Mockito.mock(TransferRepository::class.java)
    private val accountRepository = Mockito.mock(AccountRepository::class.java)
    private val activityRepository = Mockito.mock(ActivityRepository::class.java)
    private val messageResource = Mockito.mock(Message::class.java)
    private val transferService: TransferServiceImpl = TransferServiceImpl(
        transferRepository,
        accountRepository,
        activityRepository,
        messageResource
    )
    private lateinit var user: User
    private lateinit var transfer: Transfer
    private lateinit var destinyAccount: Account
    private lateinit var originAccount: Account
    private lateinit var transferRequestDTO: TransferRequestDTO
    private lateinit var jsonTransferRequestDTO: String
    private lateinit var originActivity: Activity
    private lateinit var destinyActivity: Activity

    @Before
    fun setMocks() {
        user = User(1, "Victor", "50359879063", "victor@gmail.com", true)
        destinyAccount = Account(1, 0.0, 1000.0, "1234568977", true, user)
        originAccount = Account(2, 200.0, 1000.0, "7894561231", true, user)
        transferRequestDTO = TransferRequestDTO(
            originAccount.accountNumber,
            destinyAccount.accountNumber,
            user.cpf,
            100.0,
            null
        )
        transfer = Transfer(
            1,
            originAccount,
            destinyAccount,
            transferRequestDTO.transferValue,
            transferStatus = Status.IN_PROCESS,
            errorCode = "transfer.completed"
        )
        originActivity = Activity(
            null,
            value = transferRequestDTO.transferValue,
            operation = Operation.NEGATIVE_TRANSFER,
            account = originAccount
        )
        destinyActivity = Activity(
            null,
            value = transferRequestDTO.transferValue,
            operation = Operation.POSITIVE_TRANSFER,
            account = destinyAccount
        )
        jsonTransferRequestDTO = IOUtils.toString(
            javaClass.classLoader.getResourceAsStream("payload/TransferRequestDTO.json"),
            Charset.forName("UTF-8")
        )
    }

    @Test(expected = InvalidResourceBankException::class)
    fun insufficientFundsTest() {
        originAccount.balance = 0.0

        transferService.doTransfer(originAccount, destinyAccount, transferRequestDTO)
    }

    @Test(expected = InvalidResourceBankException::class)
    fun differentCpfTest() {
        transferRequestDTO.recipientsCpf = "45698712354"

        transferService.doTransfer(originAccount, destinyAccount, transferRequestDTO)
    }

    @Test
    fun doTransferWithSuccessTest() {
        Mockito.`when`(activityRepository.saveAll(mutableListOf(originActivity, destinyActivity))).thenReturn(mutableListOf(originActivity, destinyActivity))
        Mockito.`when`(accountRepository.saveAll(mutableListOf(originAccount, destinyAccount))).thenReturn(mutableListOf(originAccount, destinyAccount))

        transferService.doTransfer(originAccount, destinyAccount, transferRequestDTO)

        val activityCaptor = argumentCaptor<MutableList<Activity>>()

        Mockito.verify(accountRepository, Mockito.times(1)).saveAll(mutableListOf(originAccount, destinyAccount))
        Mockito.verify(activityRepository, Mockito.times(1)).saveAll(activityCaptor.capture())

        Assert.assertThat(activityCaptor.firstValue.size, CoreMatchers.`is`(2))
        isSame(activityCaptor.firstValue[0], originActivity)
        isSame(activityCaptor.firstValue[1], destinyActivity)
    }

    @Test(expected = DuplicatedResourceBankException::class)
    fun duplicatedAccountsThrowAnException() {
        transferRequestDTO.originAccount = transferRequestDTO.destinyAccount

        transferService.newTransfer(transferRequestDTO, transfer)
    }

    @Test(expected = InvalidResourceBankException::class)
    fun invalidTransferValue() {
        transferRequestDTO.transferValue = 0.0

        transferService.newTransfer(transferRequestDTO, transfer)
    }

    @Test(expected = ResourceNotFoundBankException::class)
    fun originAccountNotFoundTest() {

        Mockito.`when`(accountRepository.findByAccountNumberAndIsActiveTrue(transferRequestDTO.destinyAccount)).thenReturn(destinyAccount)
        Mockito.`when`(accountRepository.findByAccountNumberAndIsActiveTrue(transferRequestDTO.originAccount)).thenReturn(null)

        transferService.newTransfer(transferRequestDTO, transfer)
    }

    @Test(expected = ResourceNotFoundBankException::class)
    fun destinyAccountNotFoundTest() {
        Mockito.`when`(accountRepository.findByAccountNumberAndIsActiveTrue(transferRequestDTO.destinyAccount)).thenReturn(null)
        Mockito.`when`(accountRepository.findByAccountNumberAndIsActiveTrue(transferRequestDTO.originAccount)).thenReturn(originAccount)

        transferService.newTransfer(transferRequestDTO, transfer)
    }

    @Test
    fun newTransferWithSuccess() {
        Mockito.`when`(activityRepository.saveAll(mutableListOf(originActivity, destinyActivity))).thenReturn(mutableListOf(originActivity, destinyActivity))
        Mockito.`when`(accountRepository.saveAll(mutableListOf(originAccount, destinyAccount))).thenReturn(mutableListOf(originAccount, destinyAccount))
        Mockito.`when`(accountRepository.findByAccountNumberAndIsActiveTrue(transferRequestDTO.originAccount)).thenReturn(originAccount)
        Mockito.`when`(accountRepository.findByAccountNumberAndIsActiveTrue(transferRequestDTO.destinyAccount)).thenReturn(destinyAccount)
        Mockito.`when`(transferRepository.save(transfer)).thenReturn(transfer)

        transferService.newTransfer(transferRequestDTO, transfer)

        Mockito.verify(transferRepository, Mockito.times(1)).save(transfer)
    }

    @Test
    fun saveTransferWithSuccess() {
        Mockito.`when`(transferRepository.save(transfer)).thenReturn(transfer)

        transferService.saveTransfer(transfer)
    }

    @Test
    @Throws(DuplicatedResourceBankException::class)
    fun throwAnExceptionWhenSomeFieldIsInvalid() {
        Mockito.`when`(transferRepository.findById(1)).thenReturn(Optional.of(transfer))
        Mockito.`when`(transferRepository.save(transfer)).thenReturn(transfer)

        transferRequestDTO.originAccount = transferRequestDTO.destinyAccount

        transferService.listen(jsonTransferRequestDTO)
    }

    @Test
    fun successfullyTransferTest() {
        Mockito.`when`(transferRepository.findById(1)).thenReturn(Optional.of(transfer))
        Mockito.`when`(accountRepository.findByAccountNumberAndIsActiveTrue(transferRequestDTO.destinyAccount)).thenReturn(destinyAccount)
        Mockito.`when`(accountRepository.findByAccountNumberAndIsActiveTrue(transferRequestDTO.originAccount)).thenReturn(originAccount)
        Mockito.`when`(activityRepository.saveAll(mutableListOf(originActivity, destinyActivity))).thenReturn(mutableListOf(originActivity, destinyActivity))
        Mockito.`when`(accountRepository.saveAll(mutableListOf(originAccount, destinyAccount))).thenReturn(mutableListOf(originAccount, destinyAccount))
        Mockito.`when`(transferRepository.save(transfer)).thenReturn(transfer)

        transferService.listen(jsonTransferRequestDTO)

        Mockito.verify(transferRepository, Mockito.times(1)).findById(1)

    }

    @Test(expected = ResourceNotFoundBankException::class)
    fun throwAnExceptionWhenNotFindTransfer() {
        Mockito.`when`(transferRepository.findById(1)).thenReturn(Optional.empty())

        transferService.getTransferStatus(1)
    }

    @Test
    fun getTransferWithSuccess() {
        Mockito.`when`(transferRepository.findById(1)).thenReturn(Optional.of(transfer))
        Mockito.`when`(messageResource.getMessage("transfer.completed")).thenReturn("Transferência realizada com sucesso")

        val response = transferService.getTransferStatus(1)

        Assert.assertEquals(response.transferId, 1)
        Assert.assertEquals(response.transferStatus, Status.IN_PROCESS)

        Mockito.verify(transferRepository, Mockito.times(1)).findById(1)
    }


    private fun isSame(argument: Activity?, activity: Activity): Boolean {
        return if(argument == null) {
            false
        } else {
            Assert.assertEquals(activity, argument.copy(activityDate = activity.activityDate))
            true
        }
    }
}