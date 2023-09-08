package com.plcoding.stockmarketapp.data.repo

import com.plcoding.stockmarketapp.data.local.StockDatabase
import com.plcoding.stockmarketapp.data.mappers.toCompanyListingModel
import com.plcoding.stockmarketapp.data.remote.dto.StockApi
import com.plcoding.stockmarketapp.domain.model.CompanyListingModel
import com.plcoding.stockmarketapp.domain.repo.StockRepository
import com.plcoding.stockmarketapp.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    private val api : StockApi,
    private val db : StockDatabase
) : StockRepository {

    private val dao = db.dao

    override suspend fun getCompanyListings(
        fetchFromRemote: Boolean,
        query: String
    ): Flow<Resource<List<CompanyListingModel>>> {
        return flow {
            emit(Resource.Loading(true))
            val localListings = dao.searchCompanyListings(query = query)
            emit(Resource.Success(
                data = localListings.map { it.toCompanyListingModel() }
            ))

            val isDbEmpty = localListings.isEmpty() && query.isBlank()
            val shouldLoadFromCache = !isDbEmpty && !fetchFromRemote

            if(shouldLoadFromCache) {
                emit(Resource.Loading(false))
                return@flow
            }

            val remoteListings = try{

                val response = api.getListings()
                response.byteStream()

            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
            } catch (e : HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
            }
        }
    }

}