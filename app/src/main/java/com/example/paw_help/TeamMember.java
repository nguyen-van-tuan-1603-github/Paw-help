package com.example.paw_help;

import com.google.gson.annotations.SerializedName;

public class TeamMember {
    @SerializedName("memberId")
    private int memberId;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("position")
    private String position;
    
    @SerializedName("description")
    private String description;
    
    @SerializedName("avatarUrl")
    private String avatarUrl;
    
    @SerializedName("displayOrder")
    private int displayOrder;
    
    // Legacy fields for backward compatibility
    private String role;
    private String team;
    private String email;
    private String phone;
    private int imageResId;

    // Default constructor for Gson
    public TeamMember() {}

    // Legacy constructor for backward compatibility
    public TeamMember(String name, String role, String position, String description,
                      String team, String email, String phone, int imageResId) {
        this.name = name;
        this.role = role;
        this.position = position;
        this.description = description;
        this.team = team;
        this.email = email;
        this.phone = phone;
        this.imageResId = imageResId;
    }

    // Getters
    public int getMemberId() { return memberId; }
    public String getName() { return name; }
    public String getRole() { return role != null ? role : position; } // Fallback to position
    public String getPosition() { return position; }
    public String getDescription() { return description; }
    public String getTeam() { return team != null ? team : ""; }
    public String getEmail() { return email != null ? email : ""; }
    public String getPhone() { return phone != null ? phone : ""; }
    public int getImageResId() { return imageResId; }
    public String getAvatarUrl() { return avatarUrl; }
    public int getDisplayOrder() { return displayOrder; }

    // Setters
    public void setMemberId(int memberId) { this.memberId = memberId; }
    public void setName(String name) { this.name = name; }
    public void setRole(String role) { this.role = role; }
    public void setPosition(String position) { this.position = position; }
    public void setDescription(String description) { this.description = description; }
    public void setTeam(String team) { this.team = team; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    public void setDisplayOrder(int displayOrder) { this.displayOrder = displayOrder; }
}


