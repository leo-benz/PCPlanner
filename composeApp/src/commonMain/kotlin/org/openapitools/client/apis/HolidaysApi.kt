/**
 *
 * Please note:
 * This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * Do not edit this file manually.
 *
 */

@file:Suppress(
    "ArrayInDataClass",
    "EnumEntryName",
    "RemoveRedundantQualifierName",
    "UnusedImport"
)

package org.openapitools.client.apis

import org.openapitools.client.models.HolidayByDateResponse
import org.openapitools.client.models.HolidayResponse

import org.openapitools.client.infrastructure.*
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.request.forms.formData
import io.ktor.client.engine.HttpClientEngine
import kotlinx.serialization.json.Json
import io.ktor.http.ParametersBuilder
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

open class HolidaysApi : ApiClient {

    constructor(
        baseUrl: String = ApiClient.BASE_URL,
        httpClientEngine: HttpClientEngine? = null,
        httpClientConfig: ((HttpClientConfig<*>) -> Unit)? = null,
        jsonSerializer: Json = ApiClient.JSON_DEFAULT
    ) : super(baseUrl = baseUrl, httpClientEngine = httpClientEngine, httpClientConfig = httpClientConfig, jsonBlock = jsonSerializer)

    constructor(
        baseUrl: String,
        httpClient: HttpClient
    ): super(baseUrl = baseUrl, httpClient = httpClient)

    /**
     * Returns a list of public holidays from all countries for a given date.
     * 
     * @param date Date of interest
     * @param languageIsoCode ISO-639-1 code of a language or empty (optional)
     * @return kotlin.collections.List<HolidayByDateResponse>
     */
    @Suppress("UNCHECKED_CAST")
    open suspend fun publicHolidaysByDateGet(date: kotlinx.datetime.LocalDate, languageIsoCode: kotlin.String? = null): HttpResponse<kotlin.collections.List<HolidayByDateResponse>> {

        val localVariableAuthNames = listOf<String>()

        val localVariableBody = 
            io.ktor.client.utils.EmptyContent

        val localVariableQuery = mutableMapOf<String, List<String>>()
        languageIsoCode?.apply { localVariableQuery["languageIsoCode"] = listOf("$languageIsoCode") }
        date?.apply { localVariableQuery["date"] = listOf("$date") }
        val localVariableHeaders = mutableMapOf<String, String>()

        val localVariableConfig = RequestConfig<kotlin.Any?>(
            RequestMethod.GET,
            "/PublicHolidaysByDate",
            query = localVariableQuery,
            headers = localVariableHeaders,
            requiresAuthentication = false,
        )

        return request(
            localVariableConfig,
            localVariableBody,
            localVariableAuthNames
        ).wrap<PublicHolidaysByDateGetResponse>().map { value }
    }

    @Serializable(PublicHolidaysByDateGetResponse.Companion::class)
    private class PublicHolidaysByDateGetResponse(val value: List<HolidayByDateResponse>) {
        companion object : KSerializer<PublicHolidaysByDateGetResponse> {
            private val serializer: KSerializer<List<HolidayByDateResponse>> = serializer<List<HolidayByDateResponse>>()
            override val descriptor = serializer.descriptor
            override fun serialize(encoder: Encoder, obj: PublicHolidaysByDateGetResponse) = serializer.serialize(encoder, obj.value)
            override fun deserialize(decoder: Decoder) = PublicHolidaysByDateGetResponse(serializer.deserialize(decoder))
        }
    }

    /**
     * Returns list of public holidays for a given country
     * 
     * @param countryIsoCode ISO 3166-1 code of the country
     * @param validFrom Start of the date range
     * @param validTo End of the date range
     * @param languageIsoCode ISO-639-1 code of a language or empty (optional)
     * @param subdivisionCode Code of the subdivision or empty (optional)
     * @return kotlin.collections.List<HolidayResponse>
     */
    @Suppress("UNCHECKED_CAST")
    open suspend fun publicHolidaysGet(countryIsoCode: kotlin.String, validFrom: kotlinx.datetime.LocalDate, validTo: kotlinx.datetime.LocalDate, languageIsoCode: kotlin.String? = null, subdivisionCode: kotlin.String? = null): HttpResponse<kotlin.collections.List<HolidayResponse>> {

        val localVariableAuthNames = listOf<String>()

        val localVariableBody = 
            io.ktor.client.utils.EmptyContent

        val localVariableQuery = mutableMapOf<String, List<String>>()
        countryIsoCode?.apply { localVariableQuery["countryIsoCode"] = listOf("$countryIsoCode") }
        languageIsoCode?.apply { localVariableQuery["languageIsoCode"] = listOf("$languageIsoCode") }
        validFrom?.apply { localVariableQuery["validFrom"] = listOf("$validFrom") }
        validTo?.apply { localVariableQuery["validTo"] = listOf("$validTo") }
        subdivisionCode?.apply { localVariableQuery["subdivisionCode"] = listOf("$subdivisionCode") }
        val localVariableHeaders = mutableMapOf<String, String>()

        val localVariableConfig = RequestConfig<kotlin.Any?>(
            RequestMethod.GET,
            "/PublicHolidays",
            query = localVariableQuery,
            headers = localVariableHeaders,
            requiresAuthentication = false,
        )

        return request(
            localVariableConfig,
            localVariableBody,
            localVariableAuthNames
        ).wrap<PublicHolidaysGetResponse>().map { value }
    }

