package com.turbopremios.winners.repository

import com.turbopremios.winners.entity.Winner
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WinnerRepository : JpaRepository<Winner, String> {
    fun findAllByOrderByCreatedAtDesc(pageable: Pageable): List<Winner>
}
