package com.mobdeve.s12.group4.mco.models

class Transaction(id : Long, amount: Double, type: String, category: Category,
                  account: Account, notes: String, createdAt: CustomDate
) {
    var id = id
        private set

    var amount = amount
        set(value) {
            field = value
        }

    var type = type
        set(value) {
            field = value
        }

    var category = category
        set(value) {
            field = value
        }

    var account = account
        set(value) {
            field = value
        }

    var notes = notes
        set(value) {
            field = value
        }

    var createdAt = createdAt
        set(value) {
            field = value
        }
}