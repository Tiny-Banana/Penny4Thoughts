package com.mobdeve.s12.group4.mco.models

class Budget(limit: Double, spent: Double, remaining: Double, month: Int, year: Int) {
    var limit = limit
        set(value) {
            field = value
        }

    var spent = spent
        set(value) {
            field = value
        }

    var remaining = remaining
        set(value) {
            field = value
        }

    var month = month
        private set

    var year = year
        private set
}