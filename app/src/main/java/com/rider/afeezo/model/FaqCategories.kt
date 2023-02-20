package com.rider.afeezo.model

class FaqCategories {
    var status: String? = null
    var records: Records? = null

    inner class Records {
        var categories: ArrayList<Records.Categories>? = null
        var paging: Paging? = null

        inner class Categories {
            var id: String? = null
            var name: String? = null
        }
    }
}
