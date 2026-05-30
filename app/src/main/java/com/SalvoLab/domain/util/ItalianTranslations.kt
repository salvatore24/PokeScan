package com.SalvoLab.domain.util

/**
 * Traduzione nomi set e rarità in italiano.
 * I nomi dall'API Pokémon TCG sono in inglese.
 */
object ItalianTranslations {

    // ── Rarità ────────────────────────────────────────────────
    private val rarityMap = mapOf(
        "Common"                    to "Comune",
        "Uncommon"                  to "Non Comune",
        "Rare"                      to "Raro",
        "Rare Holo"                 to "Raro Olografico",
        "Rare Holo EX"              to "Raro Olografico EX",
        "Rare Holo GX"              to "Raro Olografico GX",
        "Rare Holo V"               to "Raro Olografico V",
        "Rare Holo VMAX"            to "Raro Olografico VMAX",
        "Rare Holo VSTAR"           to "Raro Olografico VSTAR",
        "Rare Ultra"                to "Ultra Raro",
        "Rare Secret"               to "Segreto Raro",
        "Rare Rainbow"              to "Raro Arcobaleno",
        "Rare Shiny"                to "Raro Cromatico",
        "Rare Shiny GX"             to "Raro Cromatico GX",
        "Rare Prism Star"           to "Raro Stella Prisma",
        "Amazing Rare"              to "Raro Straordinario",
        "Radiant Rare"              to "Raro Radiante",
        "Double Rare"               to "Doppio Raro",
        "Ultra Rare"                to "Ultra Raro",
        "Illustration Rare"         to "Raro Illustrazione",
        "Special Illustration Rare" to "Raro Illustrazione Speciale",
        "Hyper Rare"                to "Iper Raro",
        "ACE SPEC Rare"             to "Raro ACE SPEC",
        "Shiny Rare"                to "Raro Cromatico",
        "Shiny Ultra Rare"          to "Ultra Raro Cromatico",
        "Classic Collection"        to "Collezione Classica",
        "Promo"                     to "Promozionale",
        "Legend"                    to "Leggenda",
        "BREAK"                     to "BREAK",
        "Rare BREAK"                to "Raro BREAK",
        "Rare ACE"                  to "Raro ACE",
        "Rare Prime"                to "Raro Prime",
        "Rare LV.X"                 to "Raro LV.X",
        "V"                         to "V",
        "VMAX"                      to "VMAX",
        "VSTAR"                     to "VSTAR",
    )

