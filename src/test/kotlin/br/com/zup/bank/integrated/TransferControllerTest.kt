package br.com.zup.bank.integrated

import br.com.zup.bank.AbstractTestConfig
import br.com.zup.bank.dto.request.TransferRequestDTO
import br.com.zup.bank.enums.Status
import com.google.gson.Gson
import org.hamcrest.CoreMatchers
import org.junit.Test
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional

/**
 * Created by Victor Santos on 10/01/2020
 */
@Transactional
class TransferControllerTest : AbstractTestConfig() {
    private val baseUrl: String = "http://localhost:8080/transfer"

    @Test
    fun throwAnExceptionWhenReceiveARequestWithInvalidFields() {
        mvc.perform(MockMvcRequestBuilders
            .post(baseUrl)
            .content(toJson(TransferRequestDTO("", "", "", 100.0, transferId = null)))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.statusHttp").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.objectName").value("transferRequestDTO"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.fields").value(CoreMatchers.notNullValue()))
    }

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
            .content(toJson(transferRequestDTO))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(Status.IN_PROCESS.toString()))
    }

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
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("COMPLETED"))
    }

    private fun toJson(anyObject: Any): String {
        return Gson().toJson(anyObject)
    }
}