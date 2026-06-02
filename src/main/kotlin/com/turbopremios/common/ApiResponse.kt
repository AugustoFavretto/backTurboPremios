package com.turbopremios.common

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null
) {
    companion object {
        fun <T> success(data: T, message: String? = null): ApiResponse<T> =
            ApiResponse(success = true, data = data, message = message)

        fun <T> error(message: String): ApiResponse<T> =
            ApiResponse(success = false, message = message)

        fun <T> noContent(message: String? = null): ApiResponse<T> =
            ApiResponse(success = true, data = null, message = message)
    }
}

data class PaginatedResponse<T>(
    val data: List<T>,
    val total: Long,
    val page: Int,
    val perPage: Int,
    val totalPages: Int
) {
    companion object {
        fun <T> of(data: List<T>, total: Long, page: Int, perPage: Int): PaginatedResponse<T> {
            val totalPages = if (total == 0L) 1 else ((total + perPage - 1) / perPage).toInt()
            return PaginatedResponse(data = data, total = total, page = page, perPage = perPage, totalPages = totalPages)
        }
    }
}
