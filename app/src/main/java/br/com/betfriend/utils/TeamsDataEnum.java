package br.com.betfriend.utils;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import br.com.betfriend.R;

public enum TeamsDataEnum {

    CRUZEIRO("Cruzeiro", "CRU", R.drawable.cruzeiro_60x60, "Cruzeiro"),

    ATLETICO_MINEIRO("Atletico Mineiro", "CAM", R.drawable.atletico_mg_60x60, "Atlético MG"),

    CORINTHIANS("Corinthians", "COR", R.drawable.corinthians_60x60, "Corinthians"),

    FLAMENGO("Flamengo", "FLA", R.drawable.flamengo_60x60, "Flamengo"),

    FLUMINENSE("Fluminense", "FLU", R.drawable.fluminense_60x60, "Fluminense"),

    AMERICA_MINEIRO("America MG", "AME", R.drawable.america_mg_60x60, "América MG"),

    SANTA_CRUZ("Santa Cruz", "STA", R.drawable.santa_cruz_60x60, "Santa Cruz"),

    SPORT("Sport Recife", "SPO", R.drawable.sport_60x60, "Sport"),

    SAO_PAULO("Sao Paulo", "SAO", R.drawable.sao_paulo_60x60, "São Paulo"),

    BOTAFOGO("Botafogo RJ", "BOT", R.drawable.botafogo_60x60, "Botafogo"),

    SANTOS("Santos", "SAN", R.drawable.santos_60x60, "Santos"),

    PALMEIRAS("Palmeiras", "PAL", R.drawable.palmeiras_60x60, "Palmeiras"),

    FIGUEIRENSE("Figueirense", "FIG", R.drawable.figueirense_60x60, "Figueirense"),

    CHAPECOENSE("Chapecoense", "CHA", R.drawable.chapecoense_60x60, "Chapecoense"),

    GREMIO("Gremio", "GRE", R.drawable.gremio_60x60, "Grêmio"),

    INTERNACIONAL("Internacional", "INT", R.drawable.internacional_60x60, "Internacional"),

    ATLETICO_PARANAENSE("Atletico Paranaense", "CAP", R.drawable.atletico_pr_60x60, "Atlético PR"),

    CORITIBA("Coritiba", "CFC", R.drawable.coritiba_60x60, "Coritiba"),

    PONTE_PRETA("Ponte Preta", "PON", R.drawable.ponte_preta_60x60, "Ponte Preta"),

    VITORIA("Vitoria", "VIT", R.drawable.vitoria_60x60, "Vitória"),

    ARSENAL("Arsenal", "ARS", R.drawable.arsenal, "Arsenal"),

    BOURNEMOUTH("Bournemouth", "BOU", R.drawable.bournemouth, "Bournemouth"),

    BURNLEY("Burnley", "BUR", R.drawable.burnley, "Burnley"),

    CHELSEA("Chelsea", "CHE", R.drawable.chelsea, "Chelsea"),

    CRYSTAL_PALACE("Crystal Palace", "CRY", R.drawable.crystal_palace, "Crystal Palace"),

    EVERTON("Vitoria", "EVE", R.drawable.everton, "Everton"),

    HULL_CITY("Hull City", "HUL", R.drawable.hull_city, "Hull City"),

    LEICESTER("Leicester City", "LEI", R.drawable.leicester, "Leicester"),

    LIVERPOOL("Vitoria", "LIV", R.drawable.liverpool, "Liverpool"),

    MAN_CITY("Manchester City", "MCI", R.drawable.manchester_city, "Man. City"),

    MAN_UTD("Manchester United", "MUN", R.drawable.manchester_united, "Man United"),

    MIDDLESBROUGH("Middlesbrough FC", "MID", R.drawable.middlesbrough, "Middlesbrough"),

    SOUTHAMPTON("Vitoria", "SOU", R.drawable.southampton, "Southampton"),

    STOKE_CITY("Vitoria", "STK", R.drawable.stoke_city, "Stoke City"),

    SUNDERLAND("Vitoria", "SUN", R.drawable.sunderland, "Sunderland"),

    SWANSEA("Swansea City", "SWA", R.drawable.swansea, "Swansea"),

    TOTTENHAM("Vitoria", "TOT", R.drawable.tottenham, "Tottenham"),

    WATFORD("Vitoria", "WAT", R.drawable.watford, "Watford"),

    WEST_BROMWICH("West Bromwich Albion", "WBA", R.drawable.west_brom, "West Bromwich"),

    WEST_HAM("Vitoria", "WHU", R.drawable.west_ham, "West Ham");

    private String description;

    private String label;

    private int logo;

    private String correctName;

    private static final Map<String, TeamsDataEnum> lookup = new HashMap<String, TeamsDataEnum>();

    static {
        for (TeamsDataEnum s : EnumSet.allOf(TeamsDataEnum.class)) {
            lookup.put(s.description(), s);
        }
    }

    private TeamsDataEnum(String description, String label, int logo, String correctName) {
        this.description = description;
        this.label = label;
        this.logo = logo;
        this.correctName = correctName;
    }

    public String description() {
        return this.description;
    }

    public String label() {
        return this.label;
    }

    public int logo() {
        return this.logo;
    }

    public String correctName() {
        return this.correctName;
    }

    public static TeamsDataEnum get(final String description) {
        return lookup.get(description);
    }

}
