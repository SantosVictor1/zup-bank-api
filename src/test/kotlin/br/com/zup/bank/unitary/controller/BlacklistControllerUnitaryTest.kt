package br.com.zup.bank.unitary.controller

import br.com.zup.bank.controller.BlacklistController
import br.com.zup.bank.dto.request.BlacklistRequestDTO
import br.com.zup.bank.dto.response.success.BlacklistResponseDTO
import br.com.zup.bank.exception.DuplicatedResourceBankException
import br.com.zup.bank.exception.ResourceNotFoundBankException
import br.com.zup.bank.service.IBlacklistService
import org.junit.Test
import org.mockito.Mockito

/**
 * @author Victor Santos
 * Created on 06/02/2020
 */
class BlacklistControllerUnitaryTest {
    private val blacklistService = Mockito.mock(IBlacklistService::class.java)
    private val blacklistController = BlacklistController(blacklistService)

    @Test(expected = DuplicatedResourceBankException::class)
    fun throwAnExceptionWhenDuplicatedCpf() {
        val blacklistRequestDTO = BlacklistRequestDTO("02160795607")

        Mockito.`when`(blacklistService.saveNewCpf(blacklistRequestDTO)).thenThrow(DuplicatedResourceBankException::class.java)

        blacklistController.saveNewCpf(blacklistRequestDTO)
    }

    @Test
    fun saveNewCpf() {
        val blacklistRequestDTO = BlacklistRequestDTO("02160795607")
        val blacklistResponseDTO = BlacklistResponseDTO(1, "02160795607")

        Mockito.`when`(blacklistService.saveNewCpf(blacklistRequestDTO)).thenReturn(blacklistResponseDTO)

        blacklistController.saveNewCpf(blacklistRequestDTO)

        Mockito.verify(blacklistService, Mockito.times(1)).saveNewCpf(blacklistRequestDTO)
    }

    @Test(expected = ResourceNotFoundBankException::class)
    fun throwAnExceptionWhenNotFindByCpf() {
        val cpf = "02160795607"

        Mockito.`when`(blacklistService.findByCpf(cpf)).thenThrow(ResourceNotFoundBankException::class.java)

        blacklistController.getByCpf(cpf)
    }

    @Test
    fun findByIdWithSuccess() {
        val cpf = "02160795607"
        val blacklistResponseDTO = BlacklistResponseDTO(1, cpf)

        Mockito.`when`(blacklistService.findByCpf(cpf)).thenReturn(blacklistResponseDTO)

        blacklistController.getByCpf(cpf)

        Mockito.verify(blacklistService, Mockito.times(1)).findByCpf(cpf)
    }
}