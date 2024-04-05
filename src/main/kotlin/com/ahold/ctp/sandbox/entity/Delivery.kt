package com.ahold.ctp.sandbox.entity

import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class Delivery(
    @Id
    @GeneratedValue
    val id: UUID? = null,
    var vehicleId: String,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val startedAt: LocalDateTime,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    var finishedAt: LocalDateTime?,
    var status: String
)

