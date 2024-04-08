package com.ahold.ctp.sandbox.controller

import com.ahold.ctp.sandbox.entity.Delivery
import com.ahold.ctp.sandbox.service.DeliveryService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.time.LocalDateTime
import java.util.*

@SpringBootTest
class DeliveryControllerTest {

    private lateinit var deliveryController: DeliveryController
    private lateinit var deliveryService: DeliveryService

    @BeforeEach
    fun setUp() {
        deliveryService = Mockito.mock(DeliveryService::class.java)
        deliveryController = DeliveryController(deliveryService)
    }

    @Test
    @DisplayName("Should return all deliveries")
    fun shouldReturnAllDeliveries() {
        val deliveries = listOf(
            Delivery(UUID.randomUUID(), "vehicle1", LocalDateTime.now(), null, "IN_PROGRESS"),
            Delivery(UUID.randomUUID(), "vehicle2", LocalDateTime.now(), null, "DELIVERED")
        )
        `when`(deliveryService.getAllDeliveries()).thenReturn(deliveries)

        val result = deliveryController.getDeliveries()

        assertEquals(deliveries, result)
    }

    @Test
    @DisplayName("Should return business summary")
    fun shouldReturnBusinessSummary() {
        val summary = mapOf("deliveries" to 3, "averageMinutesBetweenDeliveryStart" to 240)
        `when`(deliveryService.getSummary()).thenReturn(summary)

        val result = deliveryController.getBusinessSummary()

        assertEquals(ResponseEntity(summary, HttpStatus.OK), result)
    }

    @Test
    @DisplayName("Should add delivery and return created status")
    fun shouldAddDeliveryAndReturnCreatedStatus() {
        val delivery = Delivery(UUID.randomUUID(), "vehicle1", LocalDateTime.now(), null, "IN_PROGRESS")
        `when`(deliveryService.addDelivery(delivery)).thenReturn(delivery)

        val result = deliveryController.addDelivery(delivery)

        assertEquals(ResponseEntity(delivery, HttpStatus.CREATED), result)
    }

    @Test
    @DisplayName("Should update delivery and return ok status")
    fun shouldUpdateDeliveryAndReturnOkStatus() {
        val id = UUID.randomUUID()
        val userData = mapOf("finishedAt" to "2022-01-01T12:00:00", "status" to "DELIVERED")
        val delivery = userData["status"]?.let {
            Delivery(id, "vehicle1", LocalDateTime.now(), LocalDateTime.parse(userData["finishedAt"]),
                it
            )
        }
        delivery?.let {
            `when`(deliveryService.findById(id)).thenReturn(Optional.of(it))
            `when`(deliveryService.save(it)).thenReturn(it)
        }

        val result = deliveryController.updateDelivery(id, userData)

        assertEquals(ResponseEntity(delivery, HttpStatus.OK), result)
    }

    @Test
    @DisplayName("Should update bulk deliveries and return ok status")
    fun shouldUpdateBulkDeliveriesAndReturnOkStatus() {
        val bulkDeliveries = listOf(
            mapOf("id" to "3fa85f64-5717-4562-b3fc-2c963f66afa6", "finishedAt" to "2022-01-01T12:00:00", "status" to "DELIVERED"),
            mapOf("id" to "3fa85f64-5717-4562-b3fc-2c963f66afa7", "finishedAt" to "2022-01-01T13:00:00", "status" to "IN_PROGRESS")
        )
        val deliveries = bulkDeliveries.mapNotNull {
            it["status"]?.let { it1 ->
                Delivery(
                    UUID.fromString(it["id"]),
                    "vehicle1",
                    LocalDateTime.now(),
                    LocalDateTime.parse(it["finishedAt"]),
                    it1
                )
            }
        }.toMutableList()
        `when`(deliveryService.saveAll(bulkDeliveries)).thenReturn(deliveries)

        val result = deliveryController.updateBulkDelivery(bulkDeliveries)

        assertEquals(ResponseEntity(deliveries, HttpStatus.OK), result)
    }
}