package tests;

import io.restassured.RestAssured;
import models.Pet;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import utils.ApiConfig;
import utils.ApiConfig.SafeResponse;
import utils.PetBuilder;

public class PetCrudTest {

    private long petId;

    @BeforeClass
    public void init() {
        petId = System.currentTimeMillis();
    }

    @Test(priority = 1)
    public void createPet() {
        Pet pet = new PetBuilder(petId, "Rex")
                .status("available")
                .category(10, "Dogs")
                .build();

        SafeResponse sr = ApiConfig.safeExecute(() ->
                RestAssured.given()
                        .spec(ApiConfig.petSpec())
                        .body(pet)
                        .when()
                        .post()
                        .then()
                        .extract().response()
        );

        Assert.assertEquals(sr.statusCode(), 200);
        Assert.assertEquals(sr.jsonLong("id"), petId);
        Assert.assertEquals(sr.jsonString("name"), "Rex");
        Assert.assertEquals(sr.jsonString("status"), "available");
    }

    @Test(priority = 2, dependsOnMethods = "createPet")
    public void getPetById() {
        SafeResponse sr = ApiConfig.safeExecute(() ->
                RestAssured.given()
                        .spec(ApiConfig.petSpec())
                        .when()
                        .get("/{petId}", petId)
                        .then()
                        .extract().response()
        );

        Assert.assertEquals(sr.statusCode(), 200);
        Assert.assertEquals(sr.jsonLong("id"), petId);
        Assert.assertEquals(sr.jsonString("name"), "Rex");
    }

    @Test(priority = 3, dependsOnMethods = "createPet")
    public void updatePetViaPut() {
        Pet updated = new PetBuilder(petId, "Rex Updated")
                .status("sold")
                .category(10, "Dogs")
                .build();

        SafeResponse sr = ApiConfig.safeExecute(() ->
                RestAssured.given()
                        .spec(ApiConfig.petSpec())
                        .body(updated)
                        .when()
                        .put()
                        .then()
                        .extract().response()
        );

        Assert.assertEquals(sr.statusCode(), 200);
        Assert.assertEquals(sr.jsonString("name"), "Rex Updated");
        Assert.assertEquals(sr.jsonString("status"), "sold");
    }

    @Test(priority = 4, dependsOnMethods = "updatePetViaPut")
    public void verifyPutUpdateApplied() {
        SafeResponse sr = ApiConfig.safeExecute(() ->
                RestAssured.given()
                        .spec(ApiConfig.petSpec())
                        .when()
                        .get("/{petId}", petId)
                        .then()
                        .extract().response()
        );

        Assert.assertEquals(sr.statusCode(), 200);
        Assert.assertEquals(sr.jsonString("name"), "Rex Updated");
        Assert.assertEquals(sr.jsonString("status"), "sold");
    }

    @Test(priority = 5, dependsOnMethods = "verifyPutUpdateApplied")
    public void updatePetViaPostForm() {
        SafeResponse sr = ApiConfig.safeExecute(() ->
                RestAssured.given()
                        .spec(ApiConfig.petSpec())
                        .contentType("application/x-www-form-urlencoded")
                        .formParam("name", "Rex FormUpdated")
                        .formParam("status", "pending")
                        .when()
                        .post("/{petId}", petId)
                        .then()
                        .extract().response()
        );

        Assert.assertEquals(sr.statusCode(), 200);
    }

    @Test(priority = 6, dependsOnMethods = "updatePetViaPostForm")
    public void verifyPostFormUpdateApplied() {
        SafeResponse sr = ApiConfig.safeExecute(() ->
                RestAssured.given()
                        .spec(ApiConfig.petSpec())
                        .when()
                        .get("/{petId}", petId)
                        .then()
                        .extract().response()
        );

        Assert.assertEquals(sr.statusCode(), 200);
        Assert.assertEquals(sr.jsonString("name"), "Rex FormUpdated");
        Assert.assertEquals(sr.jsonString("status"), "pending");
    }

    @Test(priority = 7, dependsOnMethods = "createPet")
    public void findPetsByStatusAvailable() {
        SafeResponse sr = ApiConfig.safeExecute(() ->
                RestAssured.given()
                        .spec(ApiConfig.petSpec())
                        .queryParam("status", "available")
                        .when()
                        .get("/findByStatus")
                        .then()
                        .extract().response()
        );

        Assert.assertEquals(sr.statusCode(), 200);
        Assert.assertTrue(sr.response().jsonPath().getList("$").size() > 0);
    }

