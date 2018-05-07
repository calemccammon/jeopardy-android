package mccammon.cale.jeopardyandroid;

import android.support.annotation.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

//Category class allows for the creation of Category objects from the web API.
//Categories have: id, title, createDate, updateDate, and cluesCount.
public class Category {
    private String id;
    private String title;
    private String createDate;
    private String updateDate;
    private String cluesCount;

    public String getTitle() {
        return this.title;
    }

    @Nullable
    public static Category fromJson(JSONObject jsonObject) {
        Category category = new Category();
        // Deserialize json into object fields
        try {
            category.id = jsonObject.getString("id");
            category.title = jsonObject.getString("title");
            category.createDate = jsonObject.getString( "created_at");
            category.updateDate = jsonObject.getString("updated_at");
            category.cluesCount = jsonObject.getString("clues_count");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        // Return new object
        return category;
    }
}
