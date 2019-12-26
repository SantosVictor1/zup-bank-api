package br.com.zup.bank

import br.com.zup.bank.dto.response.success.AccountResponseDTO
import br.com.zup.bank.model.User
import br.com.zup.bank.repository.AccountRepository
import br.com.zup.bank.service.AccountService
import br.com.zup.bank.service.exception.BankException
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.springframework.test.context.junit4.SpringRunner

/**
 * Created by Victor Santos on 26/12/2019
 */
@RunWith(SpringRunner::class)
class AccountServiceTest {
    @InjectMocks
    private lateinit var accountService: AccountService
    @Mock
    private lateinit var accountRepository: AccountRepository
    private lateinit var accResponse: AccountResponseDTO

    @Before
    fun setAccount() {
        accResponse = AccountResponseDTO(1, 1000.0, 0.0, null)
    }

    @Test(expected = BankException::class)
    fun existAccountWithCpf() {
        val user = User(1, "Noah", "65015739710", "nnoahguilhermepinto@publiout.com.br")
        Mockito.`when`(accountRepository.existsAccountByUserCpf(user.cpf!!)).thenReturn(true)
        accountService.createAccount(user)

        Mockito.verify(accountRepository, Mockito.times(1)).existsAccountByUserCpf(user.cpf!!)
    }
}