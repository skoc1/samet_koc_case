package utils;

import models.Pet;

import java.util.Arrays;
import java.util.Collections;

public class PetBuilder {

    private long id;
    private String name;
    private String status;
    private Pet.Category category;
    private java.util.List<String> photoUrls;

    public PetBuilder(long id, String name) {
        this.id = id;
        this.name = name;
        this.status = "available";
        this.category = new Pet.Category(1, "Dogs");
        this.photoUrls = Collections.singletonList("https://example.com/photo.jpg");
    }

    public PetBuilder status(String status) {
        this.status = status;
        return this;
    }

    public PetBuilder category(long id, String name) {
        this.category = new Pet.Category(id, name);
        return this;
    }

    public PetBuilder photoUrls(String... urls) {
        this.photoUrls = Arrays.asList(urls);
        return this;
    }



    public Pet build() {
        Pet pet = new Pet();
        pet.setId(id);
        pet.setName(name);
        pet.setStatus(status);
        pet.setCategory(category);
        pet.setPhotoUrls(photoUrls);

        return pet;
    }
}
