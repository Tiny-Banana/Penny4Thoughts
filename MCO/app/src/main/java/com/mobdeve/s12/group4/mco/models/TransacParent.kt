package com.mobdeve.s12.group4.mco.models

data class TransacParent(
    val section: String,
    val transactions: MutableList<Transaction>) {
}