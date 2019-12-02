package com.iTrust.tcc.ui.modelos

class Comentario(val id: String, val text: String, val nome:String, val fromId: String, val fromIdFoto: String,
                  val fromIdNome: String, val dataCompleta: String){
    constructor(): this("","","","","","","")
}