package com.rider.afeezo.model

data class GetConfiguration(
    val `data`: Data,
    val msg: String,
    val status: String
) {
    data class Data(
        val address: String,
        val currency: Currency,
        val date_format: String,
        val driver_threshold_balance: String,
        val emergency_contact_number: String,
        val geo_fence_limit: String,
        val google_keys: GoogleKeys,
        val layout_direction: String,
        val live_payment_mode: String,
        val max_invites: String,
        val min_recharge_amount: String,
        val min_withdraw_amount: String,
        val owner_email: String,
        val phone: String,
        val referral: Referral,
        val site_name: String,
        val subscription_module_enabled: String,
        val scheduled_ride_module_enabled: String,
        val wallet_module_enabled: Int,
        val reward_module_enabled: Int
    ) {
        data class Currency(
            val code: String,
            val id: Int,
            val symbol_left: String,
            val symbol_right: String
        )

        data class GoogleKeys(
            val api_key: String
        )

        data class Referral(
            val enabled: String,
            val text: String
        )
    }
}