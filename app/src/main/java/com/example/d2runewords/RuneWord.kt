package com.example.d2runewords

class RuneWord(
    var chnName: String,
    var engName: String,
    var equip: Array<String>,
    var slotNum: Int,
    var runes: IntArray,
    var level: Int,
    var effect: String,
    var ver : String
) {
    constructor() : this("","",
        arrayOf(),0, intArrayOf(),0,"", "") {}
}