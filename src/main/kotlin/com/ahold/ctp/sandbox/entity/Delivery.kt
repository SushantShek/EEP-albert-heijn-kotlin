package com.ahold.ctp.sandbox.entity

import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime
import java.util.*
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Entity
data class Delivery(
    @Id
    @GeneratedValue
    val id: UUID? = null,
    var vehicleId: String,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    var startedAt: LocalDateTime,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    var finishedAt: LocalDateTime?,
    var status: String
) {
    constructor() : this(UUID.randomUUID(), "", LocalDateTime.now(), null, "") {

    }
}

