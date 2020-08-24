package com.example.lego.xml.exceptions

import java.lang.Exception

class PartNotFound(message: String): Exception(message) {
    companion object {
        fun createMessage(itemId: Int?, colorId: Int?): String {
            return "Item with itemID = $itemId and colorID = $colorId was not found"
        }
    }
}