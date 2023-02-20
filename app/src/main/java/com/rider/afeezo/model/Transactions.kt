package com.rider.afeezo.model

class Transactions {

    var txns: ArrayList<Txns>? = null
    var paging: Paging? = null

    override fun toString(): String {
        return "ClassPojo [txns = $txns, paging = $paging]"
    }

    inner class Txns {
        var id: String? = null
        var status: String? = null
        var debit: String? = null
        var credit: String? = null
        var date: String? = null
        var comments: String? = null

        override fun toString(): String {
            return "ClassPojo [id = $id, status = $status, debit = $debit, credit = $credit, date = $date, comments = $comments]"
        }
    }

}