    @Test(priority = 8, dependsOnMethods = "createPet")
    public void findPetsByStatusSold() {
        SafeResponse sr = ApiConfig.safeExecute(() ->
                RestAssured.given()
                        .spec(ApiConfig.petSpec())
                        .queryParam("status", "sold")
                        .when()
                        .get("/findByStatus")
                        .then()
                        .extract().response()
        );

        Assert.assertEquals(sr.statusCode(), 200);
        Assert.assertTrue(sr.response().jsonPath().getList("$").size() > 0);
    }



    @Test(priority = 10, dependsOnMethods = "createPet")
    public void uploadPetImage() {
        SafeResponse sr = ApiConfig.safeExecute(() ->
                RestAssured.given()
                        .spec(ApiConfig.petSpec())
                        .contentType("multipart/form-data")
                        .multiPart("additionalMetadata", "test photo")
                        .multiPart("file", "dummy.txt", "fake image content".getBytes())
                        .when()
                        .post("/{petId}/uploadImage", petId)
                        .then()
                        .extract().response()
        );

        Assert.assertEquals(sr.statusCode(), 200);
        Assert.assertTrue(sr.jsonString("message").contains("additionalMetadata"));
    }

    @Test(priority = 20, dependsOnMethods = "verifyPostFormUpdateApplied")
    public void deletePet() {
        SafeResponse sr = ApiConfig.safeExecute(() ->
                RestAssured.given()
                        .spec(ApiConfig.petSpec())
                        .when()
                        .delete("/{petId}", petId)
                        .then()
                        .extract().response()
        );

        Assert.assertEquals(sr.statusCode(), 200);
    }

    @Test(priority = 21, dependsOnMethods = "deletePet")
    public void getDeletedPetReturns404() {
        SafeResponse sr = ApiConfig.safeExecute(() ->
                RestAssured.given()
                        .spec(ApiConfig.petSpec())
                        .when()
                        .get("/{petId}", petId)
                        .then()
                        .extract().response()
        );

        Assert.assertEquals(sr.statusCode(), 404);
    }

    @Test(priority = 30)
    public void getWithInvalidIdFormat() {
        SafeResponse sr = ApiConfig.safeExecute(() ->
                RestAssured.given()
                        .spec(ApiConfig.petSpec())
                        .when()
                        .get("/{petId}", "abc")
                        .then()
                        .extract().response()
        );

        int code = sr.statusCode();
        Assert.assertTrue(code == 400 || code == 404,
                "Beklenen 400/404 ama gelen: " + code);
    }

    @Test(priority = 31)
    public void getNonExistingPet() {
        SafeResponse sr = ApiConfig.safeExecute(() ->
                RestAssured.given()
                        .spec(ApiConfig.petSpec())
                        .when()
                        .get("/{petId}", 99999999999L)
                        .then()
                        .extract().response()
        );

        Assert.assertEquals(sr.statusCode(), 404);
    }

    @Test(priority = 32)
    public void getWithNegativeId() {
        SafeResponse sr = ApiConfig.safeExecute(() ->
                RestAssured.given()
                        .spec(ApiConfig.petSpec())
                        .when()
                        .get("/{petId}", -1)
                        .then()
                        .extract().response()
        );

        int code = sr.statusCode();
        Assert.assertTrue(code == 400 || code == 404,
                "Beklenen 400/404 ama gelen: " + code);
    }

    @Test(priority = 33)
    public void createPetWithBrokenJson() {
        SafeResponse sr = ApiConfig.safeExecute(() ->
                RestAssured.given()
                        .spec(ApiConfig.petSpec())
                        .body("{ this is not valid json at all }")
                        .when()
                        .post()
                        .then()
                        .extract().response()
        );

        int code = sr.statusCode();
        Assert.assertTrue(code == 400 || code == 405 || code == 500,
                "Beklenen 400/405/500 ama gelen: " + code);
    }

    @Test(priority = 34)
    public void createPetWithEmptyBody() {
        SafeResponse sr = ApiConfig.safeExecute(() ->
                RestAssured.given()
                        .spec(ApiConfig.petSpec())
                        .body("{}")
                        .when()
                        .post()
                        .then()
                        .extract().response()
        );

        int code = sr.statusCode();
        Assert.assertTrue(code == 200 || code == 400 || code == 405,
                "Beklenen 200/400/405 ama gelen: " + code);
    }

