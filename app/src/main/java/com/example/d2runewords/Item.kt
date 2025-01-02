package com.example.d2runewords

class Item (val cnClass:String,
            val enClass:String,
            val cnName:String,
            val enName:String,
            val TwoHandDmg:Array<Int>? = null,
            val OneHandDmg:Array<Int>? = null,
            val Speed:Int? = null,
            val StrReq:Int? = null,
            val DexReq:Int? = null,
            val MaxSocket:Int? = null,
            val Class:String? = null,
            val CLevel:Int? = null,
            val QLevel:Int,
            val img:String,
            val Range:Int? = null,
            val Durability:Int? = null,
            val ThrowingDmg:Array<Int>? = null,
            val MaxStack:Int? = null,
            val Defence:Array<Int>? = null,
            val Slot:Int? = null,
            val Type:String? = null,
            val AssassinKickingDamage:Array<Int>? = null,
            val Block:Int? = null,
            val SmiteDmg:Array<Int>? = null) {

    fun getImgResStr() = "${enClass.lowercase().replace(" ","_")}_${img.substring(0,img.lastIndexOf("."))}"
}