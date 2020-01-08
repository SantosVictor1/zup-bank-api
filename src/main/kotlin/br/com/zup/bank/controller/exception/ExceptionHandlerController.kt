package br.com.zup.bank.controller.exception

import br.com.zup.bank.common.Message
import br.com.zup.bank.dto.response.error.ErrorResponse
import br.com.zup.bank.dto.response.error.ErrorSupport
import br.com.zup.bank.dto.response.error.FieldError
import br.com.zup.bank.dto.response.error.ObjectErrorResponse
import br.com.zup.bank.exception.BankException
import br.com.zup.bank.exception.DuplicatedResourceException
import br.com.zup.bank.exception.InvalidResourceException
import br.com.zup.bank.exception.ResourceNotFoundException
import org.springframework.dao.EmptyResultDataAccessException
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
    fun handleResourceNotFoundException(e: ResourceNotFoundException): ResponseEntity<ObjectErrorResponse> {
        val errorMessage = message.getMessage(e.errorCode)
        var fields = mutableListOf<FieldError>(FieldError(e.errorCode, e.field, errorMessage))

        var objectErrorResponse = ObjectErrorResponse(HttpStatus.NOT_FOUND.value(), e.objectName, fields)

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(objectErrorResponse)
    }

    @ExceptionHandler(DuplicatedResourceException::class)
    fun handleDuplicatedResourceException(e: DuplicatedResourceException): ResponseEntity<ObjectErrorResponse> {
        lateinit var objectErrorResponse: ObjectErrorResponse
        val errorMessage = message.getMessage(e.errorCode)
        var fields = mutableListOf<FieldError>(FieldError(e.errorCode, e.field, errorMessage))

        objectErrorResponse = ObjectErrorResponse(HttpStatus.BAD_REQUEST.value(), e.objectName, fields)

        return ResponseEntity.badRequest().body(objectErrorResponse)
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingArgumentException(e: MissingServletRequestParameterException): ResponseEntity<ObjectErrorResponse> {
        val errorCode = "missing.query.parameter"
        lateinit var objectErrorResponse: ObjectErrorResponse
        val errorMessage = message.getMessage(errorCode)
        var fields = mutableListOf<FieldError>(FieldError(errorCode, e.parameterName, errorMessage))

        objectErrorResponse = ObjectErrorResponse(HttpStatus.BAD_REQUEST.value(), "", fields)

        return ResponseEntity.badRequest().body(objectErrorResponse)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<ObjectErrorResponse> {
        lateinit var objectErrorResponse: ObjectErrorResponse
        var fields: MutableList<FieldError> = mutableListOf()

        e.bindingResult.fieldErrors.forEach {
            val errorMessage = message.getMessage(it.defaultMessage.toString())
            fields.add(FieldError(it.defaultMessage.toString(), it.field, errorMessage))
        }

        objectErrorResponse = ObjectErrorResponse(HttpStatus.BAD_REQUEST.value(), e.bindingResult.objectName, fields)

        return ResponseEntity.badRequest().body(objectErrorResponse)
    }

    @ExceptionHandler(InvalidResourceException::class)
    fun handleInvalidResourceException(e: InvalidResourceException): ResponseEntity<ObjectErrorResponse> {
        val errorMessage = message.getMessage(e.errorCode)
        var fields = mutableListOf<FieldError>(FieldError(e.errorCode, e.field, errorMessage))

        val objectErrorResponse = ObjectErrorResponse(HttpStatus.BAD_REQUEST.value(), e.objectName, fields)

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(objectErrorResponse)
    }
}