package com.profmori.foursoulsstatistics.database

import com.profmori.foursoulsstatistics.R

class CharacterList {
// A class to store all the current class data and update necessary characters

    companion object{
        val charList: Array<CharEntity> = arrayOf(
            // Base Characters
            CharEntity("blue baby", R.drawable.blue_baby, R.drawable.blue_baby_alt, "base"),
            CharEntity("cain", R.drawable.cain, null, "base"),
            CharEntity("eden", R.drawable.eden, null, "base"),
            CharEntity("eve", R.drawable.eve, null, "base"),
            CharEntity("the forgotten", R.drawable.forgotten, R.drawable.forgotten_alt, "base"),
            CharEntity("isaac", R.drawable.isaac, R.drawable.isaac_alt, "base"),
            CharEntity("judas", R.drawable.judas, R.drawable.judas_alt, "base"),
            CharEntity("lazarus", R.drawable.lazarus, null, "base"),
            CharEntity("lilith", R.drawable.lilith, R.drawable.lilith_alt, "base"),
            CharEntity("maggy", R.drawable.maggy, null, "base"),
            CharEntity("samson", R.drawable.samson, null, "base"),

            // Gold Box Characters
            CharEntity("apollyon", R.drawable.apollyon, null, "gold"),
            CharEntity("azazel", R.drawable.azazel, R.drawable.azazel_alt, "gold"),
            CharEntity("the keeper", R.drawable.keeper, R.drawable.keeper_alt, "gold"),
            CharEntity("the lost", R.drawable.lost, R.drawable.lost_alt, "gold"),

            // FS+ Characters
            CharEntity("bum-bo", R.drawable.bumbo, null, "plus"),
            CharEntity("dark judas", R.drawable.dark_judas, null, "plus"),
            CharEntity("guppy", R.drawable.guppy, R.drawable.guppy_alt, "plus"),
            CharEntity("whore of babylon", R.drawable.whore, null, "plus"),

            // Requiem Characters

            CharEntity("bethany", R.drawable.bethany, null, "requiem"),
            CharEntity("jacob & esau", R.drawable.jacob_esau, null, "requiem"),

            // Tainted Characters
            CharEntity("the baleful", R.drawable.baleful, null, "requiem"),
            CharEntity("the benighted", R.drawable.benighted, null, "requiem"),
            CharEntity("the broken", R.drawable.broken, R.drawable.broken_alt, "requiem"),
            CharEntity("the capricious", R.drawable.capricious, R.drawable.capricious_alt, "requiem"),
            CharEntity("the curdled", R.drawable.curdled, null, "requiem"),
            CharEntity("the dauntless", R.drawable.dauntless, null, "requiem"),
            CharEntity("the deceiver", R.drawable.deceiver, null, "requiem"),
            CharEntity("the deserter", R.drawable.deserter, null, "requiem"),
            CharEntity("the empty", R.drawable.empty, null, "requiem"),
            CharEntity("the enigma", R.drawable.enigma, R.drawable.enigma2, "requiem"),
            CharEntity("the fettered", R.drawable.fettered, null, "requiem"),
            CharEntity("the harlot", R.drawable.harlot, R.drawable.harlot_alt, "requiem"),
            CharEntity("the hoarder", R.drawable.hoarder, null, "requiem"),
            CharEntity("the miser", R.drawable.miser, null, "requiem"),
            CharEntity("the savage", R.drawable.savage, R.drawable.savage_alt, "requiem"),
            CharEntity("the soiled", R.drawable.soiled, null, "requiem"),
            CharEntity("the zealot", R.drawable.zealot, null, "requiem"),

            // Warp Zone Characters
            CharEntity("abe", R.drawable.abe, null, "warp"),
            CharEntity("ash", R.drawable.ash, null, "warp"),
            CharEntity("baba", R.drawable.baba, null, "warp"),
            CharEntity("blind johnny", R.drawable.blind_johnny, null, "warp"),
            CharEntity("blue archer", R.drawable.blue_archer, null, "warp"),
            CharEntity("boyfriend", R.drawable.boyfriend, null, "warp"),
            CharEntity("bum-bo the weird", R.drawable.bumbo_weird, null, "warp"),
            CharEntity("captain viridian", R.drawable.captain_viridian, null, "warp"),
            CharEntity("crewmate", R.drawable.crewmate, null, "warp"),
            CharEntity("edmund", R.drawable.edmund, null, "warp"),
            CharEntity("guy spelunky", R.drawable.guy_spelunky, null, "warp"),
            CharEntity("johnny", R.drawable.johnny, null, "warp"),
            CharEntity("pink knight", R.drawable.pink_knight, null, "warp"),
            CharEntity("psycho goreman", R.drawable.psycho_goreman, null, "warp"),
            CharEntity("quote", R.drawable.quote, null, "warp"),
            CharEntity("salad fingers", R.drawable.salad_fingers, null, "warp"),
            CharEntity("steven", R.drawable.steven, null, "warp"),
            CharEntity("the knight", R.drawable.knight, null, "warp"),
            CharEntity("the silent", R.drawable.silent, null, "warp"),
            CharEntity("yung venuz", R.drawable.yung_venuz, null, "warp"),

            //Tapeworm Promo Character
            CharEntity("tapeworm",R.drawable.tapeworm, null, "promo")
        )
    }
}