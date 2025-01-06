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

    fun matchEquipment(category: String): Boolean {
        val mace = arrayOf("锤类","钉头锤","铁锤")
        val bodyArmor = arrayOf(
            "近战武器") + mace + arrayOf(
            "棍棒",
            "权杖",
            "剑",
            "法杖",
            "斧头",
            "手杖",
            "爪类",
            "匕首",
            "长柄武器",
            "长矛",
        )
        val armor = bodyArmor + arrayOf("武器", "弓", "十字弓")
        val shield = arrayOf("盾牌", "圣骑士盾牌")

        if (category == "武器")
            return armor.intersect(equip.toSet()).isNotEmpty();
        else if (category == "近战武器")
            return bodyArmor.intersect(equip.toSet()).isNotEmpty();
        else if (category == "盾牌")
            return shield.intersect(equip.toSet()).isNotEmpty();
        else if (arrayOf("头盔", "盔甲", "圣骑士盾牌").contains(category))
            return equip.contains(category);
        else if (category == "锤类")
            return arrayOf("武器", "近战武器", *mace).intersect(equip.toSet()).isNotEmpty()
        else if (mace.drop(1).contains(category))
            return arrayOf("武器", "近战武器", "锤类", category).intersect(equip.toSet()).isNotEmpty()
        else if (bodyArmor.drop(1).contains(category))
            return arrayOf("武器", "近战武器", category).intersect(equip.toSet()).isNotEmpty();
        else //"弓","十字弓"
            return arrayOf("弓", "十字弓", "武器").intersect(equip.toSet()).isNotEmpty();
    }
}