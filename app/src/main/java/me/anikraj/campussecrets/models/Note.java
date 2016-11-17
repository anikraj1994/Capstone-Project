package me.anikraj.campussecrets.models;

public class Note {
    String text;
    String type;
    String username;
    String useremail;
    Boolean anonymous;
    Long time;
    String imageurl;
    int likes;
    int comments;




    public Note() {
    }

    public Note(String text, String type){
        this.text = text;
        this.type = type;
    }

    public Note(String text, String type, String username, String useremail, Boolean anonymous, Long time, String imageurl, int likes, int comments) {
        this.text = text;
        this.type = type;
        this.username = username;
        this.useremail = useremail;
        this.anonymous = anonymous;
        this.time = time;
        this.imageurl = imageurl;
        this.likes = likes;
        this.comments = comments;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUseremail() {
        return useremail;
    }

    public void setUseremail(String useremail) {
        this.useremail = useremail;
    }

    public Boolean getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(Boolean anonymous) {
        this.anonymous = anonymous;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }
}
