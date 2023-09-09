package com.plcoding.stockmarketapp.presentation.company_listings

import com.plcoding.stockmarketapp.domain.model.CompanyListingModel

data class CompanyListingsState(
    val companies : List<CompanyListingModel> = emptyList(),
    val isLoading : Boolean = false,
    val isRefreshing : Boolean = false,
    val searchQuery: String = ""
)
