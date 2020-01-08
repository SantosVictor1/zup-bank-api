package br.com.zup.bank.controller.exception

import br.com.zup.bank.common.Message
import br.com.zup.bank.dto.response.error.ErrorResponse
import br.com.zup.bank.dto.response.error.ErrorSupport
import br.com.zup.bank.dto.response.error.FieldError
import br.com.zup.bank.dto.response.error.ObjectErrorResponse
import br.com.zup.bank.exception.BankException
import br.com.zup.bank.exception.ResourceNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

/**
 * Created by Victor Santos on 23/12/2019
 */
@ControllerAdvice
class ExceptionHandlerController(
    val message: Message
) {

    @ExceptionHandler(BankException::class)
    fun handleBankException(e: BankException): ResponseEntity<ErrorResponse> {
        var errors = mutableListOf<ErrorSupport>()

        e.errors.forEach {
            errors.add(ErrorSupport(it))
        }

        return ResponseEntity.status(e.httpStatus).body(ErrorResponse(e.httpStatus, errors))
    }

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFoundException(e: ResourceNotFoundException): ResponseEntity<ErrorResponse> {
        var errors = mutableListOf<ErrorSupport>()

        e.errors!!.forEach {
            errors.add(ErrorSupport(it))
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse(400, errors))
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingArgumentException(e: MissingServletRequestParameterException): ResponseEntity<Any> {
        var errors = mutableListOf<ErrorSupport>(ErrorSupport("Parâmetro obrigatório"))

        return ResponseEntity.badRequest().body(ErrorResponse(400, errors))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): Any {
        lateinit var objectErrorResponse: ObjectErrorResponse
        var fields: MutableList<FieldError> = mutableListOf()

        e.bindingResult.fieldErrors.forEach {
            val errorMessage = message.getMessage(it.defaultMessage.toString())
            fields.add(FieldError(it.defaultMessage.toString(), it.field, errorMessage))
        }

        objectErrorResponse = ObjectErrorResponse(HttpStatus.BAD_REQUEST.value(), e.bindingResult.objectName, fields)

        return ResponseEntity.badRequest().body(objectErrorResponse)
    }
}