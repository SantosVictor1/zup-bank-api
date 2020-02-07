package br.com.zup.bank.unitary.service

import br.com.zup.bank.dto.request.BlacklistRequestDTO
import br.com.zup.bank.exception.DuplicatedResourceBankException
import br.com.zup.bank.exception.ResourceNotFoundBankException
import br.com.zup.bank.model.Blacklist
import br.com.zup.bank.repository.BlacklistRepository
import br.com.zup.bank.service.impl.BlacklistServiceImpl
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito

/**
 * @author Victor Santos
 * Created on 06/02/2020
 */
class BlacklistServiceTest {
    private val blacklistRepository = Mockito.mock(BlacklistRepository::class.java)
    private val blacklistService = BlacklistServiceImpl(blacklistRepository)


    @Test
    fun throwAnExceptionWhenDuplicatedCpf() {
        val blacklistRequestDTO = BlacklistRequestDTO("02160795607")
        Mockito.`when`(blacklistRepository.existsByCpf(blacklistRequestDTO.cpf)).thenReturn(true)

        val response = assertThrows<DuplicatedResourceBankException> { blacklistService.saveNewCpf(blacklistRequestDTO) }

        Assert.assertThat(response.errorCode, CoreMatchers.`is`("cpf.duplicated"))
        Assert.assertThat(response.field, CoreMatchers.`is`("cpf"))
        Assert.assertThat(response.objectName, CoreMatchers.`is`("BlacklistRequestDTO"))

        Mockito.verify(blacklistRepository, Mockito.times(1)).existsByCpf(blacklistRequestDTO.cpf)
    }

    @Test
    fun saveNewCpfWithSuccess() {
        val blacklistRequestDTO = BlacklistRequestDTO("02160795607")
        val blacklist = Blacklist(cpf = "02160795607")

        Mockito.`when`(blacklistRepository.existsByCpf(blacklistRequestDTO.cpf)).thenReturn(false)
        Mockito.`when`(blacklistRepository.save(blacklist)).thenReturn(blacklist.copy(id = 1))

        val response = blacklistService.saveNewCpf(blacklistRequestDTO)

        Assert.assertThat(response.cpf, CoreMatchers.`is`("02160795607"))
        Assert.assertThat(response.id, CoreMatchers.`is`(1L))

        Mockito.verify(blacklistRepository, Mockito.times(1)).existsByCpf(blacklistRequestDTO.cpf)
        Mockito.verify(blacklistRepository, Mockito.times(1)).save(blacklist)
    }

    @Test
    fun throwAnExceptionWhenNotFindByCpf() {
        val cpf = "02160795607"

        Mockito.`when`(blacklistRepository.findByCpf(cpf)).thenReturn(null)

        val response = assertThrows<ResourceNotFoundBankException> { blacklistService.findByCpf(cpf) }

        Assert.assertThat(response.errorCode, CoreMatchers.`is`("cpf.not.found"))
        Assert.assertThat(response.field, CoreMatchers.`is`("cpf"))
        Assert.assertThat(response.objectName, CoreMatchers.`is`("Blacklist"))

        Mockito.verify(blacklistRepository, Mockito.times(1)).findByCpf(cpf)
    }

    @Test
    fun findByCpfWithSuccess() {
        val cpf = "02160795607"
        val blacklist = Blacklist(1, cpf)

        Mockito.`when`(blacklistRepository.findByCpf(cpf)).thenReturn(blacklist.copy(id = 1))

        val response = blacklistService.findByCpf(cpf)

        Assert.assertThat(response.cpf, CoreMatchers.`is`(cpf))
        Assert.assertThat(response.id, CoreMatchers.`is`(1L))

        Mockito.verify(blacklistRepository, Mockito.times(1)).findByCpf(cpf)
    }

    @Test
    fun existsByCpfWithSuccess() {
        val cpf = "02160795607"

        Mockito.`when`(blacklistRepository.existsByCpf(cpf)).thenReturn(true)

        blacklistService.existsByCpf(cpf)

        Mockito.verify(blacklistRepository, Mockito.times(1)).existsByCpf(cpf)
    }

    @Test
    fun removeFromListWithSuccess() {
        val cpf = "02160795607"

        Mockito.`when`(blacklistRepository.existsByCpf(cpf)).thenReturn(true)
        Mockito.doNothing().`when`(blacklistRepository).deleteByCpf(cpf)

        blacklistService.removeFromList(cpf)

        Mockito.verify(blacklistRepository, Mockito.times(1)).deleteByCpf(cpf)
    }
}