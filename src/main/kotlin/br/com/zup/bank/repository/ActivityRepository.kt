package br.com.zup.bank.repository

import br.com.zup.bank.model.Activity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Created by Victor Santos on 27/12/2019
 */
@Repository
interface ActivityRepository : JpaRepository<Activity, Long> {

    /**
     * Método responsável por encontrar o histórico de uma conta ativa baseada em seu número
     *
     * @param  accNumber  Número de conta que será usado na busca
     * @param  pageable Pageable usado para paginação do retorno
     *
     * @return Histórico paginado
     */
    fun findAllByAccountAccountNumberOrderByActivityDateDesc(accNumber: String, pageable: Pageable): Page<Activity>
}