package myusick.controller.dto;

/**
 * Created by david on 31/03/2015.
 */
public class GroupDTO {
    
    private int creator;
    private String name;
    private String year;
    private String description;

    public GroupDTO() {
    }

    public GroupDTO(int creator, String name, String year) {
        this.creator = creator;
        this.name = name;
        this.year = year;
    }

    public int getCreator() {
        return creator;
    }

    public void setCreator(int creator) {
        this.creator = creator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
