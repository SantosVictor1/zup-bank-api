package br.com.zup.bank.integrated

import br.com.zup.bank.AbstractTestConfig
import br.com.zup.bank.dto.request.UserRequestDTO
import br.com.zup.bank.enums.Status
import com.google.gson.Gson
import org.hamcrest.CoreMatchers
import org.junit.Test
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional

/**
 * Created by Victor Santos on 08/01/2020
 */
@Transactional
class UserControllerTest : AbstractTestConfig() {
    private val baseUrl: String = "http://localhost:8080/users"

    @Test
    fun throwExceptionWhenCreateUserWithInvalidFields() {
        this.mvc.perform(MockMvcRequestBuilders
            .post(baseUrl)
            .content(toJson(UserRequestDTO("", "", "")))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.statusHttp").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.objectName").value("userRequestDTO"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.fields").value(CoreMatchers.notNullValue()))
    }

    @Test
    fun createUserWithValidFields() {
        mvc.perform(MockMvcRequestBuilders
            .post(baseUrl)
            .content(toJson(UserRequestDTO("Pedro", "42511229846", "pedro@gmail.com")))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("42511229846"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(Status.IN_PROCESS.toString()))
    }

    @Sql("/scripts/UserSQL.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    fun deactivateUserWithSuccess() {
        mvc.perform(MockMvcRequestBuilders
            .delete("$baseUrl/deactivate").param("cpf", "02160795607")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNoContent)
    }

    @Test
    fun throwExceptionWhenRequestIsMadeWithoutQueryParam() {
        mvc.perform(MockMvcRequestBuilders
            .delete("$baseUrl/deactivate")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.statusHttp").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.objectName").value(""))
            .andExpect(MockMvcResultMatchers.jsonPath("$.fields[0].errorCode").value("missing.query.parameter"))
    }

    @Sql("/scripts/UserSQL.sql")
    @Test
    fun getAllAndReturnAllUsers() {
        mvc.perform(MockMvcRequestBuilders
            .get("$baseUrl")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[1].id").value(2))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[2].id").value(3))
    }

    @Sql("/scripts/WaitlistSQL.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    fun getRegisterStatusWithSuccess() {
        mvc.perform(MockMvcRequestBuilders
            .get("$baseUrl/status").param("cpf", "16950459041")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("16950459041"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(Status.IN_PROCESS.toString()))
    }

    @Sql("/scripts/UserSQL.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    fun getByIdAndReturnTheUser() {
        mvc.perform(MockMvcRequestBuilders
            .get("$baseUrl/{id}", 1)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Victor"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("02160795607"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("victor@gmail.com"))
    }

    @Sql("/scripts/UserSQL.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    fun throwExceptionWhenRequestUserNotFound() {
        mvc.perform(MockMvcRequestBuilders
            .get("$baseUrl/{id}", 4)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNotFound)
            .andExpect(MockMvcResultMatchers.jsonPath("$.statusHttp").value(404))
            .andExpect(MockMvcResultMatchers.jsonPath("$.objectName").value("User"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.fields[0].errorCode").value("user.not.found"))
    }

    @Sql("/scripts/UserSQL.sql")
    @Test
    fun reactivateUserWithSuccess() {
        mvc.perform(MockMvcRequestBuilders
            .patch("$baseUrl/reactivate").param("cpf", "14292133611")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(3))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Aline"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("14292133611"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("aline@gmail.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.isActive").value(true))
    }

    @Test
    fun throwExceptionWhenRequestIsMadeWithoutQueryParamInReactivate() {
        mvc.perform(MockMvcRequestBuilders
            .patch("$baseUrl/reactivate")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.statusHttp").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.objectName").value(""))
            .andExpect(MockMvcResultMatchers.jsonPath("$.fields[0].errorCode").value("missing.query.parameter"))
    }

    private fun toJson(anyObject: Any): String {
        return Gson().toJson(anyObject)
    }
}