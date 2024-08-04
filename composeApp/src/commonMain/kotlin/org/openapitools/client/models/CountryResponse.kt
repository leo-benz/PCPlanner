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

package org.openapitools.client.models

import org.openapitools.client.models.LocalizedText

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

/**
 * Representation of a country as defined in ISO 3166-1
 *
 * @param isoCode ISO 3166-1 country code
 * @param name Localized country names
 * @param officialLanguages Official ISO-639-1 language codes
 */
@Serializable

data class CountryResponse (

    /* ISO 3166-1 country code */
    @SerialName(value = "isoCode") @Required val isoCode: kotlin.String,

    /* Localized country names */
    @SerialName(value = "name") @Required val name: kotlin.collections.List<LocalizedText>,

    /* Official ISO-639-1 language codes */
    @SerialName(value = "officialLanguages") @Required val officialLanguages: kotlin.collections.List<kotlin.String>

) {


}

