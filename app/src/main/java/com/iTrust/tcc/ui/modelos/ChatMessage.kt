package com.iTrust.tcc.ui.modelos

class ChatMessage(val id: String, val text: String, val fromId:String, val toId:String,
                  val dataCompleta: String, val hora: String){
    constructor(): this("","","","","","")
}