    @Serializable(PublicHolidaysGetResponse.Companion::class)
    private class PublicHolidaysGetResponse(val value: List<HolidayResponse>) {
        companion object : KSerializer<PublicHolidaysGetResponse> {
            private val serializer: KSerializer<List<HolidayResponse>> = serializer<List<HolidayResponse>>()
            override val descriptor = serializer.descriptor
            override fun serialize(encoder: Encoder, obj: PublicHolidaysGetResponse) = serializer.serialize(encoder, obj.value)
            override fun deserialize(decoder: Decoder) = PublicHolidaysGetResponse(serializer.deserialize(decoder))
        }
    }

    /**
     * Returns a list of school holidays from all countries for a given date.
     * 
     * @param date Date of interest
     * @param languageIsoCode ISO-639-1 code of a language or empty (optional)
     * @return kotlin.collections.List<HolidayByDateResponse>
     */
    @Suppress("UNCHECKED_CAST")
    open suspend fun schoolHolidaysByDateGet(date: kotlinx.datetime.LocalDate, languageIsoCode: kotlin.String? = null): HttpResponse<kotlin.collections.List<HolidayByDateResponse>> {

        val localVariableAuthNames = listOf<String>()

        val localVariableBody = 
            io.ktor.client.utils.EmptyContent

        val localVariableQuery = mutableMapOf<String, List<String>>()
        languageIsoCode?.apply { localVariableQuery["languageIsoCode"] = listOf("$languageIsoCode") }
        date?.apply { localVariableQuery["date"] = listOf("$date") }
        val localVariableHeaders = mutableMapOf<String, String>()

        val localVariableConfig = RequestConfig<kotlin.Any?>(
            RequestMethod.GET,
            "/SchoolHolidaysByDate",
            query = localVariableQuery,
            headers = localVariableHeaders,
            requiresAuthentication = false,
        )

        return request(
            localVariableConfig,
            localVariableBody,
            localVariableAuthNames
        ).wrap<SchoolHolidaysByDateGetResponse>().map { value }
    }

    @Serializable(SchoolHolidaysByDateGetResponse.Companion::class)
    private class SchoolHolidaysByDateGetResponse(val value: List<HolidayByDateResponse>) {
        companion object : KSerializer<SchoolHolidaysByDateGetResponse> {
            private val serializer: KSerializer<List<HolidayByDateResponse>> = serializer<List<HolidayByDateResponse>>()
            override val descriptor = serializer.descriptor
            override fun serialize(encoder: Encoder, obj: SchoolHolidaysByDateGetResponse) = serializer.serialize(encoder, obj.value)
            override fun deserialize(decoder: Decoder) = SchoolHolidaysByDateGetResponse(serializer.deserialize(decoder))
        }
    }

    /**
     * Returns list of official school holidays for a given country
     * 
     * @param countryIsoCode ISO 3166-1 code of the country
     * @param validFrom Start of the date range
     * @param validTo End of the date range
     * @param languageIsoCode ISO-639-1 code of a language or empty (optional)
     * @param subdivisionCode Code of the subdivision or empty (optional)
     * @return kotlin.collections.List<HolidayResponse>
     */
    @Suppress("UNCHECKED_CAST")
    open suspend fun schoolHolidaysGet(countryIsoCode: kotlin.String, validFrom: kotlinx.datetime.LocalDate, validTo: kotlinx.datetime.LocalDate, languageIsoCode: kotlin.String? = null, subdivisionCode: kotlin.String? = null): HttpResponse<kotlin.collections.List<HolidayResponse>> {

        val localVariableAuthNames = listOf<String>()

        val localVariableBody = 
            io.ktor.client.utils.EmptyContent

        val localVariableQuery = mutableMapOf<String, List<String>>()
        countryIsoCode?.apply { localVariableQuery["countryIsoCode"] = listOf("$countryIsoCode") }
        languageIsoCode?.apply { localVariableQuery["languageIsoCode"] = listOf("$languageIsoCode") }
        validFrom?.apply { localVariableQuery["validFrom"] = listOf("$validFrom") }
        validTo?.apply { localVariableQuery["validTo"] = listOf("$validTo") }
        subdivisionCode?.apply { localVariableQuery["subdivisionCode"] = listOf("$subdivisionCode") }
        val localVariableHeaders = mutableMapOf<String, String>()

        val localVariableConfig = RequestConfig<kotlin.Any?>(
            RequestMethod.GET,
            "/SchoolHolidays",
            query = localVariableQuery,
            headers = localVariableHeaders,
            requiresAuthentication = false,
        )

        return request(
            localVariableConfig,
            localVariableBody,
            localVariableAuthNames
        ).wrap<SchoolHolidaysGetResponse>().map { value }
    }

    @Serializable(SchoolHolidaysGetResponse.Companion::class)
    private class SchoolHolidaysGetResponse(val value: List<HolidayResponse>) {
        companion object : KSerializer<SchoolHolidaysGetResponse> {
            private val serializer: KSerializer<List<HolidayResponse>> = serializer<List<HolidayResponse>>()
            override val descriptor = serializer.descriptor
            override fun serialize(encoder: Encoder, obj: SchoolHolidaysGetResponse) = serializer.serialize(encoder, obj.value)
            override fun deserialize(decoder: Decoder) = SchoolHolidaysGetResponse(serializer.deserialize(decoder))
        }
    }

}