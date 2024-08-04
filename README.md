# io.swagger.client - Kotlin client library for OpenHolidays API v1

## Requires

* Kotlin 1.4.30
* Gradle 5.3

## Build

First, create the gradle wrapper script:

```
gradle wrapper
```

Then, run:

```
./gradlew check assemble
```

This runs all tests and packages the library.

## Features/Implementation Notes

* Supports JSON inputs/outputs, File inputs, and Form inputs.
* Supports collection formats for query parameters: csv, tsv, ssv, pipes.
* Some Kotlin and Java types are fully qualified to avoid conflicts with types defined in Swagger definitions.
* Implementation of ApiClient is intended to reduce method counts, specifically to benefit Android targets.

<a name="documentation-for-api-endpoints"></a>
## Documentation for API Endpoints

All URIs are relative to */*

Class | Method | HTTP request | Description
------------ | ------------- | ------------- | -------------
*HolidaysApi* | [**publicHolidaysByDateGet**](docs/HolidaysApi.md#publicholidaysbydateget) | **GET** /PublicHolidaysByDate | Returns a list of public holidays from all countries for a given date.
*HolidaysApi* | [**publicHolidaysGet**](docs/HolidaysApi.md#publicholidaysget) | **GET** /PublicHolidays | Returns list of public holidays for a given country
*HolidaysApi* | [**schoolHolidaysByDateGet**](docs/HolidaysApi.md#schoolholidaysbydateget) | **GET** /SchoolHolidaysByDate | Returns a list of school holidays from all countries for a given date.
*HolidaysApi* | [**schoolHolidaysGet**](docs/HolidaysApi.md#schoolholidaysget) | **GET** /SchoolHolidays | Returns list of official school holidays for a given country
*RegionalApi* | [**countriesGet**](docs/RegionalApi.md#countriesget) | **GET** /Countries | Returns a list of all supported countries
*RegionalApi* | [**languagesGet**](docs/RegionalApi.md#languagesget) | **GET** /Languages | Returns a list of all used languages
*RegionalApi* | [**subdivisionsGet**](docs/RegionalApi.md#subdivisionsget) | **GET** /Subdivisions | Returns a list of relevant subdivisions for a supported country (if any)
*StatisticsApi* | [**statisticsPublicHolidaysGet**](docs/StatisticsApi.md#statisticspublicholidaysget) | **GET** /Statistics/PublicHolidays | Returns statistical data about public holidays for a given country.
*StatisticsApi* | [**statisticsSchoolHolidaysGet**](docs/StatisticsApi.md#statisticsschoolholidaysget) | **GET** /Statistics/SchoolHolidays | Returns statistical data about school holidays for a given country

<a name="documentation-for-models"></a>
## Documentation for Models

 - [io.swagger.client.models.CountryReference](docs/CountryReference.md)
 - [io.swagger.client.models.CountryResponse](docs/CountryResponse.md)
 - [io.swagger.client.models.HolidayByDateResponse](docs/HolidayByDateResponse.md)
 - [io.swagger.client.models.HolidayQuality](docs/HolidayQuality.md)
 - [io.swagger.client.models.HolidayResponse](docs/HolidayResponse.md)
 - [io.swagger.client.models.HolidayType](docs/HolidayType.md)
 - [io.swagger.client.models.LanguageResponse](docs/LanguageResponse.md)
 - [io.swagger.client.models.LocalizedText](docs/LocalizedText.md)
 - [io.swagger.client.models.StatisticsResponse](docs/StatisticsResponse.md)
 - [io.swagger.client.models.SubdivisionReference](docs/SubdivisionReference.md)
 - [io.swagger.client.models.SubdivisionResponse](docs/SubdivisionResponse.md)

<a name="documentation-for-authorization"></a>
## Documentation for Authorization

All endpoints do not require authorization.
