package com.mobdeve.s12.group4.mco.models

class Transaction(id : Long, amount: Double, type: String, category: Category,
                  account: Account, notes: String, createdAt: CustomDate
) {
    var id = id
        private set

    var amount = amount
        private set

    var type = type
        private set

    var category = category
        private set

    var account = account
        private set

    var notes = notes
        private set

    var createdAt = createdAt
        private set
}