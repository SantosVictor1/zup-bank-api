package br.com.zup.bank

import br.com.zup.bank.model.User
import br.com.zup.bank.repository.UserRepository
import br.com.zup.bank.service.UserService
import br.com.zup.bank.service.exception.BankException
import org.junit.Test
import org.junit.jupiter.api.fail
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.springframework.test.context.junit4.SpringRunner
import java.lang.RuntimeException

/**
 * Created by Victor Santos on 23/12/2019
 */
@RunWith(SpringRunner::class)
class UserServiceTest {
    @InjectMocks
    private lateinit var userService: UserService

    @Mock
    private lateinit var userRepository: UserRepository

//    @Test(expected = BankException::class)
//    fun existsByCpfTest() {
//        val user = User(1, "Victor", "02160795607", "victor@gmail.com")
//
//        Mockito.`when`(userRepository.existsByCpf(user.cpf)).thenReturn(true)
//        userService.createUser(user)
//
//        Mockito.verify(userRepository, Mockito.times(1)).existsByCpf(user.cpf)
//    }

//    @Test(expected = BankException::class)
//    fun existsByEmailTest() {
//        val user = User(1, "Victor", "02160795607", "teste@gmail.com")
//
//        Mockito.`when`(userRepository.existsByEmail(user.email)).thenReturn(true)
//        userService.createUser(user)
//
//        Mockito.verify(userRepository, Mockito.times(1)).existsByEmail(user.email)
//    }

//    @Test(expected = BankException::class)
//    fun isNameNullTest() {
//        var user = User(1, null, "02160795607", "teste@gmail.com")
//
//        userService.createUser(user)
//    }

//    @Test(expected = BankException::class)
//    fun isNameEmptyTest() {
//        var user = User(1, "", "02160795607", "teste@gmail.com")
//
//        userService.createUser(user)
//    }

//    @Test(expected = BankException::class)
//    fun lengthNameTest() {
//        var user = User(1, "an", "02160795607", "teste@gmail.com")
//
//        userService.createUser(user)
//    }

    @Test(expected = BankException::class)
    fun isCpfNullTest() {
        var user = User(1, "Victor", null, "victor@gmail.com")

        userService.createUser(user)
    }

//    @Test(expected = BankException::class)
//    fun isCpfEmptyTest() {
//        var user = User(1, "Victor", "", "victor@gmail.com")
//
//        userService.createUser(user)
//    }

    @Test
    fun getAllTest() {
        var userList: MutableList<User> = mutableListOf<User>()
        userList.add(User(1, "Victor", "12345678912", "victor@gmail.com"))

        Mockito.`when`(userRepository.findAll()).thenReturn(userList)

        userService.getAll()

        Mockito.verify(userRepository, Mockito.times(1)).findAll()
    }
}