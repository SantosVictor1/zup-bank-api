package br.com.zup.bank.controller

import br.com.zup.bank.dto.request.AccountRequestDTO
import br.com.zup.bank.dto.response.error.ErrorResponse
import br.com.zup.bank.dto.response.error.ErrorSupport
import br.com.zup.bank.dto.response.success.AccountResponseDTO
import br.com.zup.bank.dto.response.success.UserAccountResponseDTO
import br.com.zup.bank.service.AccountService
import br.com.zup.bank.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

/**
 * Created by Victor Santos on 26/12/2019
 */
@RestController
@RequestMapping("api/account")
class AccountController {

    @Autowired
    private lateinit var userService: UserService
    @Autowired
    private lateinit var accountService: AccountService

    @PostMapping
    fun newAccount(
        @RequestBody @Valid accountRequestDTO: AccountRequestDTO,
        result: BindingResult
    ): ResponseEntity<Any> {
        if (result.hasErrors()) {
            val errors = mutableListOf<ErrorSupport>()
            result.allErrors.forEach {
                errors.add(ErrorSupport(it.defaultMessage.toString()))
            }
            return ResponseEntity(ErrorResponse(400, errors), HttpStatus.BAD_REQUEST)
        }

        val user = userService.findByCpf(accountRequestDTO.cpf!!)

        return ResponseEntity.ok(accountService.createAccount(user))
    }

    @GetMapping
    fun getAllAccounts(): ResponseEntity<MutableList<AccountResponseDTO>> {
        val accounts = accountService.getAll()
        val accResponse = mutableListOf<AccountResponseDTO>()

        accounts.forEach {
            accResponse.add(AccountResponseDTO(it.id, it.limit, it.balance, UserAccountResponseDTO(it.user?.name, it.user?.cpf)))
        }
        return ResponseEntity.ok(accResponse)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<AccountResponseDTO> {
        val account = accountService.getById(id)
        val userAccResponse = UserAccountResponseDTO(account.user?.name, account.user?.cpf)
        val accResponse = AccountResponseDTO(account.id, account.limit, account.balance, userAccResponse)

        return ResponseEntity.ok(accResponse)
    }

    @GetMapping("/cpf/{cpf}")
    fun getByUserCpf(@PathVariable cpf: String): ResponseEntity<AccountResponseDTO> {
        val account = accountService.getByCpf(cpf)
        val userAccResponse = UserAccountResponseDTO(account.user?.name, account.user?.cpf)
        val accResponse = AccountResponseDTO(account.id, account.limit, account.balance, userAccResponse)

        return ResponseEntity.ok(accResponse)
    }
}