    // ── Nomi set ──────────────────────────────────────────────
    private val setNameMap = mapOf(
        // Scarlatto e Violetto
        "Scarlet & Violet"                          to "Scarlatto e Violetto",
        "Scarlet & Violet—Paldea Evolved"           to "Scarlatto e Violetto—Evoluzione a Paldea",
        "Scarlet & Violet—Obsidian Flames"          to "Scarlatto e Violetto—Fiamme Ossidiane",
        "Scarlet & Violett—Paradox Rift"            to "Scarlatto e Violetto—Spaccatura Paradosso",
        "Scarlet & Violet—Paradox Rift"             to "Scarlatto e Violetto—Spaccatura Paradosso",
        "Scarlet & Violet—Paldean Fates"            to "Scarlatto e Violetto—Destini di Paldea",
        "Scarlet & Violet—Temporal Forces"          to "Scarlatto e Violetto—Forze Temporali",
        "Scarlet & Violet—Twilight Masquerade"      to "Scarlatto e Violetto—Masquerade al Crepuscolo",
        "Scarlet & Violet—Shrouded Fable"           to "Scarlatto e Violetto—Favola Avvolta",
        "Scarlet & Violet—Stellar Crown"            to "Scarlatto e Violetto—Corona Stellare",
        "Scarlet & Violet—Surging Sparks"           to "Scarlatto e Violetto—Scintille Impetuose",
        "Scarlet & Violet—Prismatic Evolutions"     to "Scarlatto e Violetto—Evoluzioni Prismatiche",
        "Scarlet & Violet—Journey Together"         to "Scarlatto e Violetto—Viaggio Insieme",

        // Spada e Scudo
        "Sword & Shield"                            to "Spada e Scudo",
        "Sword & Shield—Rebel Clash"                to "Spada e Scudo—Scontro Ribelle",
        "Sword & Shield—Darkness Ablaze"            to "Spada e Scudo—Oscurità Infuocata",
        "Sword & Shield—Vivid Voltage"              to "Spada e Scudo—Tensione Vivida",
        "Sword & Shield—Battle Styles"              to "Spada e Scudo—Stili di Battaglia",
        "Sword & Shield—Chilling Reign"             to "Spada e Scudo—Regno Glaciale",
        "Sword & Shield—Evolving Skies"             to "Spada e Scudo—Cieli in Evoluzione",
        "Sword & Shield—Fusion Strike"              to "Spada e Scudo—Fusione Offensiva",
        "Sword & Shield—Brilliant Stars"            to "Spada e Scudo—Stelle Splendenti",
        "Sword & Shield—Astral Radiance"            to "Spada e Scudo—Radiosità Astrale",
        "Sword & Shield—Pokémon GO"                 to "Spada e Scudo—Pokémon GO",
        "Sword & Shield—Lost Origin"                to "Spada e Scudo—Origine Perduta",
        "Sword & Shield—Silver Tempest"             to "Spada e Scudo—Tempesta d'Argento",
        "Sword & Shield—Crown Zenith"               to "Spada e Scudo—Zenit della Corona",
        "Champion's Path"                           to "Sentiero del Campione",
        "Shining Fates"                             to "Destini Cromatici",

        // Sole e Luna
        "Sun & Moon"                                to "Sole e Luna",
        "Sun & Moon—Guardians Rising"               to "Sole e Luna—Guardiani Ascendenti",
        "Sun & Moon—Burning Shadows"                to "Sole e Luna—Ombre Infuocate",
        "Sun & Moon—Crimson Invasion"               to "Sole e Luna—Invasione Cremisi",
        "Sun & Moon—Ultra Prism"                    to "Sole e Luna—Ultra Prisma",
        "Sun & Moon—Forbidden Light"                to "Sole e Luna—Luce Proibita",
        "Sun & Moon—Celestial Storm"                to "Sole e Luna—Tempesta Celeste",
        "Sun & Moon—Dragon Majesty"                 to "Sole e Luna—Maestà dei Draghi",
        "Sun & Moon—Lost Thunder"                   to "Sole e Luna—Tuono Perduto",
        "Sun & Moon—Team Up"                        to "Sole e Luna—Alleanza Infallibile",
        "Sun & Moon—Unbroken Bonds"                 to "Sole e Luna—Legami Indistruttibili",
        "Sun & Moon—Unified Minds"                  to "Sole e Luna—Menti Unite",
        "Sun & Moon—Cosmic Eclipse"                 to "Sole e Luna—Eclissi Cosmica",
        "Sun & Moon—Shining Legends"                to "Sole e Luna—Leggende Splendenti",
        "Sun & Moon—Hidden Fates"                   to "Sole e Luna—Destini Nascosti",

        // X e Y
        "XY"                                        to "X e Y",
        "XY—Flashfire"                              to "X e Y—Fiamme Veloci",
        "XY—Furious Fists"                          to "X e Y—Pugni Furiosi",
        "XY—Phantom Forces"                         to "X e Y—Forze Fantasma",
        "XY—Primal Clash"                           to "X e Y—Scontro Primordiale",
        "XY—Roaring Skies"                          to "X e Y—Cieli Ruggenti",
        "XY—Ancient Origins"                        to "X e Y—Origini Antiche",
        "XY—BREAKthrough"                           to "X e Y—BREAK Evolutivo",
        "XY—BREAKpoint"                             to "X e Y—BREAK Point",
        "XY—Fates Collide"                          to "X e Y—Destini Incrociati",
        "XY—Steam Siege"                            to "X e Y—Assedio Vapore",
        "XY—Evolutions"                             to "X e Y—Evoluzioni",
        "Double Crisis"                             to "Doppia Crisi",
        "Generations"                               to "Generazioni",

        // Nero e Bianco
        "Black & White"                             to "Nero e Bianco",
        "Black & White—Emerging Powers"             to "Nero e Bianco—Poteri Emergenti",
        "Black & White—Noble Victories"             to "Nero e Bianco—Nobili Vittorie",
        "Black & White—Next Destinies"              to "Nero e Bianco—Prossimi Destini",
        "Black & White—Dark Explorers"              to "Nero e Bianco—Esploratori Oscuri",
        "Black & White—Dragons Exalted"             to "Nero e Bianco—Draghi Esaltati",
        "Black & White—Boundaries Crossed"          to "Nero e Bianco—Confini Incrociati",
        "Black & White—Plasma Storm"                to "Nero e Bianco—Tempesta di Plasma",
        "Black & White—Plasma Freeze"               to "Nero e Bianco—Plasma Gelido",
        "Black & White—Plasma Blast"                to "Nero e Bianco—Esplosione Plasma",
        "Black & White—Legendary Treasures"         to "Nero e Bianco—Tesori Leggendari",

        // HeartGold SoulSilver
        "HeartGold & SoulSilver"                    to "HeartGold e SoulSilver",
        "HS—Unleashed"                              to "HS—Scatenati",
        "HS—Undaunted"                              to "HS—Intrepidi",
        "HS—Triumphant"                             to "HS—Trionfanti",
        "Call of Legends"                           to "Richiamo delle Leggende",

        // Platino e Diamond/Pearl
        "Platinum"                                  to "Platino",
        "Platinum—Rising Rivals"                    to "Platino—Rivali Ascendenti",
        "Platinum—Supreme Victors"                  to "Platino—Supremi Vincitori",
        "Platinum—Arceus"                           to "Platino—Arceus",
        "Diamond & Pearl"                           to "Diamante e Perla",
        "Diamond & Pearl—Mysterious Treasures"      to "Diamante e Perla—Tesori Misteriosi",
        "Diamond & Pearl—Secret Wonders"            to "Diamante e Perla—Meraviglie Segrete",
        "Diamond & Pearl—Great Encounters"          to "Diamante e Perla—Grandi Incontri",
        "Diamond & Pearl—Majestic Dawn"             to "Diamante e Perla—Alba Maestosa",
        "Diamond & Pearl—Legends Awakened"          to "Diamante e Perla—Leggende Risvegliate",
        "Diamond & Pearl—Stormfront"                to "Diamante e Perla—Fronte Temporalesco",

        // Set classici
        "Base Set"                                  to "Set Base",
        "Jungle"                                    to "Giungla",
        "Fossil"                                    to "Fossile",
        "Base Set 2"                                to "Set Base 2",
        "Team Rocket"                               to "Team Rocket",
        "Gym Heroes"                                to "Eroi della Palestra",
        "Gym Challenge"                             to "Sfida della Palestra",
        "Neo Genesis"                               to "Neo Genesi",
        "Neo Discovery"                             to "Neo Scoperta",
        "Neo Revelation"                            to "Neo Rivelazione",
        "Neo Destiny"                               to "Neo Destino",
        "Legendary Collection"                      to "Collezione Leggendaria",
        "Expedition Base Set"                       to "Spedizione",
        "Aquapolis"                                 to "Aquaopolis",
        "Skyridge"                                  to "Skyridge",
        "EX Ruby & Sapphire"                        to "EX Rubino e Zaffiro",
        "EX Sandstorm"                              to "EX Tempesta di Sabbia",
        "EX Dragon"                                 to "EX Dragone",
        "EX Team Magma vs Team Aqua"                to "EX Team Magma contro Team Aqua",
        "EX Hidden Legends"                         to "EX Leggende Nascoste",
        "EX FireRed & LeafGreen"                    to "EX RossoFuoco e VerdeFoglia",
        "EX Team Rocket Returns"                    to "EX Il Ritorno del Team Rocket",
        "EX Deoxys"                                 to "EX Deoxys",
        "EX Emerald"                                to "EX Smeraldo",
        "EX Unseen Forces"                          to "EX Forze Invisibili",
        "EX Delta Species"                          to "EX Specie Delta",
        "EX Legend Maker"                           to "EX Creatore di Leggende",
        "EX Holon Phantoms"                         to "EX Fantasmi di Holon",
        "EX Crystal Guardians"                      to "EX Guardiani di Cristallo",
        "EX Dragon Frontiers"                       to "EX Frontiere del Dragone",
        "EX Power Keepers"                          to "EX Custodi del Potere",

        // Promo e speciali
        "McDonald's Collection"                     to "Collezione McDonald's",
        "Detective Pikachu"                         to "Detective Pikachu",
        "Celebrations"                              to "Celebrazioni",
        "Celebrations: Classic Collection"          to "Celebrazioni: Collezione Classica",
        "SWSH Black Star Promos"                    to "Promo Black Star SpSc",
        "SM Black Star Promos"                      to "Promo Black Star SL",
        "XY Black Star Promos"                      to "Promo Black Star XY",
        "BW Black Star Promos"                      to "Promo Black Star NB",
        "HeartGold & SoulSilver Black Star Promos"  to "Promo Black Star HGSS",
        "Wizards Black Star Promos"                 to "Promo Black Star Wizards",
        "SVP Black Star Promos"                     to "Promo Black Star SV",
    )

