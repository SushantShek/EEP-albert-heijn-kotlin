package com.ahold.ctp.sandbox.service

import com.ahold.ctp.sandbox.entity.Delivery
import com.ahold.ctp.sandbox.repository.DeliveryRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

@Service
class DeliveryService(private val deliveryRepository: DeliveryRepository) {

    private val logger = LoggerFactory.getLogger(DeliveryService::class.java)

    @Transactional(readOnly = true)
    fun getAllDeliveries(): List<Delivery> {
        return deliveryRepository.findAll()
    }

    @Transactional
    fun addDelivery(delivery: Delivery): Delivery {
        // Validate delivery fields
        if (delivery.vehicleId.isBlank() || delivery.status.isBlank()) {
            throw IllegalArgumentException("VehicleId, StartedAt, and Status are required fields")
        }

        // Additional validation logic can be added here

        // Save delivery to the database
        return deliveryRepository.save(delivery)
    }

    @Transactional
    fun updateDelivery(id: UUID, updatedDelivery: Delivery): Delivery {
        val existingDelivery = deliveryRepository.findById(id)
            .orElseThrow { NoSuchElementException("Delivery with id $id not found") }

        existingDelivery.vehicleId = updatedDelivery.vehicleId
        existingDelivery.startedAt = updatedDelivery.startedAt
        existingDelivery.status = updatedDelivery.status

        return deliveryRepository.save(existingDelivery)
    }

    fun save(delivery: Delivery): Delivery {
        // Validate delivery fields
        if (delivery.vehicleId.isNullOrBlank() || delivery.status.isNullOrBlank() || !listOf(
                "IN_PROGRESS",
                "DELIVERED"
            ).contains(delivery.status)
        ) {
            throw IllegalArgumentException("VehicleId, StartedAt, and Status are required fields and Status should be either IN_PROGRESS or DELIVERED")
        }
        return deliveryRepository.save(delivery)
    }

    fun saveAll(deliveries: List<Map<String, String>>): MutableList<Delivery> {
        val listBulkDelivery = mutableListOf<Delivery>()
        for (delivery in deliveries) {
            val deliveryToUpdate = findById(UUID.fromString(delivery["id"]))
            if (deliveryToUpdate.isEmpty) {
                continue
            }
            val existingDelivery = deliveryToUpdate.get()

            // Update only non-null properties
            try {
                delivery["finishedAt"]?.let {
                    existingDelivery.finishedAt = LocalDateTime.parse(it, DateTimeFormatter.ISO_DATE_TIME)
                }
            } catch (e: DateTimeParseException) {
                logger.error("Invalid date format for finishedAt", e)
                continue
            }
            delivery["status"]?.let { existingDelivery.status = it }

            listBulkDelivery.add(existingDelivery)
        }
        return deliveryRepository.saveAll(listBulkDelivery)
    }

    fun findById(id: UUID): Optional<Delivery> {
        return deliveryRepository.findById(id)
    }

    fun getSummary(): Map<String, Any> {
        val yesterday = LocalDateTime.now().minusDays(1)
        val startOfYesterday = yesterday.withHour(0).withMinute(0).withSecond(0).withNano(0)
        val endOfYesterday = yesterday.withHour(23).withMinute(59).withSecond(59).withNano(999999999)

        val yesterdayDeliveries = deliveryRepository.findAllByStartedAtBetween(startOfYesterday, endOfYesterday)
            .sortedBy { it.startedAt }

        val deliveriesCount = yesterdayDeliveries.size

        var totalMinutesBetweenDeliveries = 0L
        for (i in 1 until yesterdayDeliveries.size) {
            val diff = Duration.between(yesterdayDeliveries[i - 1].startedAt, yesterdayDeliveries[i].startedAt)
            totalMinutesBetweenDeliveries += diff.toMinutes()
        }

        val averageMinutesBetweenDeliveries =
            if (deliveriesCount > 1) totalMinutesBetweenDeliveries / (deliveriesCount - 1) else 0

        return mapOf(
            "deliveries" to deliveriesCount,
            "averageMinutesBetweenDeliveryStart" to averageMinutesBetweenDeliveries
        )
    }
}