package com.ahold.ctp.sandbox.controller

import com.ahold.ctp.sandbox.entity.Delivery
import com.ahold.ctp.sandbox.service.DeliveryService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

@RestController
@RequestMapping("/app/v1")
class DeliveryController(private val service: DeliveryService) {

    private val logger = LoggerFactory.getLogger(DeliveryController::class.java)

    @GetMapping("/deliveries")
    fun getDeliveries(): List<Delivery> {
        return service.getAllDeliveries()
    }

    @GetMapping("/deliveries/business-summary")
    fun getBusinessSummary(): ResponseEntity<Map<String, Any>> {
        val summary = service.getSummary()
        return ResponseEntity(summary, HttpStatus.OK)
    }

    @PostMapping("/deliveries")
    fun addDelivery(@RequestBody delivery: Delivery): ResponseEntity<Any> {
        // Save delivery to the database
        val savedDelivery = service.addDelivery(delivery)//service.save(delivery)
        return ResponseEntity(savedDelivery, HttpStatus.CREATED)
    }


    @PatchMapping("/deliveries/{id}")
    fun updateDelivery(
        @PathVariable id: UUID,
        @RequestBody userData: Map<String, String>
    ): ResponseEntity<Any> {
        val deliveryToUpdate = service.findById(id)
        if (deliveryToUpdate.isEmpty) {
            return ResponseEntity("Delivery not found", HttpStatus.NOT_FOUND)
        }

        val existingDelivery = deliveryToUpdate.get()

        // Update only non-null properties
        try {
            userData["finishedAt"]?.let {
                existingDelivery.finishedAt = LocalDateTime.parse(it, DateTimeFormatter.ISO_DATE_TIME)
            }
        } catch (e: DateTimeParseException) {
            logger.error("Invalid date format for finishedAt", e)
            return ResponseEntity("Invalid date format for finishedAt", HttpStatus.BAD_REQUEST)
        }
        userData["status"]?.let { existingDelivery.status = it }

        // Save the updated delivery
        val savedDelivery = service.save(existingDelivery)
        return ResponseEntity(savedDelivery, HttpStatus.OK)
    }

    @PatchMapping("/deliveries/bulk-update")
    fun updateBulkDelivery(
        @RequestBody bulkDeliveries: List<Map<String, String>>
    ): ResponseEntity<Any> {


        // Save the updated delivery
        val saveAll = service.saveAll(bulkDeliveries)
        return ResponseEntity(saveAll, HttpStatus.OK)
    }
}