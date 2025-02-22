package com.protect.jikigo


enum class NewsEnvironmentType {
    ALL,
    AIR,
    WATER,
    ECOSYSTEM,
    POLICY;

    fun getTodayNewsTabTitle(): String = when(this) {
        ALL -> "전체"
        AIR -> "대기"
        WATER -> "물"
        ECOSYSTEM -> "생태계"
        POLICY -> "정책"
    }

    companion object {
        fun fromString(type: String): NewsEnvironmentType {
            return NewsEnvironmentType.valueOf(type.uppercase())
        }
    }
}