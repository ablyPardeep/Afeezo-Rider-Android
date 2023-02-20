package com.rider.afeezo.model

class Paging {
    var recordCount: String? = null
    var pageCount: String? = null
    var pagesize: String? = null
    var page: String? = null

    override fun toString(): String {
        return "ClassPojo [recordCount = $recordCount, pageCount = $pageCount, pagesize = $pagesize, page = $page]"
    }
}