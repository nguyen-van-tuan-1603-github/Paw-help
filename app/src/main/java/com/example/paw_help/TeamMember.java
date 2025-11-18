package com.example.paw_help;

public class TeamMember {
    private String name;
    private String role;
    private String position;
    private String description;
    private String team;
    private String email;
    private String phone;
    private int imageResId;

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
    public String getName() { return name; }
    public String getRole() { return role; }
    public String getPosition() { return position; }
    public String getDescription() { return description; }
    public String getTeam() { return team; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public int getImageResId() { return imageResId; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setRole(String role) { this.role = role; }
    public void setPosition(String position) { this.position = position; }
    public void setDescription(String description) { this.description = description; }
    public void setTeam(String team) { this.team = team; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
}


