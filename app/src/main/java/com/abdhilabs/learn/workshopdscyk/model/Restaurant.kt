package com.abdhilabs.learn.workshopdscyk.model

data class Restaurant(
    var name: String? = null,
    var city: String? = null,
    var category: String? = null,
    var photo: String? = null,
    var price: Int? = null,
    var numRating: Int? = null,
    var avgRating: Double? = null
) {
    companion object {
        const val FIELD_CITY = "city"
        const val FIELD_CATEGORY = "category"
        const val FIELD_PRICE = "price"
        const val FIELD_POPULARITY = "numRatings"
        const val FIELD_AVG_RATING = "avgRating"
    }
}