package com.ahold.ctp.sandbox.service

import com.ahold.ctp.sandbox.entity.Delivery
import com.ahold.ctp.sandbox.repository.DeliveryRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import java.util.*

@SpringBootTest
class DeliveryServiceTest {

    private lateinit var deliveryService: DeliveryService
    private lateinit var deliveryRepository: DeliveryRepository

    @BeforeEach
    fun setup() {
        deliveryRepository = mock(DeliveryRepository::class.java)
        deliveryService = DeliveryService(deliveryRepository)
    }

    @Test
    @DisplayName("Should save delivery when all fields are valid")
    fun shouldSaveDeliveryWhenAllFieldsAreValid() {
        val delivery = Delivery(UUID.randomUUID(), "vehicle1", LocalDateTime.now(), null, "IN_PROGRESS")
        `when`(deliveryRepository.save(delivery)).thenReturn(delivery)

        val result = deliveryService.save(delivery)

        assertEquals(delivery, result)
    }

    @Test
    @DisplayName("Should throw exception when vehicleId is blank")
    fun shouldThrowExceptionWhenVehicleIdIsBlank() {
        val delivery = Delivery(UUID.randomUUID(), "", LocalDateTime.now(), null, "IN_PROGRESS")

        assertThrows(IllegalArgumentException::class.java) {
            deliveryService.save(delivery)
        }
    }

    @Test
    @DisplayName("Should throw exception when status is blank")
    fun shouldThrowExceptionWhenStatusIsBlank() {
        val delivery = Delivery(UUID.randomUUID(), "vehicle1", LocalDateTime.now(), null, "")

        assertThrows(IllegalArgumentException::class.java) {
            deliveryService.save(delivery)
        }
    }

    @Test
    @DisplayName("Should return summary of yesterday's deliveries")
    fun shouldReturnSummaryOfYesterdaysDeliveries() {
        val yesterday = LocalDateTime.now().minusDays(1)
        val startOfYesterday = yesterday.withHour(0).withMinute(0).withSecond(0).withNano(0)
        val endOfYesterday = yesterday.withHour(23).withMinute(59).withSecond(59).withNano(999999999)

        val deliveries = listOf(
            Delivery(UUID.randomUUID(), "vehicle1", startOfYesterday.plusHours(1), null, "IN_PROGRESS"),
            Delivery(UUID.randomUUID(), "vehicle1", startOfYesterday.plusHours(3), null, "IN_PROGRESS"),
            Delivery(UUID.randomUUID(), "vehicle1", startOfYesterday.plusHours(9), null, "IN_PROGRESS")
        )

        `when`(deliveryRepository.findAllByStartedAtBetween(startOfYesterday, endOfYesterday)).thenReturn(deliveries)

        val summary = deliveryService.getSummary()

        assertEquals(3, summary["deliveries"])
        assertEquals(240L, summary["averageMinutesBetweenDeliveryStart"])
    }
}