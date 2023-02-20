package com.rider.afeezo.model

class RideCancel {
    var status: String? = null
    var reasons: ArrayList<Reasons>? = null
    inner class Reasons {
        var rcreason_id: String? = null
        var rcreason_title: String? = null
        var rcreason_display_comment_box: String? = null
        var rcreason_active: String? = null
    }
}

