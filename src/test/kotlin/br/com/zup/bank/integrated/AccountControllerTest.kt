package br.com.zup.bank.integrated

import br.com.zup.bank.dto.request.AccountRequestDTO
import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.CoreMatchers
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
import java.lang.RuntimeException

/**
 * Created by Victor Santos on 09/01/2020
 */
@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AccountControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc
    private val baseUrl: String = "http://localhost:8080/api/account"

    @Test
    fun throwExceptionWhenCreateUserWithInvalidFields() {
        mvc.perform(MockMvcRequestBuilders
            .post(baseUrl)
            .content(asJsonString(AccountRequestDTO("")))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.statusHttp").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("$.statusHttp").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.objectName").value("accountRequestDTO"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.fields").isNotEmpty)
    }

    @Transactional
    @Sql("/scripts/UserSQL.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    fun createAccountWithValidCpf() {
        mvc.perform(MockMvcRequestBuilders
            .post(baseUrl)
            .content(asJsonString(AccountRequestDTO("02160795607")))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.accountNumber").isNotEmpty)
            .andExpect(MockMvcResultMatchers.jsonPath("$.user").value(CoreMatchers.notNullValue()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.isActive").value(true))
    }

    @Transactional
    @SqlGroup(
        Sql("/scripts/UserSQL.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        Sql("/scripts/AccountSQL.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    )
    @Test
    fun getAllAndReturnAllAccounts() {
        mvc.perform(MockMvcRequestBuilders
            .get("$baseUrl")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
    }

    @Transactional
    @SqlGroup(
        Sql("/scripts/UserSQL.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        Sql("/scripts/AccountSQL.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    )
    @Test
    fun getByIdAndReturnTheUser() {
        mvc.perform(MockMvcRequestBuilders
            .get("$baseUrl/{id}", 3)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$.accountNumber").value("6548732158"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.isActive").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.user").value(CoreMatchers.notNullValue()))
    }


    @Transactional
    @SqlGroup(
        Sql("/scripts/UserSQL.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        Sql("/scripts/AccountSQL.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    )
    @Test
    fun throwAnExceptionWhenQueryParamIsNotFound() {
        mvc.perform(MockMvcRequestBuilders
            .get("$baseUrl/data")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.statusHttp").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.objectName").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("$.fields").isNotEmpty)
    }

    private fun asJsonString(accountRequestDTO: AccountRequestDTO): String {
        try {
            return ObjectMapper().writeValueAsString(accountRequestDTO)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}