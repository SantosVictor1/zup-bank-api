package br.com.zup.bank.unitary.service

import br.com.zup.bank.dto.request.TransferRequestDTO
import br.com.zup.bank.enums.Status
import br.com.zup.bank.exception.ResourceNotFoundBankException
import br.com.zup.bank.model.Account
import br.com.zup.bank.model.Transfer
import br.com.zup.bank.model.User
import br.com.zup.bank.repository.AccountRepository
import br.com.zup.bank.service.ITransferService
import br.com.zup.bank.service.impl.KafkaServiceImpl
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argumentCaptor
import org.apache.commons.io.IOUtils
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.springframework.kafka.core.KafkaTemplate
import java.nio.charset.Charset

class KafkaServiceTest {
    private val kafkaTemplate = Mockito.mock(KafkaTemplate::class.java) as KafkaTemplate<String, String>
    private val accountRepository = Mockito.mock(AccountRepository::class.java)
    private val transferService = Mockito.mock(ITransferService::class.java)
    private val kafkaService = KafkaServiceImpl(
        kafkaTemplate,
        transferService,
        accountRepository
    )
    private lateinit var user: User
    private lateinit var transfer: Transfer
    private lateinit var destinyAccount: Account
    private lateinit var originAccount: Account
    private lateinit var transferRequestDTO: TransferRequestDTO

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
            null,
            originAccount,
            destinyAccount,
            transferRequestDTO.transferValue,
            transferStatus = Status.IN_PROCESS
        )
    }

    @Test
    fun throwAnExceptionWhenNotFindAccount() {
        Mockito.`when`(accountRepository.findByAccountNumberAndIsActiveTrue(originAccount.accountNumber)).thenReturn(null)

        val exception = assertThrows<ResourceNotFoundBankException> { kafkaService.newTransferRequest(transferRequestDTO) }

        Assert.assertThat(exception.errorCode, CoreMatchers.`is`("account.not.found"))
        Assert.assertThat(exception.field, CoreMatchers.`is`("accountNumber"))
        Assert.assertThat(exception.objectName, CoreMatchers.`is`("Account"))

        Mockito.verify(accountRepository, Mockito.times(1)).findByAccountNumberAndIsActiveTrue(originAccount.accountNumber)
    }

    @Test
    fun sendTransferWithSuccess() {
        val newTransfer = Transfer(
            1, originAccount,
            destinyAccount,
            transferRequestDTO.transferValue,
            transferStatus = Status.IN_PROCESS
        )

        Mockito.`when`(transferService.saveTransfer(any())).thenAnswer {
            val argument = it.getArgument<Transfer>(0)
            if (isSame(argument, transfer)) {
                newTransfer
            } else {
                null
            }
        }
        Mockito.`when`(accountRepository.findByAccountNumberAndIsActiveTrue(transferRequestDTO.originAccount))
            .thenReturn(originAccount)
        Mockito.`when`(accountRepository.findByAccountNumberAndIsActiveTrue(transferRequestDTO.destinyAccount))
            .thenReturn(destinyAccount)

        val response = kafkaService.newTransferRequest(transferRequestDTO)

        Assert.assertEquals(response.id, newTransfer.id)
        Assert.assertEquals(response.status, newTransfer.transferStatus)

        val transferCaptor = argumentCaptor<Transfer>()
        val jsonTransferRequestDTO: String = IOUtils.toString(
            javaClass.classLoader.getResourceAsStream("payload/TransferRequestDTO.json"),
            Charset.forName("UTF-8")
        )

        Mockito.verify(transferService, Mockito.times(1)).saveTransfer(transferCaptor.capture())
        Mockito.verify(kafkaTemplate, Mockito.times(1)).send("bank_api", jsonTransferRequestDTO)

        isSame(transferCaptor.firstValue, transfer, true)
    }

    private fun isSame(argument: Transfer?, transfer: Transfer, withAssert: Boolean = false): Boolean {
        if (argument == null) return false
        return if (withAssert) {
            Assert.assertEquals(transfer, argument.copy(transferDate = transfer.transferDate))
            true
        } else {
            transfer == argument.copy(transferDate = transfer.transferDate)
        }
    }
}