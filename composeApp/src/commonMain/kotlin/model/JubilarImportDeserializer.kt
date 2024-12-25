package model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.DateTimeFormatBuilder
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.serialization.Contextual
import kotlinx.serialization.json.*
import java.time.format.DateTimeFormatter

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import repository.JubilareRepository
import kotlin.uuid.Uuid

@Serializable
data class JubilareWrapper(
    @Serializable(with = JubilareListDeserializer::class) val jubilare: List<Jubilar>
)

class JubilareListDeserializer : KSerializer<List<Jubilar>> {
    private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("Jubilare") {
            element<List<JubilarEntity>>("jubilare")
        }

    override fun deserialize(decoder: Decoder): List<Jubilar> {
        require(decoder is JsonDecoder) // This deserializer is JSON-specific
        val jubilareArray = decoder.decodeJsonElement().jsonArray

        return jubilareArray.map { element ->
            val obj = element.jsonObject
            JubilarEntity(
                lastName = obj["lastname"]?.jsonPrimitive?.content ?: "",
                firstName = obj["firstname"]?.jsonPrimitive?.content ?: "",
                address = obj["address"]?.jsonPrimitive?.content ?: "",
                originalJubilarDate = LocalDate.parse(
                    obj["originalJubilarDate"]?.jsonPrimitive?.content ?: "",
                    LocalDate.Format {
                        dayOfMonth(Padding.ZERO);char('.');monthNumber(Padding.ZERO);char('.');year()
                    }
                ),
                gender = obj["gender"]?.jsonPrimitive?.content?.let {
                    when (it) {
                        "w" -> Gender.FEMALE
                        "m" -> Gender.MALE
                        else -> Gender.OTHER
                    }
                },
                optOut = false,
                comment = "",
                type = obj["type"]?.jsonPrimitive?.content?.let {
                    when (it) {
                        "BIRTHDAY" -> JubilarType.BIRTHDAY
                        "ANNIVERSARY" -> JubilarType.ANNIVERSARY
                        else -> JubilarType.BIRTHDAY
                    }
                } ?: JubilarType.BIRTHDAY
            ).toDomain()
        }
    }

    override fun serialize(encoder: Encoder, value: List<Jubilar>) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(
                descriptor,
                0,
                ListSerializer(JubilarEntity.serializer()),
                value.map { it.toEntity() }
            )
        }
    }
}