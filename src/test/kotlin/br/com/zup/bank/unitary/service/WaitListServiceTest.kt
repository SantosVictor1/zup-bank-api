package br.com.zup.bank.unitary.service

import br.com.zup.bank.common.BankErrorCode
import br.com.zup.bank.common.Message
import br.com.zup.bank.dto.response.success.UserResponseDTO
import br.com.zup.bank.enums.Status
import br.com.zup.bank.exception.MultipleRegisterBankException
import br.com.zup.bank.model.WaitList
import br.com.zup.bank.repository.WaitListRepository
import br.com.zup.bank.service.IUserService
import br.com.zup.bank.service.impl.WaitListServiceImpl
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito

class WaitListServiceTest {
    private val messageSource = Mockito.mock(Message::class.java)
    private val waitListRepository = Mockito.mock(WaitListRepository::class.java)
    private val userService = Mockito.mock(IUserService::class.java)
    private val waitListService = WaitListServiceImpl(waitListRepository, userService, messageSource)

    @Test
    fun testWaitListNotNull() {
        val cpf = "02160795607"
        val waitList = WaitList(1, cpf, Status.FAILED, BankErrorCode.BANK070.code)

        Mockito.`when`(waitListRepository.findByCpf(cpf)).thenReturn(waitList)
        Mockito.`when`(waitListRepository.save(waitList)).thenReturn(waitList.copy(status = Status.IN_PROCESS, message = ""))

        val response = waitListService.saveOnWait(cpf)

        Assert.assertThat(response.cpf, CoreMatchers.`is`(cpf))
        Assert.assertThat(response.message, CoreMatchers.`is`(""))
        Assert.assertThat(response.status, CoreMatchers.`is`(Status.IN_PROCESS))

        Mockito.verify(waitListRepository, Mockito.times(1)).save(waitList)
    }

    @Test
    fun testWaitListNull() {
        val cpf = "02160795607"
        val waitList = WaitList.toEntity(cpf)

        Mockito.`when`(waitListRepository.findByCpf(cpf)).thenReturn(null)
        Mockito.`when`(waitListRepository.save(waitList)).thenReturn(waitList.copy(
            id = 1,
            status = Status.IN_PROCESS,
            message = "")
        )

        val response = waitListService.saveOnWait(cpf)

        Assert.assertThat(response.cpf, CoreMatchers.`is`(cpf))
        Assert.assertThat(response.message, CoreMatchers.`is`(""))
        Assert.assertThat(response.status, CoreMatchers.`is`(Status.IN_PROCESS))

        Mockito.verify(waitListRepository, Mockito.times(1)).save(waitList)
    }

    @Test
    fun testDisapproveUser() {
        val cpf = "02160795607"
        val waitList = WaitList(1, cpf, Status.IN_PROCESS, "")
        val waitListReproved = waitList.copy(status = Status.FAILED, message = BankErrorCode.BANK070.code)

        Mockito.`when`(waitListRepository.findByCpf(cpf)).thenReturn(waitList)
        Mockito.`when`(waitListRepository.save(waitList)).thenReturn(waitListReproved)

        waitListService.disapproveRegister(cpf)

        Mockito.verify(waitListRepository, Mockito.times(1)).save(waitList)
    }

    @Test
    fun testRemoveFromList() {
        val cpf = "02160795607"

        Mockito.doNothing().`when`(waitListRepository).deleteByCpf(cpf)

        waitListService.removeFromList(cpf)

        Mockito.verify(waitListRepository, Mockito.times(1)).deleteByCpf(cpf)
    }

    @Test
    fun testWaitListStatusNull() {
        val cpf = "02160795607"
        val userResponseDTO = UserResponseDTO(1, "Victor Santos", cpf, "victor@gmail.com", true)

        Mockito.`when`(waitListRepository.findByCpf(cpf)).thenReturn(null)
        Mockito.`when`(messageSource.getMessage(BankErrorCode.BANK072.code)).thenReturn("Cadastro aprovado")
        Mockito.`when`(userService.getByCpf(cpf, true)).thenReturn(userResponseDTO)

        val response = waitListService.getStatus(cpf)

        Assert.assertThat(response.cpf, CoreMatchers.`is`(cpf))
        Assert.assertThat(response.message, CoreMatchers.`is`("Cadastro aprovado"))
        Assert.assertThat(response.status, CoreMatchers.`is`(Status.COMPLETED))
    }

    @Test
    fun testWaitListStatusNotNull() {
        val cpf = "02160795607"
        val waitList = WaitList(1, cpf, Status.FAILED, BankErrorCode.BANK070.code)

        Mockito.`when`(waitListRepository.findByCpf(cpf)).thenReturn(waitList)
        Mockito.`when`(messageSource.getMessage(waitList.message)).thenReturn("Cadastro reprovado")

        val response = waitListService.getStatus(cpf)

        Assert.assertThat(response.cpf, CoreMatchers.`is`(cpf))
        Assert.assertThat(response.message, CoreMatchers.`is`("Cadastro reprovado"))
        Assert.assertThat(response.status, CoreMatchers.`is`(Status.FAILED))
    }

    @Test
    fun testStatusInProcess() {
        val cpf = "02160795607"
        val waitList = WaitList(1, cpf, Status.IN_PROCESS, "")

        Mockito.`when`(waitListRepository.findByCpf(cpf)).thenReturn(waitList)

        val response = assertThrows<MultipleRegisterBankException> { waitListService.validateRequest(cpf) }

        Assert.assertThat(response.errorCode, CoreMatchers.`is`(BankErrorCode.BANK071.code))
        Assert.assertThat(response.objectName, CoreMatchers.`is`("WaitList"))
        Assert.assertThat(response.field, CoreMatchers.`is`(""))
    }
}