    fun translateSetName(englishName: String): String =
        setNameMap[englishName] ?: englishName

    fun translateRarity(englishRarity: String?): String? =
        englishRarity?.let { rarityMap[it] ?: it }

    /** Traduce il nome della serie (es. "Scarlet & Violet" → "Scarlatto e Violetto") */
    fun translateSeries(englishSeries: String): String = when (englishSeries) {
        "Scarlet & Violet"  -> "Scarlatto e Violetto"
        "Sword & Shield"    -> "Spada e Scudo"
        "Sun & Moon"        -> "Sole e Luna"
        "XY"                -> "X e Y"
        "Black & White"     -> "Nero e Bianco"
        "HeartGold & SoulSilver" -> "HeartGold e SoulSilver"
        "Platinum"          -> "Platino"
        "Diamond & Pearl"   -> "Diamante e Perla"
        "EX"                -> "EX"
        "Neo"               -> "Neo"
        "Base"              -> "Base"
        "Gym"               -> "Palestra"
        "Legendary"         -> "Leggendario"
        else                -> englishSeries
    }
}

// ── Extension functions ───────────────────────────────────────

fun String.toItalianSetName()    = ItalianTranslations.translateSetName(this)
fun String.toItalianSeries()     = ItalianTranslations.translateSeries(this)
fun String?.toItalianRarity()    = ItalianTranslations.translateRarity(this)
