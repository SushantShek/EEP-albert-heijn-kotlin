package com.ahold.ctp.sandbox.controller

import com.ahold.ctp.sandbox.database.DeliveryRepository
import com.ahold.ctp.sandbox.entity.Delivery
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/deliveries")
class DeliveryController(private val deliveryRepository: DeliveryRepository) {

    @GetMapping
    fun getAllDeliveries(): List<Delivery> {
        return deliveryRepository.findAll()
    }

    @PostMapping
    fun addDelivery(@RequestBody delivery: Delivery): ResponseEntity<Any> {
        // Validate delivery fields
        if (delivery.vehicleId.isBlank() || delivery.startedAt == null || delivery.status.isBlank()) {
            return ResponseEntity("VehicleId, StartedAt, and Status are required fields", HttpStatus.BAD_REQUEST)
        }

        // Additional validation logic can be added here

        // Save delivery to the database
        val savedDelivery = deliveryRepository.save(delivery)
        return ResponseEntity(savedDelivery, HttpStatus.CREATED)
    }

    @PatchMapping("/{id}")
    fun updateDelivery(
        @PathVariable id: UUID,
        @RequestBody updatedDelivery: Delivery
    ): ResponseEntity<Any> {
        val deliveryToUpdate = deliveryRepository.findById(id)
        if (deliveryToUpdate.isEmpty) {
            return ResponseEntity("Delivery not found", HttpStatus.NOT_FOUND)
        }

        var existingDelivery = deliveryToUpdate.get()

        // Update only non-null properties
        updatedDelivery.vehicleId.takeIf { it.isNotBlank() }?.let { existingDelivery.vehicleId = it }
        updatedDelivery.startedAt?.let { existingDelivery.startedAt = it }
        updatedDelivery.finishedAt?.let { existingDelivery.finishedAt = it }
        updatedDelivery.status.takeIf { it.isNotBlank() }?.let { existingDelivery.status = it }

        // Save the updated delivery
        val savedDelivery = deliveryRepository.save(existingDelivery)
        return ResponseEntity(savedDelivery, HttpStatus.OK)
    }
}