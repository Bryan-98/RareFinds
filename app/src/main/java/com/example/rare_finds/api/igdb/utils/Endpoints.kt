package edu.practice.utils.shared.com.example.rare_finds.api.igdb.utils

/**
 * The Public Endpoint Enum class holds all of the accessible IGDB API Endpoints
 */
enum class Endpoints : Endpoint {
    AGE_RATINGS, AGE_RATING_CONTENT_DESCRIPTIONS, ALTERNATIVE_NAMES, ARTWORKS, CHARACTERS, CHARACTER_MUG_SHOTS,
    COLLECTIONS, COMPANIES, COMPANY_WEBSITES, COMPANY_LOGOS, COVERS, EXTERNAL_GAMES, FRANCHISES, GAMES, GAME_ENGINES,
    GAME_ENGINE_LOGOS, GAME_MODES, GAME_VERSIONS, GAME_VERSION_FEATURES, GAME_VERSION_FEATURE_VALUES, GAME_VIDEOS,
    GENRES, INVOLVED_COMPANIES, KEYWORDS, MULTIPLAYER_MODES, PLATFORMS, PLATFORM_LOGOS, PLATFORM_VERSIONS,
    PLATFORM_VERSION_COMPANIES, PLATFORM_VERSION_RELEASE_DATES, PLATFORM_WEBSITES, PLAYER_PERSPECTIVES,
    PLATFORM_FAMILIES, RELEASE_DATES, SCREENSHOTS, SEARCH, THEMES, WEBSITES;

    override fun url(): String {
        return "/${this.name.lowercase()}"
    }

}