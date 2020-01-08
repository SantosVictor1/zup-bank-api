package br.com.zup.bank.common

/**
 * Created by Victor Santos on 08/01/2020
 */
class BankErrorCode {
    var code: String

    private constructor(code: String) {
        this.code = code
    }

    companion object {
        //System errors
        val BANK001: BankErrorCode = BankErrorCode("missing.query.parameter")

        //User errors
        val BANK010: BankErrorCode = BankErrorCode("name.required")
        val BANK011: BankErrorCode = BankErrorCode("name.size.invalid")
        val BANK012: BankErrorCode = BankErrorCode("email.required")
        val BANK013: BankErrorCode = BankErrorCode("email.invalid")
        val BANK014: BankErrorCode = BankErrorCode("cpf.invalid")
        val BANK015: BankErrorCode = BankErrorCode("cpf.required")
        val BANK016: BankErrorCode = BankErrorCode("cpf.duplicated")
        val BANK017: BankErrorCode = BankErrorCode("email.duplicated")
        val BANK018: BankErrorCode = BankErrorCode("user.not.found")

        //Account errors
        val BANK020: BankErrorCode = BankErrorCode("account.number.required")
        val BANK021: BankErrorCode = BankErrorCode("account.number.size.invalid")
        val BANK022: BankErrorCode = BankErrorCode("account.not.found")

        //Transfer errors
        val BANK030: BankErrorCode = BankErrorCode("origin.account.required")
        val BANK031: BankErrorCode = BankErrorCode("destiny.account.required")
    }
}