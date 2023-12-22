package com.example.loseit.auth

data class Users (
    var name: String = "",
    var email: String = "",
    var password: String = "",
    var weightNow: String = "",
    var weightWant: String = "",
    var height: String = "",
    var goal: String = "",
    var date: String = "",
    var calories: String = "",
    var type: String = ""
)