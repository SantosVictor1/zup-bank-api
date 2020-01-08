package br.com.zup.bank.integrated

import br.com.zup.bank.controller.UserController
import br.com.zup.bank.dto.request.UserRequestDTO
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

/**
 * Created by Victor Santos on 08/01/2020
 */
@RunWith(SpringRunner::class)
@WebMvcTest(UserController::class)
@WebAppConfiguration
class UserControllerTest() {
    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    fun createUserWithInvalidFields() {
        mvc.perform(MockMvcRequestBuilders
            .post("api/user")
            .content(UserRequestDTO("", "", "").toString())
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isBadRequest)
    }
}