package model

import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames

val MonthNames.Companion.GERMAN_FULL: MonthNames
    get() = MonthNames(listOf(
        "Januar", "Februar", "März", "April", "Mai", "Juni",
        "Juli", "August", "September", "Oktober", "November", "Dezember"
    ))

val DayOfWeekNames.Companion.GERMAN_FULL: DayOfWeekNames
    get() = DayOfWeekNames(listOf(
        "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag"
    ))