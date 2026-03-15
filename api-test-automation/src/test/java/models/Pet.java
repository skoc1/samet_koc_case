package models;

import java.util.List;

public class Pet {

    private long id;
    private Category category;
    private String name;
    private List<String> photoUrls;
    private String status;

    public Pet() {}

    public Pet(long id, String name, String status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<String> getPhotoUrls() { return photoUrls; }
    public void setPhotoUrls(List<String> photoUrls) { this.photoUrls = photoUrls; }


    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public static class Category {
        private long id;
        private String name;

        public Category() {}
        public Category(long id, String name) {
            this.id = id;
            this.name = name;
        }

        public long getId() { return id; }
        public void setId(long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }




}
