package com.ahold.ctp.sandbox.repository

import com.ahold.ctp.sandbox.entity.Delivery
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.*

@Repository
interface DeliveryRepository : JpaRepository<Delivery, UUID> {
    fun findAllByStartedAtBetween(start: LocalDateTime, end: LocalDateTime): List<Delivery>
}
