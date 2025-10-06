package com.example.scoreboard.data

import android.graphics.Color

object TeamRepository {

    fun getTeams(): List<Team> {
        return listOf(
            Team("Hawks", Color.parseColor("#E03A3E"), Color.WHITE),
            Team("Celtics", Color.parseColor("#007A33"), Color.WHITE),
            Team("Nets", Color.parseColor("#000000"), Color.WHITE),
            Team("Hornets", Color.parseColor("#1D1160"), Color.WHITE),
            Team("Bulls", Color.parseColor("#CE1141"), Color.WHITE),
            Team("Cavaliers", Color.parseColor("#6F263D"), Color.WHITE),
            Team("Mavericks", Color.parseColor("#00538C"), Color.WHITE),
            Team("Nuggets", Color.parseColor("#0E2240"), Color.WHITE),
            Team("Pistons", Color.parseColor("#C8102E"), Color.WHITE),
            Team("Warriors", Color.parseColor("#1D428A"), Color.YELLOW),
            Team("Rockets", Color.parseColor("#CE1141"), Color.WHITE),
            Team("Pacers", Color.parseColor("#002D62"), Color.YELLOW),
            Team("Clippers", Color.parseColor("#C8102E"), Color.WHITE),
            Team("Lakers", Color.parseColor("#552583"), Color.YELLOW),
            Team("Grizzlies", Color.parseColor("#5D76A9"), Color.YELLOW),
            Team("Heat", Color.parseColor("#98002E"), Color.WHITE),
            Team("Bucks", Color.parseColor("#00471B"), Color.WHITE),
            Team("Timberwolves", Color.parseColor("#0C2340"), Color.WHITE),
            Team("Pelicans", Color.parseColor("#0C2340"), Color.YELLOW),
            Team("Knicks", Color.parseColor("#F58426"), Color.WHITE),
            Team("Thunder", Color.parseColor("#007AC1"), Color.WHITE),
            Team("Magic", Color.parseColor("#0077C0"), Color.WHITE),
            Team("76ers", Color.parseColor("#006BB6"), Color.WHITE),
            Team("Suns", Color.parseColor("#1D1160"), Color.WHITE),
            Team("Trail Blazers", Color.parseColor("#E03A3E"), Color.WHITE),
            Team("Kings", Color.parseColor("#5A2D81"), Color.YELLOW),
            Team("Spurs", Color.parseColor("#C4CED4"), Color.YELLOW),

            Team("Raptors", Color.parseColor("#CE1141"), Color.YELLOW),
            Team("Jazz", Color.parseColor("#002B5C"), Color.WHITE),
            Team("Wizards", Color.parseColor("#002B5C"), Color.WHITE)
        )
    }

}