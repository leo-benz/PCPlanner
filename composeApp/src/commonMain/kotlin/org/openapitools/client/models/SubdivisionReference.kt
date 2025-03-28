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


import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

/**
 * Representation of a subdivision reference
 *
 * @param code Subdivision code
 * @param shortName Short name for display
 */
@Serializable

data class SubdivisionReference (

    /* Subdivision code */
    @SerialName(value = "code") @Required val code: kotlin.String,

    /* Short name for display */
    @SerialName(value = "shortName") @Required val shortName: kotlin.String

) {


}

