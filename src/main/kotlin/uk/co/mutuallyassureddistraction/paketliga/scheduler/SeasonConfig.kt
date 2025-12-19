package uk.co.mutuallyassureddistraction.paketliga.scheduler

class SeasonConfig {
    var seasons: List<Season> = mutableListOf()
}

class Season {
    var seasonName: String? = null
    var endDate: String? = null
}
