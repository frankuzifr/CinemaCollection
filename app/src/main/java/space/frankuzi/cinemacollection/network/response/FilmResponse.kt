package space.frankuzi.cinemacollection.network.response

import com.google.gson.annotations.SerializedName

data class FilmResponse(
    @SerializedName("kinopoiskId") val kinopoiskId: Int,
//    @SerializedName("total") val imdbId: String?,
    @SerializedName("nameRu") val nameRu: String?,
    @SerializedName("nameEn") val nameEn: String?,
    @SerializedName("nameOriginal") val nameOriginal: String?,
    @SerializedName("posterUrl") val posterUrl: String,
    @SerializedName("posterUrlPreview") val posterUrlPreview: String,
//    @SerializedName("total") val coverUrl: String?,
//    @SerializedName("total") val logoUrl: String?,
//    @SerializedName("total") val reviewsCount: Int,
//    @SerializedName("total") val ratingGoodReview: Float?,
//    @SerializedName("total") val ratingGoodReviewVoteCount: Int?,
//    @SerializedName("total") val ratingKinopoisk: Float?,
//    @SerializedName("total") val ratingKinopoiskVoteCount: Int?,
//    @SerializedName("total") val ratingImdb: Float?,
//    @SerializedName("total") val ratingImdbVoteCount: Int?,
//    @SerializedName("total") val ratingFilmCritics: Float?,
//    @SerializedName("total") val ratingFilmCriticsVoteCount: Int?,
//    @SerializedName("total") val ratingAwait: Float?,
//    @SerializedName("total") val ratingAwaitCount: Int?,
//    @SerializedName("total") val ratingRfCritics: Float?,
//    @SerializedName("total") val ratingRfCriticsVoteCount: Int?,
//    @SerializedName("total") val webUrl: String,
    @SerializedName("year") val year: Int?,
//    @SerializedName("total") val filmLength: Int?,
//    @SerializedName("total") val slogan: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("shortDescription") val shortDescription: String?,
//    editorAnnotation*	string
//    nullable: true
//    example: Фильм доступен только на языке оригинала с русскими субтитрами
//    isTicketsAvailable*	boolean
//    example: false
//    productionStatus*	string
//    nullable: true
//    example: POST_PRODUCTION
//    Enum:
//    Array [ 6 ]
    @SerializedName("type") val type: String,
//    example: FILM
//    Enum:
//    Array [ 5 ]
//    ratingMpaa*	string
//    nullable: true
//    example: r
//    ratingAgeLimits*	string
//    nullable: true
//    example: age16
//    hasImax*	boolean
//    nullable: true
//    example: false
//    has3D*	boolean
//    nullable: true
//    example: false
//    lastSync*	string
//    example: 2021-07-29T20:07:49.109817
//    countries*	[...]
//    genres*	[...]
//    startYear*	integer
//    nullable: true
//    example: 1996
//    endYear*	integer
//    nullable: true
//    example: 1996
//    serial	boolean
//    nullable: true
//    example: false
//    shortFilm	boolean
//    nullable: true
//    example: false
//    completed	boolean
)