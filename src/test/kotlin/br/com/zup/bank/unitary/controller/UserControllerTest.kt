package br.com.zup.bank.unitary.controller

import br.com.zup.bank.controller.UserController
import br.com.zup.bank.dto.request.UserRequestDTO
import br.com.zup.bank.dto.response.success.UserResponseDTO
import br.com.zup.bank.service.IUserService
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.bind.MissingServletRequestParameterException

/**
 * Created by Victor Santos on 08/01/2020
 */
@RunWith(SpringRunner::class)
class UserControllerTest {
    private val userController: UserController = UserController(
        Mockito.mock(IUserService::class.java)
    )
    private lateinit var userRequestDTO: UserRequestDTO
    private lateinit var userResponseDTO: UserResponseDTO

    @Before
    fun setMocks() {
        userRequestDTO = UserRequestDTO("Victor", "02160795607", "victor@gmail.com")
        userResponseDTO = UserResponseDTO(1, "Victor", "02160795607", "victor@gmail.com", true)
    }

    @Test(expected = MissingServletRequestParameterException::class)
    fun createUserWithInvalidFields() {
        userRequestDTO.name = ""
        userRequestDTO.cpf = ""
        userRequestDTO.email = ""

        Mockito.`when`(userController.userService.createUser(userRequestDTO)).thenReturn(userResponseDTO)

        val response = userController.createUser(userRequestDTO)

        Assert.assertEquals(true, true)
    }
}