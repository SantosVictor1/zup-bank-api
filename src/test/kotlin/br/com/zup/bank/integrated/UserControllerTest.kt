package br.com.zup.bank.integrated

import br.com.zup.bank.dto.request.UserRequestDTO
import com.google.gson.Gson
import org.hamcrest.CoreMatchers
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.transaction.annotation.Transactional

/**
 * Created by Victor Santos on 08/01/2020
 */
@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private lateinit var mvc: MockMvc
    private val baseUrl: String = "http://localhost:8080/api/users"

    @Test
    fun throwExceptionWhenCreateUserWithInvalidFields() {
        mvc.perform(MockMvcRequestBuilders
            .post(baseUrl)
            .content(toJson(UserRequestDTO("", "", "")))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.statusHttp").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("$.objectName").value("userRequestDTO"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.fields").value(CoreMatchers.notNullValue()))
    }

    @Transactional
    @Test
    fun createUserWithValidFields() {
        mvc.perform(MockMvcRequestBuilders
            .post(baseUrl)
            .content(toJson(UserRequestDTO("Pedro", "42511229846", "pedro@gmail.com")))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(4))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Pedro"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("42511229846"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("pedro@gmail.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.isActive").value(true))
    }

    @Transactional
    @Sql("/scripts/UserSQL.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    fun deactivateUserWithSuccess() {
        mvc.perform(MockMvcRequestBuilders
            .delete("$baseUrl/deactivate").param("cpf", "02160795607")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNoContent)
    }

    @Transactional
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

    @Transactional
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

    @Transactional
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

    @Transactional
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

    @Transactional
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

    @Transactional
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