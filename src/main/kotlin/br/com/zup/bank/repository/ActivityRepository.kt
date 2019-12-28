package br.com.zup.bank.repository

import br.com.zup.bank.model.Activity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Created by Victor Santos on 27/12/2019
 */
@Repository
interface ActivityRepository : JpaRepository<Activity, Long> {
    fun findAllByOrderByActivityDateDesc(): List<Activity>
}