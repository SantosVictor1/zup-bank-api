package br.com.zup.bank

import br.com.zup.bank.model.User
import br.com.zup.bank.repository.UserRepository
import br.com.zup.bank.service.UserService
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.springframework.test.context.junit4.SpringRunner

/**
 * Created by Victor Santos on 23/12/2019
 */
@RunWith(SpringRunner::class)
class UserServiceTest {
    @InjectMocks
    private lateinit var userService: UserService

    @Mock
    private lateinit var userRepository: UserRepository

    @Test
    fun getAllTest() {
        var userList: MutableList<User> = mutableListOf<User>()
        userList.add(User(1, "Victor", "12345678912", "victor@gmail.com"))

        Mockito.`when`(userRepository.findAll()).thenReturn(userList)

        userService.getAll()

        Mockito.verify(userRepository, Mockito.times(1)).findAll()
    }
}