    @Test(priority = 35)
    public void createPetWithMissingNameField() {
        SafeResponse sr = ApiConfig.safeExecute(() ->
                RestAssured.given()
                        .spec(ApiConfig.petSpec())
                        .body("{\"status\": \"available\"}")
                        .when()
                        .post()
                        .then()
                        .extract().response()
        );

        int code = sr.statusCode();
        Assert.assertTrue(code == 200 || code == 400 || code == 405,
                "Beklenen 200/400/405 ama gelen: " + code);
    }

    @Test(priority = 36)
    public void updateWithWrongPayloadType() {
        SafeResponse sr = ApiConfig.safeExecute(() ->
                RestAssured.given()
                        .spec(ApiConfig.petSpec())
                        .body("{\"id\": \"not-a-number\", \"name\": 12345}")
                        .when()
                        .put()
                        .then()
                        .extract().response()
        );

        int code = sr.statusCode();
        Assert.assertTrue(code == 400 || code == 500,
                "Beklenen 400/500 ama gelen: " + code);
    }

    @Test(priority = 37)
    public void updateNonExistingPet() {
        Pet ghost = new PetBuilder(88888888888L, "Ghost")
                .status("available")
                .build();

        SafeResponse sr = ApiConfig.safeExecute(() ->
                RestAssured.given()
                        .spec(ApiConfig.petSpec())
                        .body(ghost)
                        .when()
                        .put()
                        .then()
                        .extract().response()
        );

        int code = sr.statusCode();
        Assert.assertTrue(code == 200 || code == 404,
                "Beklenen 200/404 ama gelen: " + code);
    }

    @Test(priority = 38)
    public void deleteWithInvalidId() {
        SafeResponse sr = ApiConfig.safeExecute(() ->
                RestAssured.given()
                        .spec(ApiConfig.petSpec())
                        .when()
                        .delete("/{petId}", "xyz")
                        .then()
                        .extract().response()
        );

        int code = sr.statusCode();
        Assert.assertTrue(code == 400 || code == 404,
                "Beklenen 400/404 ama gelen: " + code);
    }

    @Test(priority = 39)
    public void deleteNonExistingPet() {
        SafeResponse sr = ApiConfig.safeExecute(() ->
                RestAssured.given()
                        .spec(ApiConfig.petSpec())
                        .when()
                        .delete("/{petId}", 77777777777L)
                        .then()
                        .extract().response()
        );

        Assert.assertEquals(sr.statusCode(), 404);
    }

    @Test(priority = 40)
    public void findByInvalidStatus() {
        SafeResponse sr = ApiConfig.safeExecute(() ->
                RestAssured.given()
                        .spec(ApiConfig.petSpec())
                        .queryParam("status", "nonexistentstatus")
                        .when()
                        .get("/findByStatus")
                        .then()
                        .extract().response()
        );

        int code = sr.statusCode();
        Assert.assertTrue(code == 200 || code == 400,
                "Beklenen 200/400 ama gelen: " + code);
    }

    @Test(priority = 41)
    public void postFormUpdateNonExistingPet() {
        SafeResponse sr = ApiConfig.safeExecute(() ->
                RestAssured.given()
                        .spec(ApiConfig.petSpec())
                        .contentType("application/x-www-form-urlencoded")
                        .formParam("name", "DoesNotExist")
                        .formParam("status", "available")
                        .when()
                        .post("/{petId}", 66666666666L)
                        .then()
                        .extract().response()
        );

        int code = sr.statusCode();
        Assert.assertTrue(code == 404 || code == 200,
                "Beklenen 404/200 ama gelen: " + code);
    }

    @Test(priority = 42)
    public void uploadImageToNonExistingPet() {
        SafeResponse sr = ApiConfig.safeExecute(() ->
                RestAssured.given()
                        .spec(ApiConfig.petSpec())
                        .contentType("multipart/form-data")
                        .multiPart("additionalMetadata", "ghost photo")
                        .multiPart("file", "ghost.txt", "not real".getBytes())
                        .when()
                        .post("/{petId}/uploadImage", 55555555555L)
                        .then()
                        .extract().response()
        );

        int code = sr.statusCode();
        Assert.assertTrue(code == 200 || code == 404,
                "Beklenen 200/404 ama gelen: " + code);
    }
}
