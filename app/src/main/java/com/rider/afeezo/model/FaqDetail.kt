package com.rider.afeezo.model

class FaqDetail {
    var status: String? = null
    var records: Records? = null

    inner class Records {
        var faqs: ArrayList<Faqs>? = null
        var paging: Paging? = null

        inner class Faqs {
            var id: String? = null
            var isExpand = false
            var title: String? = null
            var answer: String? = null
        }
    }
}