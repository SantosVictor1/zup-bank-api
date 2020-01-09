package br.com.zup.bank.integrated

import br.com.zup.bank.dto.request.UserRequestDTO
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Profile
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.lang.RuntimeException
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

/**
 * Created by Victor Santos on 08/01/2020
 */
@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest() {
    @Autowired
    private lateinit var mvc: MockMvc
    private val baseUrl: String = "http://localhost:8080/api/users"

    @Test
    fun throwExceptionWhenCreateUserWithInvalidFields() {
        mvc.perform(MockMvcRequestBuilders
            .post(baseUrl)
            .content(asJsonString(UserRequestDTO("", "", "")))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.statusHttp").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("$.objectName").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("$.fields").isNotEmpty)
    }

    @Test
    fun createUserWithValidFields() {
        mvc.perform(MockMvcRequestBuilders
            .post(baseUrl)
            .content(asJsonString(UserRequestDTO("Pedro", "42511229846", "pedro@gmail.com")))
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").isString)
    }


    private fun asJsonString(userRequestDTO: UserRequestDTO): String {
        try {
            return ObjectMapper().writeValueAsString(userRequestDTO)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}