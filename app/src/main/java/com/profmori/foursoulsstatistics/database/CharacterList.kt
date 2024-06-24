package com.profmori.foursoulsstatistics.database

import com.profmori.foursoulsstatistics.R

class CharacterList {
// A class to store all the current class data and update necessary characters

    companion object {
        val charList: Array<CharEntity> = arrayOf(
            // Base Characters
            CharEntity("blue baby", R.drawable.b2_blue_baby, R.drawable.aa_blue_baby, "base"),
            CharEntity("cain", R.drawable.b2_cain, null, "base"),
            CharEntity("eden", R.drawable.b2_eden, null, "base"),
            CharEntity("eve", R.drawable.b2_eve, null, "base"),
            CharEntity(
                "the forgotten",
                R.drawable.b2_the_forgotten,
                R.drawable.aa_the_forgotten,
                "base"
            ),
            CharEntity("isaac", R.drawable.b2_isaac, R.drawable.aa_isaac, "base"),
            CharEntity("judas", R.drawable.b2_judas, R.drawable.aa_judas, "base"),
            CharEntity("lazarus", R.drawable.b2_lazarus, null, "base"),
            CharEntity("lilith", R.drawable.b2_lilith, R.drawable.aa_lilith, "base"),
            CharEntity("maggy", R.drawable.b2_maggy, null, "base"),
            CharEntity("samson", R.drawable.b2_samson, null, "base"),

            // Gold Box Characters
            CharEntity("apollyon", R.drawable.g2_apollyon, null, "gold"),
            CharEntity("azazel", R.drawable.g2_azazel, R.drawable.aa_azazel, "gold"),
            CharEntity("the keeper", R.drawable.g2_the_keeper, R.drawable.aa_the_keeper, "gold"),
            CharEntity("the lost", R.drawable.g2_the_lost, R.drawable.aa_the_lost, "gold"),

            // FS+ Characters
            CharEntity("bum-bo", R.drawable.fsp2_bum_bo, null, "plus"),
            CharEntity("dark judas", R.drawable.fsp2_dark_judas, null, "plus"),
            CharEntity("guppy", R.drawable.fsp2_guppy, R.drawable.aa_guppy, "plus"),
            CharEntity("whore of babylon", R.drawable.fsp2_whore_of_babylon, null, "plus"),

            // Requiem Characters

            CharEntity("bethany", R.drawable.r_bethany, null, "requiem"),
            CharEntity("jacob & esau", R.drawable.r_jacob_and_esau, null, "requiem"),
            CharEntity("flash isaac", R.drawable.r_flash_isaac, null, "requiem"),

            // Tainted Characters
            CharEntity("the baleful", R.drawable.r_the_baleful, null, "requiem"),
            CharEntity("the benighted", R.drawable.r_the_benighted, null, "requiem"),
            CharEntity("the broken", R.drawable.r_the_broken, null, "requiem"),
            CharEntity(
                "the capricious",
                R.drawable.r_the_capricious,
                R.drawable.aa_the_capricious,
                "requiem"
            ),
            CharEntity("the curdled", R.drawable.r_the_curdled, null, "requiem"),
            CharEntity("the dauntless", R.drawable.r_the_dauntless, null, "requiem"),
            CharEntity("the deceiver", R.drawable.r_the_deceiver, null, "requiem"),
            CharEntity("the deserter", R.drawable.r_the_deserter, null, "requiem"),
            CharEntity("the empty", R.drawable.r_the_empty, null, "requiem"),
            CharEntity(
                "the enigma",
                R.drawable.r_the_enigma,
                R.drawable.r_the_enigma_back,
                "requiem"
            ),
            CharEntity("the fettered", R.drawable.r_the_fettered, null, "requiem"),
            CharEntity("the harlot", R.drawable.r_the_harlot, R.drawable.aa_the_harlot, "requiem"),
            CharEntity("the hoarder", R.drawable.r_the_hoarder, null, "requiem"),
            CharEntity("the miser", R.drawable.r_the_miser, null, "requiem"),
            CharEntity("the savage", R.drawable.r_the_savage, R.drawable.aa_the_savage, "requiem"),
            CharEntity("the soiled", R.drawable.r_the_soiled, null, "requiem"),
            CharEntity("the zealot", R.drawable.r_the_zealot, null, "requiem"),

            // Warp Zone Characters
            CharEntity("abe", R.drawable.rwz_abe, null, "warp"),
            CharEntity("ash", R.drawable.rwz_ash, null, "warp"),
            CharEntity("baba", R.drawable.rwz_baba, null, "warp"),
            CharEntity("blind johnny", R.drawable.rwz_blind_johnny, null, "warp"),
            CharEntity("blue archer", R.drawable.rwz_blue_archer, null, "warp"),
            CharEntity("boyfriend", R.drawable.rwz_boyfriend, null, "warp"),
            CharEntity("bum-bo the weird", R.drawable.rwz_bumbo_the_weird, null, "warp"),
            CharEntity("captain viridian", R.drawable.rwz_captain_viridian, null, "warp"),
            CharEntity("crewmate", R.drawable.rwz_crewmate, null, "warp"),
            CharEntity("edmund", R.drawable.rwz_edmund, null, "warp"),
            CharEntity("guy spelunky", R.drawable.rwz_guy_spelunky, null, "warp"),
            CharEntity("johnny", R.drawable.rwz_johnny, null, "warp"),
            CharEntity("pink knight", R.drawable.rwz_pink_knight, null, "warp"),
            CharEntity("psycho goreman", R.drawable.rwz_psycho_goreman, null, "warp"),
            CharEntity("quote", R.drawable.rwz_quote, null, "warp"),
            CharEntity("salad fingers", R.drawable.rwz_salad_fingers, null, "warp"),
            CharEntity("steven", R.drawable.rwz_steven, null, "warp"),
            CharEntity("the knight", R.drawable.rwz_the_knight, null, "warp"),
            CharEntity("the silent", R.drawable.rwz_the_silent, null, "warp"),
            CharEntity("yung venuz", R.drawable.rwz_yung_venuz, null, "warp"),

            //Tapeworm Promo Character
            CharEntity("tapeworm", R.drawable.tw_tapeworm, null, "tapeworm"),

            // Summer of Isaac Characters
            CharEntity("clubby the seal", R.drawable.soi_clubby_the_seal, null, "summer"),
            CharEntity("gish", R.drawable.soi_gish, null, "summer"),
            CharEntity("stacy", R.drawable.soi_stacy, null, "summer")
        )
    }
}