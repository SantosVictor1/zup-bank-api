package br.com.zup.bank.integrated

import br.com.zup.bank.dto.request.AccountRequestDTO
import br.com.zup.bank.dto.request.ActivityRequestDTO
import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.CoreMatchers
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.SqlGroup
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional

/**
 * Created by Victor Santos on 09/01/2020
 */
@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc
    private val baseUrl: String = "http://localhost:8080/api/account"

    @Test
    fun throwExceptionWhenCreateUserWithInvalidFields() {
        mvc.perform(MockMvcRequestBuilders
            .post(baseUrl)
            .content(asJsonStringAccountRequest(AccountRequestDTO("")))
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
            .content(asJsonStringAccountRequest(AccountRequestDTO("02160795607")))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(4))
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

    @Transactional
    @SqlGroup(
        Sql("/scripts/UserSQL.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        Sql("/scripts/AccountSQL.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    )
    @Test
    fun getAccountByNumberWithSuccess() {
        mvc.perform(MockMvcRequestBuilders
            .get("$baseUrl/data")
            .param("accNumber", "6548732157")
            .param("cpf", "")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.accountNumber").value("6548732157"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.isActive").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.user").value(CoreMatchers.notNullValue()))
    }

    @Transactional
    @SqlGroup(
        Sql("/scripts/UserSQL.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        Sql("/scripts/AccountSQL.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    )
    @Test
    fun getAccountByUserCpfWithSuccess() {
        mvc.perform(MockMvcRequestBuilders
            .get("$baseUrl/data")
            .param("accNumber", "")
            .param("cpf", "42511229846")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.accountNumber").value("6548732157"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.isActive").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.user").value(CoreMatchers.notNullValue()))
    }

    @Transactional
    @SqlGroup(
        Sql("/scripts/UserSQL.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        Sql("/scripts/AccountSQL.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    )
    @Test
    fun throwAnExceptionWhenQueryParamIsNotFoundInBalanceRequest() {
        mvc.perform(MockMvcRequestBuilders
            .get("$baseUrl/balance")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.statusHttp").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.objectName").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("$.fields").isNotEmpty)
    }

    @Transactional
    @SqlGroup(
        Sql("/scripts/UserSQL.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        Sql("/scripts/AccountSQL.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    )
    @Test
    fun getAccountBalanceWithSuccess() {
        mvc.perform(MockMvcRequestBuilders
            .get("$baseUrl/balance")
            .param("accNumber", "6548732157")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.accNumber").value("6548732157"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.balance").value(1000.0))
    }

    @Test
    fun throwAnExceptionWithInvalidFieldsOnDeposit() {
        mvc.perform(MockMvcRequestBuilders
            .post("$baseUrl/deposit")
            .content(asJsonStringActivityRequest(ActivityRequestDTO("", "", 0.0)))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.statusHttp").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.objectName").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("$.fields").isNotEmpty)
    }

    @Transactional
    @SqlGroup(
        Sql("/scripts/UserSQL.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        Sql("/scripts/AccountSQL.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    )
    @Test
    fun doADepositWithSuccess() {
        mvc.perform(MockMvcRequestBuilders
            .post("$baseUrl/deposit")
            .content(asJsonStringActivityRequest(ActivityRequestDTO("02160795607", "6548732156", 100.0)))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.balance").value(1100))
            .andExpect(MockMvcResultMatchers.jsonPath("$.accNumber").value("6548732156"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.operation").value("DEPOSIT"))
    }

    @Test
    fun throwAnExceptionWithInvalidFieldsOnWithdraw() {
        mvc.perform(MockMvcRequestBuilders
            .post("$baseUrl/deposit")
            .content(asJsonStringActivityRequest(ActivityRequestDTO("", "", 0.0)))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.statusHttp").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.objectName").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("$.fields").isNotEmpty)
    }

    @Transactional
    @SqlGroup(
        Sql("/scripts/UserSQL.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        Sql("/scripts/AccountSQL.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    )
    @Test
    fun doAWithdrawWithSuccess() {
        mvc.perform(MockMvcRequestBuilders
            .post("$baseUrl/withdraw")
            .content(asJsonStringActivityRequest(ActivityRequestDTO("02160795607", "6548732156", 100.0)))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.balance").value(900))
            .andExpect(MockMvcResultMatchers.jsonPath("$.accNumber").value("6548732156"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.operation").value("WITHDRAW"))
    }

    @Test
    fun throwAnExceptionWhenQueryParamNotFoundOnExtract() {
        mvc.perform(MockMvcRequestBuilders
            .get("$baseUrl/extract")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.statusHttp").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.objectName").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("$.fields").isNotEmpty)
    }

    @Transactional
    @SqlGroup(
        Sql("/scripts/UserSQL.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        Sql("/scripts/AccountSQL.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        Sql("/scripts/ActivitySQL.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    )
    @Test
    fun getExtractWithSuccess() {
        mvc.perform(MockMvcRequestBuilders
            .get("$baseUrl/extract")
            .param("accNumber", "6548732156")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.extract").value(CoreMatchers.notNullValue()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.pagination").value(CoreMatchers.notNullValue()))
    }

    private fun asJsonStringAccountRequest(accountRequestDTO: AccountRequestDTO): String {
        try {
            return ObjectMapper().writeValueAsString(accountRequestDTO)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun asJsonStringActivityRequest(activityRequestDTO: ActivityRequestDTO): String {
        try {
            return ObjectMapper().writeValueAsString(activityRequestDTO)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}