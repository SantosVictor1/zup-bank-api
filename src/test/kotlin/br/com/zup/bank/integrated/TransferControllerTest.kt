package br.com.zup.bank.integrated

import br.com.zup.bank.dto.request.TransferRequestDTO
import br.com.zup.bank.enums.Status
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional

/**
 * Created by Victor Santos on 10/01/2020
 */
@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
class TransferControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc
    private val baseUrl: String = "http://localhost:8080/api/transfer"

    @Test
    fun throwAnExceptionWhenReceiveARequestWithInvalidFields() {
        mvc.perform(MockMvcRequestBuilders
            .post(baseUrl)
            .content(asJsonString(TransferRequestDTO("", "", "", 100.0, transferId = null)))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.statusHttp").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("$.objectName").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("$.fields").isNotEmpty)
    }

    @Transactional
    @SqlGroup(
        Sql("/scripts/UserSQL.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        Sql("/scripts/AccountSQL.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    )
    @Test
    fun doATransferWithSuccess() {
        val transferRequestDTO = TransferRequestDTO(
            "6548732156",
            "6548732157",
            "42511229846",
            100.0,
            transferId = null
        )
        mvc.perform(MockMvcRequestBuilders
            .post(baseUrl)
            .content(asJsonString(transferRequestDTO))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.transferId").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.transferStatus").value(Status.IN_PROCESS.toString()))
    }

    @Transactional
    @SqlGroup(
        Sql("/scripts/UserSQL.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        Sql("/scripts/AccountSQL.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        Sql("/scripts/TransferSQL.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    )
    @Test
    fun throwAnExceptionWhenNotFindById() {
        mvc.perform(MockMvcRequestBuilders
            .get("$baseUrl/{id}", 2)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andExpect(MockMvcResultMatchers.jsonPath("$.statusHttp").value(404))
            .andExpect(MockMvcResultMatchers.jsonPath("$.objectName").value("Transfer"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.fields[0].errorCode")
                .value("transfer.not.found"))
    }

    @Transactional
    @SqlGroup(
        Sql("/scripts/UserSQL.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        Sql("/scripts/AccountSQL.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        Sql("/scripts/TransferSQL.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    )
    @Test
    fun getTransferStatusWithSuccess() {
        mvc.perform(MockMvcRequestBuilders
            .get("$baseUrl/{id}", 1)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.transferId").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.transferStatus").value("COMPLETED"))
    }

    private fun asJsonString(transferRequestDTO: TransferRequestDTO): String {
        try {
            return ObjectMapper().writeValueAsString(transferRequestDTO)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}