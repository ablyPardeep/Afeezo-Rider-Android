package com.rider.afeezo.model

class RewardPointsArr {
    var rewardPoints: ArrayList<RewardPoints>? = null
    var paging: Paging? = null

    inner class RewardPoints {
        var id: String? = null
        var points: String? = null
        var date: String? = null
        var comments: String? = null
    }
}
