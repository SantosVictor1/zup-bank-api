package br.com.zup.bank.controller.exception

import br.com.zup.bank.dto.response.error.ErrorResponse
import br.com.zup.bank.service.exception.BankException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import javax.servlet.http.HttpServletRequest

/**
 * Created by Victor Santos on 23/12/2019
 */
@ControllerAdvice
class ExceptionHandlerController {

    @ExceptionHandler(BankException::class)
    fun throwException(e: BankException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(e.httpStatus).body(ErrorResponse(e.httpStatus, e.errorMessage))
    }
}