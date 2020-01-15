package br.com.zup.bank.controller.exception

import br.com.zup.bank.common.BankErrorCode
import br.com.zup.bank.common.Message
import br.com.zup.bank.dto.response.error.FieldError
import br.com.zup.bank.dto.response.error.ObjectErrorResponse
import br.com.zup.bank.exception.DuplicatedResourceBankException
import br.com.zup.bank.exception.InvalidResourceBankException
import br.com.zup.bank.exception.ResourceNotFoundBankException
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
    @ExceptionHandler(ResourceNotFoundBankException::class)
    fun handleResourceNotFoundException(e: ResourceNotFoundBankException): ResponseEntity<ObjectErrorResponse> {
        val errorMessage = message.getMessage(e.errorCode)
        val fields = mutableListOf(FieldError(e.errorCode, e.field, errorMessage))

        val objectErrorResponse = getObjectErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, e.objectName, fields)

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(objectErrorResponse)
    }

    @ExceptionHandler(DuplicatedResourceBankException::class)
    fun handleDuplicatedResourceException(e: DuplicatedResourceBankException): ResponseEntity<ObjectErrorResponse> {
        val errorMessage = message.getMessage(e.errorCode)
        val fields = mutableListOf(FieldError(e.errorCode, e.field, errorMessage))

        val objectErrorResponse = getObjectErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, e.objectName, fields)

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(objectErrorResponse)
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingArgumentException(e: MissingServletRequestParameterException): ResponseEntity<ObjectErrorResponse> {
        val errorCode = BankErrorCode.BANK001.code
        val errorMessage = message.getMessage(errorCode)
        val fields = mutableListOf(FieldError(errorCode, e.parameterName, errorMessage))

        val objectErrorResponse = getObjectErrorResponse(HttpStatus.BAD_REQUEST, "", fields)

        return ResponseEntity.badRequest().body(objectErrorResponse)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<ObjectErrorResponse> {
        val fields: MutableList<FieldError> = mutableListOf()

        e.bindingResult.fieldErrors.forEach {
            val errorMessage = message.getMessage(it.defaultMessage.toString())
            fields.add(FieldError(it.defaultMessage.toString(), it.field, errorMessage))
        }

        val objectErrorResponse = getObjectErrorResponse(HttpStatus.BAD_REQUEST, e.bindingResult.objectName, fields)

        return ResponseEntity.badRequest().body(objectErrorResponse)
    }

    @ExceptionHandler(InvalidResourceBankException::class)
    fun handleInvalidResourceException(e: InvalidResourceBankException): ResponseEntity<ObjectErrorResponse> {
        val errorMessage = message.getMessage(e.errorCode)
        val fields = mutableListOf(FieldError(e.errorCode, e.field, errorMessage))

        val objectErrorResponse = getObjectErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, e.objectName, fields)

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(objectErrorResponse)
    }

    private fun getObjectErrorResponse(
        httpStatus: HttpStatus,
        objectName: String,
        fields: MutableList<FieldError>
    ): ObjectErrorResponse {
        return ObjectErrorResponse(httpStatus.value(), objectName, fields)
    }
}