package com.ahold.ctp.sandbox.database

import com.ahold.ctp.sandbox.entity.Delivery
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface DeliveryRepository : JpaRepository<Delivery, UUID>
