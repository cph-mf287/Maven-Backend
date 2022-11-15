package dtos;

import com.google.gson.annotations.SerializedName;

public class JokeDTO {
    String type;
    @SerializedName(value = "id")
    String id;
    @SerializedName(value = "value", alternate = "joke")
    String value;
    @SerializedName(value = "url")
    String url;
    /*@SerializedName(value = "created_at")
    String created;
    @SerializedName(value = "updated_at")
    String updated;*/
    //@SerializedName(value = "icon"/*, alternate = "icon_url"*/)
    String icon;
    /*@SerializedName(value = "status")
    String status;*/

    public void setType(String type) {
        this.type = type;
    }

    public void setUrl(String url) {
        this.url = url + id;
    }

    public void setIcon(String url) {
        this.icon = url;
    }
}
