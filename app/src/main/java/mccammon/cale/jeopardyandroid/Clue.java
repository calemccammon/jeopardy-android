package mccammon.cale.jeopardyandroid;

import android.support.annotation.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

//Clue objects created from the web API.
public class Clue {
    private String id;
    private String answer;
    private String question;
    private String value;
    private String airDate;
    private String createDate;
    private String updateDate;
    private String gameID;
    private String invalidCount;
    private Category category;

    //Get rid of unnecessary tags and characters in the clue's Answer.
    public String getAnswer() {
        String charsToReplace[] = {"<i>", "</i>", "\\", ")", "(", "\""};
        for(int i = 0; i < charsToReplace.length; i++) {
            if(answer.contains(charsToReplace[i])) {
                answer = answer.replace(charsToReplace[i], "");
            }
        }
        return this.answer;
    }

    public String getQuestion() {
        return this.question;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String input) {
        this.value = input;
    }

    public Category getCategory() {
        return this.category;
    }

    @Nullable
    public static Clue fromJson(JSONObject jsonObject) {
        Clue c = new Clue();
        // Deserialize json into object fields
        try {
            c.id = jsonObject.getString("id");
            c.answer = jsonObject.getString("answer");
            c.question = jsonObject.getString("question");
            c.value = jsonObject.getString("value");
            if(c.value == "null") {
                c.setValue("0");
            }
            c.airDate = jsonObject.getString("airdate");
            c.createDate = jsonObject.getString( "created_at");
            c.updateDate = jsonObject.getString("updated_at");
            c.gameID = jsonObject.getString("game_id");
            c.invalidCount = jsonObject.getString("invalid_count");
            String categoryString = jsonObject.getString("category");
            JSONObject categoryObject = new JSONObject(categoryString);
            c.category = Category.fromJson(categoryObject);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        // Return new object
        return c;
    }

    public static ArrayList<Clue> fromJson(JSONArray jsonArray) {
        JSONObject clueJson;
        ArrayList<Clue> clues = new ArrayList<Clue>(jsonArray.length());
        // Process each result in json array, decode and convert to business object
        for (int i=0; i < jsonArray.length(); i++) {
            try {
                clueJson = jsonArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            Clue clue = Clue.fromJson(clueJson);

            if (clue != null) {
                clues.add(clue);
            }
        }
        return clues;
    }
}
