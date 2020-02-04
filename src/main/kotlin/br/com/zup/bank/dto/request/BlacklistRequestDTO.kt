package br.com.zup.bank.dto.request

import org.hibernate.validator.constraints.br.CPF
import javax.validation.constraints.NotBlank

data class BlacklistRequestDTO(
    @field:[CPF(message = "cpf.invalid")]
    @field:[NotBlank(message = "cpf.required")]
    val cpf: String
)