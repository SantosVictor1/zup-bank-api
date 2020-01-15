package br.com.zup.bank.unitary.controller

import br.com.zup.bank.controller.TransferController
import br.com.zup.bank.dto.request.TransferRequestDTO
import br.com.zup.bank.dto.response.success.NewTransferResponseDTO
import br.com.zup.bank.exception.InvalidResourceBankException
import br.com.zup.bank.service.ITransferService
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import org.springframework.http.HttpStatus
import org.springframework.kafka.core.KafkaTemplate
import java.util.*

/**
 * Created by Victor Santos on 11/01/2020
 */
class TransferControllerUnitaryTest {
    private val transferService: ITransferService = Mockito.mock(ITransferService::class.java)
    private val transferController: TransferController = TransferController(
        transferService,
        Mockito.mock(KafkaTemplate::class.java) as KafkaTemplate<String, String>
    )

    @Test(expected = InvalidResourceBankException::class)
    fun throwAnExceptionWhenTransferIsInvalid() {
        val transferRequestDTO = TransferRequestDTO("4657891235", "6584973246", "02160795607", -100.0)

        Mockito.`when`(transferService.newTransfer(transferRequestDTO)).thenThrow(InvalidResourceBankException::class.java)

        transferController.newTransfer(transferRequestDTO)
    }

    @Test
    fun makeATransferWithSuccess() {
        val transferRequestDTO = TransferRequestDTO("4657891235", "6584973246", "02160795607", 100.0)
        val transferResponseDTO = NewTransferResponseDTO(Date(), 100.0, "6584973246")

        Mockito.`when`(transferService.newTransfer(transferRequestDTO)).thenReturn(transferResponseDTO)

        val response = transferController.newTransfer(transferRequestDTO)

        Assert.assertEquals(response.statusCodeValue, HttpStatus.OK.value())
        Assert.assertEquals(response.body, transferResponseDTO)

        Mockito.verify(transferService, Mockito.times(1)).newTransfer(transferRequestDTO)
    }

}