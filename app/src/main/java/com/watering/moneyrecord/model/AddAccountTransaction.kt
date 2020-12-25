package com.watering.moneyrecord.model

import com.watering.moneyrecord.entities.Account

class AddAccountTransaction(id : Int, institute : String, description : String, group: Int) : Transaction {
    val account = Account()
    val id = id

    override fun execute() {
        account.id = id

    }
}