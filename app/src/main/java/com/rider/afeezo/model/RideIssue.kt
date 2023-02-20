package com.rider.afeezo.model

class RideIssue {
    var rideIssues: ArrayList<RideIssues>? = null
    var status: String? = null

    inner class RideIssues {
        var id: String? = null
        var name: String? = null
    }
}