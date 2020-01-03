package br.com.zup.bank.controller

import br.com.zup.bank.dto.request.AccountRequestDTO
import br.com.zup.bank.dto.response.error.ErrorResponse
import br.com.zup.bank.dto.response.success.AccountBalanceDTO
import br.com.zup.bank.dto.response.success.AccountResponseDTO
import br.com.zup.bank.exception.BankException
import br.com.zup.bank.service.IAccountService
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.validation.ObjectError
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

/**
 * Created by Victor Santos on 26/12/2019
 */
@RestController
@RequestMapping("api/account")
class AccountController {

    @Autowired
    private lateinit var accountService: IAccountService

    @ApiOperation(value = "Cadastra um usuário")
    @ApiResponses(
        ApiResponse(code = 200, message = "Requisição feita com sucesso!", response = AccountResponseDTO::class),
        ApiResponse(code = 400, message = "Algum dado é inválido", response = ErrorResponse::class)
    )
    @PostMapping
    fun newAccount(
        @RequestBody @Valid accountRequestDTO: AccountRequestDTO,
        result: BindingResult
    ): ResponseEntity<AccountResponseDTO> {
        if (result.hasErrors()) {
            badRequest(result.allErrors)
        }

        return ResponseEntity.ok(accountService.createAccount(accountRequestDTO))
    }

    @ApiOperation(value = "Retorna todas as contas")
    @ApiResponses(
        ApiResponse(code = 200, message = "Requisição feita com sucesso!", response = AccountResponseDTO::class, responseContainer = "List")
    )
    @GetMapping
    fun getAllAccounts(): ResponseEntity<MutableList<AccountResponseDTO>> {
        return ResponseEntity.ok(accountService.getAll())
    }

    @ApiOperation(value = "Retorna uma conta pelo id")
    @ApiResponses(
        ApiResponse(code = 200, message = "Requisição feita com sucesso!", response = AccountResponseDTO::class)
    )
    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<AccountResponseDTO> {
        return ResponseEntity.ok(accountService.getById(id))
    }

    @ApiOperation(value = "Retorna uma conta pelo cpf")
    @ApiResponses(
        ApiResponse(code = 200, message = "Requisição feita com sucesso!", response = AccountResponseDTO::class)
    )
    @GetMapping("/cpf/{cpf}")
    fun getByUserCpf(@PathVariable cpf: String): ResponseEntity<AccountResponseDTO> {
        return ResponseEntity.ok(accountService.getByCpf(cpf))
    }

    @ApiOperation(value = "Retorna uma conta pelo número de conta")
    @ApiResponses(
        ApiResponse(code = 200, message = "Requisição feita com sucesso!", response = AccountResponseDTO::class)
    )
    @GetMapping("/number/{accNumber}")
    fun getByAccountNumber(
        @PathVariable @ApiParam(value = "Número da conta") accNumber: String
    ): ResponseEntity<AccountResponseDTO> {
        return ResponseEntity.ok(accountService.getByAccountNumber(accNumber))
    }

    @ApiOperation(value = "Retorna extrato de uma conta pelo seu número")
    @ApiResponses(
        ApiResponse(code = 200, message = "Requisição feita com sucesso!", response = AccountResponseDTO::class)
    )
    @GetMapping("/balance/{accNumber}")
    fun getAccountBalance(
        @PathVariable @ApiParam(value = "Número da conta") accNumber: String
    ): ResponseEntity<AccountBalanceDTO> {
        return ResponseEntity.ok(accountService.getAccountBalance(accNumber))
    }

    private fun badRequest(objectErrors: MutableList<ObjectError>) {
        val errors = mutableListOf<String>()

        objectErrors.forEach {
            errors.add(it.defaultMessage.toString())
        }

        throw BankException(400, errors)
    }
}