package com.profmori.foursoulsstatistics.database

import com.profmori.foursoulsstatistics.R

class CharacterList {
// A class to store all the current class data and update necessary characters

    companion object {
        val charList: Array<CharEntity> = arrayOf(
            // Base Characters
            CharEntity("blue baby", R.drawable.blue_baby, R.drawable.blue_baby_alt, "base"),
            CharEntity("cain", R.drawable.cain, null, "base"),
            CharEntity("eden", R.drawable.eden, null, "base"),
            CharEntity("eve", R.drawable.eve, null, "base"),
            CharEntity(
                "the forgotten",
                R.drawable.the_forgotten,
                R.drawable.the_forgotten_alt,
                "base"
            ),
            CharEntity("isaac", R.drawable.isaac, R.drawable.isaac_alt, "base"),
            CharEntity("judas", R.drawable.judas, R.drawable.judas_alt, "base"),
            CharEntity("lazarus", R.drawable.lazarus, null, "base"),
            CharEntity("lilith", R.drawable.lilith, R.drawable.lilith_alt, "base"),
            CharEntity("maggy", R.drawable.maggy, null, "base"),
            CharEntity("samson", R.drawable.samson, null, "base"),

            // Gold Box Characters
            CharEntity("apollyon", R.drawable.apollyon, null, "gold"),
            CharEntity("azazel", R.drawable.azazel, R.drawable.azazel_alt, "gold"),
            CharEntity("the keeper", R.drawable.the_keeper, R.drawable.the_keeper_alt, "gold"),
            CharEntity("the lost", R.drawable.the_lost, R.drawable.the_lost_alt, "gold"),

            // FS+ Characters
            CharEntity("bum-bo", R.drawable.bum_bo, null, "plus"),
            CharEntity("dark judas", R.drawable.dark_judas, null, "plus"),
            CharEntity("guppy", R.drawable.guppy, R.drawable.guppy_alt, "plus"),
            CharEntity("whore of babylon", R.drawable.whore_of_babylon, null, "plus"),

            // Requiem Characters

            CharEntity("bethany", R.drawable.bethany, null, "requiem"),
            CharEntity("jacob & esau", R.drawable.jacob_and_esau, null, "requiem"),
            CharEntity("flash isaac", R.drawable.flash_isaac, null, "requiem"),

            // Tainted Characters
            CharEntity("the baleful", R.drawable.the_baleful, null, "requiem"),
            CharEntity("the benighted", R.drawable.the_benighted, null, "requiem"),
            CharEntity("the broken", R.drawable.the_broken, null, "requiem"),
            CharEntity(
                "the capricious",
                R.drawable.the_capricious,
                R.drawable.the_capricious_alt,
                "requiem"
            ),
            CharEntity("the curdled", R.drawable.the_curdled, null, "requiem"),
            CharEntity("the dauntless", R.drawable.the_dauntless, null, "requiem"),
            CharEntity("the deceiver", R.drawable.the_deceiver, null, "requiem"),
            CharEntity("the deserter", R.drawable.the_deserter, null, "requiem"),
            CharEntity("the empty", R.drawable.the_empty, null, "requiem"),
            CharEntity("the enigma", R.drawable.the_enigma, R.drawable.the_enigma_back, "requiem"),
            CharEntity("the fettered", R.drawable.the_fettered, null, "requiem"),
            CharEntity("the harlot", R.drawable.the_harlot, R.drawable.the_harlot_alt, "requiem"),
            CharEntity("the hoarder", R.drawable.the_hoarder, null, "requiem"),
            CharEntity("the miser", R.drawable.the_miser, null, "requiem"),
            CharEntity("the savage", R.drawable.the_savage, R.drawable.the_savage_alt, "requiem"),
            CharEntity("the soiled", R.drawable.the_soiled, null, "requiem"),
            CharEntity("the zealot", R.drawable.the_zealot, null, "requiem"),

            // Warp Zone Characters
            CharEntity("abe", R.drawable.abe, null, "warp"),
            CharEntity("ash", R.drawable.ash, null, "warp"),
            CharEntity("baba", R.drawable.baba, null, "warp"),
            CharEntity("blind johnny", R.drawable.blind_johnny, null, "warp"),
            CharEntity("blue archer", R.drawable.blue_archer, null, "warp"),
            CharEntity("boyfriend", R.drawable.boyfriend, null, "warp"),
            CharEntity("bum-bo the weird", R.drawable.bumbo_the_weird, null, "warp"),
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
            CharEntity("the knight", R.drawable.the_knight, null, "warp"),
            CharEntity("the silent", R.drawable.the_silent, null, "warp"),
            CharEntity("yung venuz", R.drawable.yung_venuz, null, "warp"),

            //Tapeworm Promo Character
            CharEntity("tapeworm", R.drawable.tapeworm, null, "tapeworm")
        )
    }
}