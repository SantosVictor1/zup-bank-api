package br.com.zup.bank.controller

import br.com.zup.bank.dto.request.AccountRequestDTO
import br.com.zup.bank.dto.response.error.ErrorResponse
import br.com.zup.bank.dto.response.error.ErrorSupport
import br.com.zup.bank.dto.response.success.AccountResponseDTO
import br.com.zup.bank.dto.response.success.UserAccountResponseDTO
import br.com.zup.bank.service.IAccountService
import br.com.zup.bank.service.IUserService
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
    private lateinit var userService: IUserService
    @Autowired
    private lateinit var accountService: IAccountService

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

        var user = userService.findByCpf(accountRequestDTO.cpf!!)
        val acc = accountService.createAccount(user)
        val userResponse = UserAccountResponseDTO(user.name, user.cpf)

        return ResponseEntity.ok(AccountResponseDTO(acc.id, acc.limit, acc.balance, acc.accountNumber, userResponse))
    }

    @GetMapping
    fun getAllAccounts(): ResponseEntity<MutableList<AccountResponseDTO>> {
        val accounts = accountService.getAll()
        val accResponse = mutableListOf<AccountResponseDTO>()

        accounts.forEach {
            val user = UserAccountResponseDTO(it.user?.name, it.user?.cpf)
            accResponse.add(AccountResponseDTO(it.id, it.limit, it.balance, it.accountNumber, user))
        }
        return ResponseEntity.ok(accResponse)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<AccountResponseDTO> {
        val account = accountService.getById(id)

        return ResponseEntity.ok(accountService.getAccountDTO(account))
    }

    @GetMapping("/cpf/{cpf}")
    fun getByUserCpf(@PathVariable cpf: String): ResponseEntity<AccountResponseDTO> {
        val account = accountService.getByCpf(cpf)

        return ResponseEntity.ok(accountService.getAccountDTO(account))
    }

    @GetMapping("/account/{accNumber}")
    fun getByAccountNumber(@PathVariable accNumber: String): ResponseEntity<AccountResponseDTO> {
        val account = accountService.getByAccountNumber(accNumber)

        return ResponseEntity.ok(accountService.getAccountDTO(account))
    }
}