package br.com.zup.bank.unitary.controller

import br.com.zup.bank.controller.TransferController
import br.com.zup.bank.dto.request.TransferRequestDTO
import br.com.zup.bank.dto.response.success.NewTransferResponseDTO
import br.com.zup.bank.dto.response.success.StatusResponseDTO
import br.com.zup.bank.enums.Status
import br.com.zup.bank.exception.InvalidResourceBankException
import br.com.zup.bank.exception.ResourceNotFoundBankException
import br.com.zup.bank.service.IKafkaService
import br.com.zup.bank.service.ITransferService
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.springframework.http.HttpStatus
import java.util.*

/**
 * Created by Victor Santos on 11/01/2020
 */
class TransferControllerUnitaryTest {
    private val kafkaService: IKafkaService = Mockito.mock(IKafkaService::class.java)
    private val transferService: ITransferService = Mockito.mock(ITransferService::class.java)
    private val transferController: TransferController = TransferController(kafkaService, transferService)
    private lateinit var statusResponseDTO: StatusResponseDTO
    private lateinit var transferRequestDTO: TransferRequestDTO
    private lateinit var transferResponseDTO: NewTransferResponseDTO

    @Before
    fun setMocks() {
        this.statusResponseDTO = StatusResponseDTO(1, Status.COMPLETED, "")
        this.transferRequestDTO = TransferRequestDTO("4657891235", "6584973246", "02160795607", 100.0, transferId = null)
        this.transferResponseDTO = NewTransferResponseDTO(Date(), 100.0, "6584973246")
    }

    @Test(expected = ResourceNotFoundBankException::class)
    fun throwAnExceptionWhenNotFindAccount() {
        val transferRequestDTO = TransferRequestDTO("4657891235", "6584973246", "02160795607", -100.0, transferId = null)

        Mockito.`when`(kafkaService.newTransferRequest(transferRequestDTO)).thenThrow(ResourceNotFoundBankException::class.java)

        transferController.newTransfer(transferRequestDTO)
    }

    @Test
    fun makeATransferWithSuccess() {
        Mockito.`when`(kafkaService.newTransferRequest(transferRequestDTO)).thenReturn(this.statusResponseDTO)

        val response = transferController.newTransfer(transferRequestDTO)

        Assert.assertEquals(response.statusCodeValue, HttpStatus.OK.value())
        Assert.assertEquals(response.body, this.statusResponseDTO)

        Mockito.verify(kafkaService, Mockito.times(1)).newTransferRequest(this.transferRequestDTO)
    }

    @Test(expected = ResourceNotFoundBankException::class)
    fun throwAnExceptionWhenNotFindTransfer() {
        Mockito.`when`(transferService.getTransferStatus(2)).thenThrow(ResourceNotFoundBankException::class.java)

        transferController.getTransferStatus(2)
    }

    @Test
    fun getTransferWithSuccess() {
        Mockito.`when`(transferService.getTransferStatus(1)).thenReturn(this.statusResponseDTO)

        val response = transferController.getTransferStatus(1)

        Assert.assertEquals(response.statusCode, HttpStatus.OK)
        Assert.assertEquals(response.body, this.statusResponseDTO)

        Mockito.verify(transferService, Mockito.times(1)).getTransferStatus(1)
    }

}