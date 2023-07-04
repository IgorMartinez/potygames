package br.com.igormartinez.potygames.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class YGOProdeckAPIConsumer {

    private static final String DEFAULT_PATH = "db/ygoprodeck/";
    private static final String DEFAULT_URL = "https://db.ygoprodeck.com/api/v7/cardinfo.php";
    private static final List<String> DB_CATEGORIES = Arrays.asList(
            "Monster Effect",
            "Monster Flip Effect",
            "Monster Flip Tuner Effect",
            "Monster Normal",
            "Monster Gemini",
            "Monster Normal Tuner",
            "Monster Pendulum Effect",
            "Monster Pendulum Effect Ritual",
            "Monster Pendulum Flip Effect",
            "Monster Pendulum Normal",
            "Monster Pendulum Tuner Effect",
            "Monster Ritual Effect",
            "Monster Ritual",
            "Monster Spirit",
            "Monster Toon",
            "Monster Tuner",
            "Monster Union Effect",
            "Monster Fusion",
            "Monster Link",
            "Monster Pendulum Effect Fusion",
            "Monster Synchro",
            "Monster Synchro Pendulum Effect",
            "Monster Synchro Tuner",
            "Monster XYZ",
            "Monster XYZ Pendulum Effect",
            "Spell Normal",
            "Spell Field",
            "Spell Equip",
            "Spell Continuous",
            "Spell Quick-Play",
            "Spell Ritual",
            "Trap Normal",
            "Trap Continuous",
            "Trap Counter",
            "Others Skill Card",
            "Others Token");
    private static final List<String> DB_TYPES = Arrays.asList(
        "Aqua",
            "Beast",
            "Beast-Warrior",
            "Creator-God",
            "Cyberse",
            "Dinosaur",
            "Divine-Beast",
            "Dragon",
            "Fairy",
            "Fiend",
            "Fish",
            "Insect",
            "Illusionist",
            "Machine",
            "Plant",
            "Psychic",
            "Pyro",
            "Reptile",
            "Rock",
            "Sea Serpent",
            "Spellcaster",
            "Thunder",
            "Warrior",
            "Winged Beast",
            "Wyrm",
            "Zombie");

    public static void consume() {
        try {
            getData();
            processData();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void getData() throws IOException {
        File cardInfoFileEN = new File(DEFAULT_PATH + "cardinfo_en.json");
        File cardInfoFilePT = new File(DEFAULT_PATH + "cardinfo_pt.json");

        System.out.println("[DBG] Check if file in EN exists: " + cardInfoFileEN.exists());
        System.out.println("[DBG] Check if file in PT exists: " + cardInfoFilePT.exists());

        if (cardInfoFileEN.exists() && cardInfoFilePT.exists())
            return;

        System.out.println("[DBG] Some files do not exist. Data must be downloaded.");

        LocalDateTime now = LocalDateTime.now();
        String nowString = "" + now.getYear() + now.getMonthValue() + now.getDayOfMonth() + now.getHour()
                + now.getMinute();

        String filename = DEFAULT_PATH + "cardinfo_en" + nowString + ".json";
        downloadData(filename, "en");

        filename = DEFAULT_PATH + "cardinfo_pt" + nowString + ".json";
        downloadData(filename, "pt");
    }

    private static void downloadData(String filename, String language) throws IOException {
        File responseData = new File(filename);

        String requestURL = (language.equals("en") ? DEFAULT_URL : DEFAULT_URL + "?language=" + language);

        System.out.println("[DBG] Downloading data from " + requestURL);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(requestURL, HttpMethod.GET, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            String responseBody = response.getBody();

            System.out.println("[DBG] Writing dowloaded data in cardinfo files");

            // Escreve um arquivo único (para backup)
            FileWriter writer = new FileWriter(responseData);
            writer.write(responseBody);
            writer.close();

            // Escreve no arquivo padrão
            writer = new FileWriter(DEFAULT_PATH + "cardinfo_" + language + ".json");
            writer.write(responseBody);
            writer.close();
        } else {
            System.out.println("[DBG] The request failed. Status code: " + response.getStatusCode());
        }
    }

    private static void processData() throws JSONException, IOException {
        System.out.println("[DBG] Processing the data downloaded");

        // Read the data downloaded in DEFAULT_URL
        File cardinfoFile = new File(DEFAULT_PATH + "cardinfo_en.json");
        FileReader cardinfoReader = new FileReader(cardinfoFile);
        BufferedReader cardinfoBufferedReader = new BufferedReader(cardinfoReader);
        JSONObject cardinfoData = new JSONObject(cardinfoBufferedReader.readLine());
        cardinfoBufferedReader.close();

        JSONArray cards = cardinfoData.getJSONArray("data");

        // Open files to write the SQL code
        File ygoFile = new File(DEFAULT_PATH + "ygoprodeck_yugiohcard.sql");
        try (FileWriter ygoFileWriter = new FileWriter(ygoFile)) {
            
            ygoFileWriter.write("INSERT INTO yugioh_cards (id_ygoprodeck,name,category,type,attribute,level_rank_link,effect_lore_text,pendulum_scale,link_arrows,atk,def) VALUES \n");

            System.out.println("[DBG] Number of cards to be processed: " + cards.length());
            for (int i = 0; i < cards.length(); i++) {
                if (i % 100 == 0)
                    System.out.println("[DBG] Cards already processed: " + i);

                JSONObject card = cards.getJSONObject(i);

                String line;

                // YUGIOH_CARD TABLE
                String searchCategory;
                boolean isMonster = false;
                boolean isLink = false;
                String type = card.getString("type");
                String race = card.optString("race", null);
                int idRace = -1;

                Integer levelRankLink = null;
                String effectText = card.getString("desc");
                Integer pendulumScale = null;
                String linkArrows = null;
                if (type.contains("Monster")) {
                    isMonster = true;

                    type = type.replace(" Monster", "");
                    searchCategory = "Monster " + type;

                    idRace = DB_TYPES.indexOf(race);
                    if (idRace < 0)
                        throw new RuntimeException("Race id not found with " + race);
                    idRace++; // start with 1 not 0

                    if (type.contains("Link")) {
                        isLink = true;
                        levelRankLink = card.getInt("linkval");

                        JSONArray linkMarkers = card.getJSONArray("linkmarkers");
                        linkArrows = "'{";
                        for (int k = 0; k < linkMarkers.length(); k++) {
                            linkArrows += "\"";
                            switch (linkMarkers.getString(k)) {
                                case "Top":
                                    linkArrows += "N";
                                    break;
                                case "Top-Right":
                                    linkArrows += "NE";
                                    break;
                                case "Right":
                                    linkArrows += "E";
                                    break;
                                case "Bottom-Right":
                                    linkArrows += "SE";
                                    break;
                                case "Bottom":
                                    linkArrows += "S";
                                    break;
                                case "Bottom-Left":
                                    linkArrows += "SW";
                                    break;
                                case "Left":
                                    linkArrows += "W";
                                    break;
                                case "Top-Left":
                                    linkArrows += "NW";
                                    break;
                                default:
                                    break;
                            }
                            linkArrows += "\"";

                            if (k < linkMarkers.length() - 1) linkArrows += ",";
                        }
                        linkArrows += "}'";
                    } else {
                        levelRankLink = card.getInt("level");
                    }

                    if (type.contains("Pendulum")) {
                        pendulumScale = card.getInt("scale");
                    }

                } else if (type.contains("Spell")) {
                    searchCategory = "Spell " + race;
                } else if (type.contains("Trap")) {
                    searchCategory = "Trap " + race;
                } else {
                    searchCategory = "Others " + type;
                }

                int idCategory = DB_CATEGORIES.indexOf(searchCategory);
                if (idCategory < 0)
                    throw new RuntimeException("Category id not found with " + searchCategory);
                idCategory++; // start with 1 not 0

                line = "(";
                line += card.getLong("id") + ","; // id_ygoprodeck
                line += format(card.getString("name")) + ", "; // name
                line += idCategory + ", "; // category
                line += (isMonster ? idRace : "null") + ", "; // type
                line += (isMonster ? format(card.getString("attribute")) : "null") + ", "; // attribute
                line += levelRankLink + ", "; // level_rank_link
                line += format(effectText) + ", "; // effect_lore_text
                line += pendulumScale + ", "; // pendulum_scale
                line += linkArrows + ", "; // link_arrows
                line += (isMonster ? card.getInt("atk") : "null") + ", "; // atk
                line += (isMonster && !isLink ? card.getInt("def") : "null"); // def
                line += (i < cards.length() - 1) ? "),\n" : ");";
                ygoFileWriter.write(line);
            }
            ygoFileWriter.close();
        }
        System.out.println("[DBG] All cards have been processed ");
    }

    private static String format(String s) {
        if (s == null)
            return "null";
        return String.format("'%s'", s.replace("'", "''"));
    }
}
